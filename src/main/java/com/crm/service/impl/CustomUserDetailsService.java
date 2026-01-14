package com.crm.service.impl;

import com.crm.model.Permission;
import com.crm.model.Role;
import com.crm.model.User;
import com.crm.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 自定义用户详情服务
 * 实现Spring Security的UserDetailsService接口，用于加载用户认证信息及角色权限
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 根据用户名（邮箱）加载用户信息
     * Spring Security会在用户登录时调用此方法
     * 
     * @param email 用户邮箱（作为用户名）
     * @return UserDetails 包含用户认证信息和权限的对象
     * @throws UsernameNotFoundException 如果用户不存在
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 从数据库查询用户，包括关联的角色和权限（通过EAGER加载）
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在：" + email));

        // 检查账户状态
        if (!user.isActive()) {
            throw new UsernameNotFoundException("账户已被禁用：" + email);
        }

        // 构建Spring Security的UserDetails对象
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(getAuthorities(user))
                .accountExpired(false)
                .accountLocked(!user.isActive())
                .credentialsExpired(false)
                .disabled(!user.isActive())
                .build();
    }

    /**
     * 获取用户的所有权限（角色 + 权限）
     * 权限格式：
     * - 角色：ROLE_ADMIN, ROLE_SALES_MANAGER, ROLE_SALES_REP, ROLE_USER, ROLE_GUEST
     * - 权限：READ_CUSTOMER, EDIT_OPPORTUNITY, CREATE_USER 等
     * 
     * @param user 用户对象
     * @return 权限集合
     */
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // 获取用户的所有角色
        Set<Role> roles = user.getRoles();
        
        if (roles != null && !roles.isEmpty()) {
            // 添加角色权限（格式：ROLE_XXX）
            for (Role role : roles) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
                
                // 添加角色关联的细粒度权限
                Set<Permission> permissions = role.getPermissions();
                if (permissions != null) {
                    authorities.addAll(permissions.stream()
                            .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                            .collect(Collectors.toSet()));
                }
            }
        } else {
            // 如果用户没有角色，默认赋予GUEST角色
            authorities.add(new SimpleGrantedAuthority("ROLE_GUEST"));
        }

        return authorities;
    }

    /**
     * 获取用户的最高级别角色
     * 用于登录后的路由跳转决策
     * 
     * @param user 用户对象
     * @return 最高级别的角色，如果没有角色则返回null
     */
    public Role getHighestRole(User user) {
        return user.getRoles().stream()
                .max((r1, r2) -> Integer.compare(r1.getLevel(), r2.getLevel()))
                .orElse(null);
    }
}
