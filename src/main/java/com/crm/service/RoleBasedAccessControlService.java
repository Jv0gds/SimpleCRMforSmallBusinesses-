package com.crm.service;

import com.crm.model.Permission;
import com.crm.model.Role;
import com.crm.model.User;
import com.crm.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 基于角色的访问控制服务
 * 提供运行时权限验证和角色判断功能
 */
@Service
public class RoleBasedAccessControlService {

    private final UserRepository userRepository;

    public RoleBasedAccessControlService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 获取当前登录用户
     * 
     * @return 当前用户对象，如果未登录返回null
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() 
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        String email = authentication.getName();
        Optional<User> userOptional = userRepository.findByEmail(email);
        
        return userOptional.orElse(null);
    }

    /**
     * 检查当前用户是否拥有指定角色
     * 
     * @param roleName 角色名称（如：ADMIN, SALES_MANAGER, SALES_REP, USER, GUEST）
     * @return 如果拥有该角色返回true，否则返回false
     */
    public boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String roleWithPrefix = "ROLE_" + roleName;
        
        return authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals(roleWithPrefix));
    }

    /**
     * 检查当前用户是否拥有任一指定角色
     * 
     * @param roleNames 角色名称数组
     * @return 如果拥有任一角色返回true，否则返回false
     */
    public boolean hasAnyRole(String... roleNames) {
        for (String roleName : roleNames) {
            if (hasRole(roleName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查当前用户是否拥有所有指定角色
     * 
     * @param roleNames 角色名称数组
     * @return 如果拥有所有角色返回true，否则返回false
     */
    public boolean hasAllRoles(String... roleNames) {
        for (String roleName : roleNames) {
            if (!hasRole(roleName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查当前用户是否拥有指定权限
     * 
     * @param permissionName 权限名称（如：READ_CUSTOMER, EDIT_OPPORTUNITY）
     * @return 如果拥有该权限返回true，否则返回false
     */
    public boolean hasPermission(String permissionName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        return authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals(permissionName));
    }

    /**
     * 检查当前用户是否拥有对指定资源的操作权限
     * 
     * @param resource 资源类型（如：CUSTOMER, OPPORTUNITY, USER）
     * @param action 操作类型（如：READ, CREATE, UPDATE, DELETE）
     * @return 如果拥有权限返回true，否则返回false
     */
    public boolean hasPermission(String resource, String action) {
        String permissionName = action + "_" + resource;
        return hasPermission(permissionName);
    }

    /**
     * 检查当前用户的角色级别是否大于或等于指定级别
     * 角色级别：5-管理员, 4-经理, 3-代表, 2-用户, 1-访客
     * 
     * @param minLevel 最小角色级别
     * @return 如果角色级别满足要求返回true，否则返回false
     */
    public boolean hasMinimumRoleLevel(int minLevel) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }

        Set<Role> roles = currentUser.getRoles();
        if (roles == null || roles.isEmpty()) {
            return false;
        }

        // 获取用户的最高角色级别
        int maxLevel = roles.stream()
                .mapToInt(Role::getLevel)
                .max()
                .orElse(0);

        return maxLevel >= minLevel;
    }

    /**
     * 检查当前用户是否可以访问指定用户的数据
     * 权限规则：
     * - 管理员可以访问所有用户数据
     * - 销售经理可以访问团队成员数据
     * - 销售代表和普通用户只能访问自己的数据
     * 
     * @param targetUserId 目标用户ID
     * @return 如果可以访问返回true，否则返回false
     */
    public boolean canAccessUserData(Long targetUserId) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }

        // 管理员可以访问所有数据
        if (hasRole("ADMIN")) {
            return true;
        }

        // 用户可以访问自己的数据
        if (currentUser.getId().equals(targetUserId)) {
            return true;
        }

        // 销售经理可以访问团队成员数据（假设有团队关系表，此处简化处理）
        if (hasRole("SALES_MANAGER")) {
            // 这里需要查询团队关系表，判断targetUserId是否在当前用户的团队中
            // 暂时返回false，待团队功能实现后完善
            return false;
        }

        return false;
    }

    /**
     * 获取当前用户的所有角色名称
     * 
     * @return 角色名称集合
     */
    public Set<String> getCurrentUserRoles() {
        User currentUser = getCurrentUser();
        if (currentUser == null || currentUser.getRoles() == null) {
            return Set.of();
        }

        return currentUser.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    /**
     * 获取当前用户的所有权限名称
     * 
     * @return 权限名称集合
     */
    public Set<String> getCurrentUserPermissions() {
        User currentUser = getCurrentUser();
        if (currentUser == null || currentUser.getRoles() == null) {
            return Set.of();
        }

        return currentUser.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());
    }

    /**
     * 检查用户是否为管理员
     * 
     * @return 如果是管理员返回true，否则返回false
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * 检查用户是否为销售经理
     * 
     * @return 如果是销售经理返回true，否则返回false
     */
    public boolean isSalesManager() {
        return hasRole("SALES_MANAGER");
    }

    /**
     * 检查用户是否为销售代表
     * 
     * @return 如果是销售代表返回true，否则返回false
     */
    public boolean isSalesRep() {
        return hasRole("SALES_REP");
    }

    /**
     * 检查用户是否为普通用户
     * 
     * @return 如果是普通用户返回true，否则返回false
     */
    public boolean isUser() {
        return hasRole("USER");
    }

    /**
     * 检查用户是否为访客
     * 
     * @return 如果是访客返回true，否则返回false
     */
    public boolean isGuest() {
        return hasRole("GUEST");
    }
}
