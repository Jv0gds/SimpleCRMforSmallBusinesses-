package com.crm.config;

import com.crm.model.Permission;
import com.crm.model.Role;
import com.crm.repository.PermissionRepository;
import com.crm.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色权限初始化器
 * 应用启动时自动初始化五类角色及其对应的权限映射
 * 
 * 角色体系：
 * - GUEST (访客, Level 1): 无权限
 * - USER (普通用户, Level 2): 个人信息管理
 * - SALES_REP (销售代表, Level 3): 个人客户、商机、报表管理
 * - SALES_MANAGER (销售经理, Level 4): 团队客户、商机、报表管理
 * - ADMIN (管理员, Level 5): 全部权限
 */
@Component
public class RolePermissionInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(RolePermissionInitializer.class);
    
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    
    public RolePermissionInitializer(RoleRepository roleRepository, 
                                    PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("=== 开始初始化角色权限数据 ===");
        
        // 1. 初始化权限
        Map<String, Permission> permissions = initializePermissions();
        logger.info("权限初始化完成，共 {} 个权限", permissions.size());
        
        // 2. 初始化角色
        Map<String, Role> roles = initializeRoles();
        logger.info("角色初始化完成，共 {} 个角色", roles.size());
        
        // 3. 分配权限给角色
        assignPermissionsToRoles(roles, permissions);
        logger.info("角色权限映射完成");
        
        logger.info("=== 角色权限数据初始化完成 ===");
    }
    
    /**
     * 初始化系统权限
     */
    private Map<String, Permission> initializePermissions() {
        Map<String, Permission> permissions = new HashMap<>();
        
        // 定义所有权限
        List<PermissionData> permissionDataList = Arrays.asList(
            // 个人信息权限
            new PermissionData("READ_PROFILE", "USER", "READ", "查看个人信息"),
            new PermissionData("UPDATE_PROFILE", "USER", "UPDATE", "编辑个人信息"),
            new PermissionData("MANAGE_USERS", "USER", "MANAGE", "管理所有用户"),
            
            // 客户管理权限
            new PermissionData("READ_OWN_CUSTOMER", "CUSTOMER", "READ", "查看自己的客户"),
            new PermissionData("CREATE_CUSTOMER", "CUSTOMER", "CREATE", "创建客户"),
            new PermissionData("UPDATE_OWN_CUSTOMER", "CUSTOMER", "UPDATE", "编辑自己的客户"),
            new PermissionData("DELETE_OWN_CUSTOMER", "CUSTOMER", "DELETE", "删除自己的客户"),
            new PermissionData("READ_TEAM_CUSTOMER", "CUSTOMER", "READ", "查看团队的客户"),
            new PermissionData("UPDATE_TEAM_CUSTOMER", "CUSTOMER", "UPDATE", "编辑团队的客户"),
            new PermissionData("MANAGE_ALL_CUSTOMERS", "CUSTOMER", "MANAGE", "管理所有客户"),
            
            // 商机管理权限
            new PermissionData("READ_OWN_OPPORTUNITY", "OPPORTUNITY", "READ", "查看自己的商机"),
            new PermissionData("CREATE_OPPORTUNITY", "OPPORTUNITY", "CREATE", "创建商机"),
            new PermissionData("UPDATE_OWN_OPPORTUNITY", "OPPORTUNITY", "UPDATE", "编辑自己的商机"),
            new PermissionData("DELETE_OWN_OPPORTUNITY", "OPPORTUNITY", "DELETE", "删除自己的商机"),
            new PermissionData("READ_TEAM_OPPORTUNITY", "OPPORTUNITY", "READ", "查看团队的商机"),
            new PermissionData("UPDATE_TEAM_OPPORTUNITY", "OPPORTUNITY", "UPDATE", "编辑团队的商机"),
            new PermissionData("MANAGE_ALL_OPPORTUNITIES", "OPPORTUNITY", "MANAGE", "管理所有商机"),
            
            // 报表权限
            new PermissionData("READ_OWN_REPORT", "REPORT", "READ", "查看个人报表"),
            new PermissionData("READ_TEAM_REPORT", "REPORT", "READ", "查看团队报表"),
            new PermissionData("READ_ALL_REPORTS", "REPORT", "READ", "查看所有报表"),
            
            // 系统配置权限
            new PermissionData("MANAGE_SYSTEM", "SYSTEM", "MANAGE", "管理系统配置")
        );
        
        // 创建或更新权限
        for (PermissionData data : permissionDataList) {
            Permission permission = permissionRepository.findByName(data.name)
                .orElseGet(() -> {
                    Permission newPermission = new Permission(
                        data.name, 
                        data.resource, 
                        data.action, 
                        data.description
                    );
                    logger.debug("创建新权限: {}", data.name);
                    return newPermission;
                });
            
            // 更新权限描述（如果有变更）
            permission.setDescription(data.description);
            permission = permissionRepository.save(permission);
            permissions.put(data.name, permission);
        }
        
        return permissions;
    }
    
    /**
     * 初始化系统角色
     */
    private Map<String, Role> initializeRoles() {
        Map<String, Role> roles = new HashMap<>();
        
        // 定义所有角色
        List<RoleData> roleDataList = Arrays.asList(
            new RoleData("GUEST", "访客 - 未登录用户，仅能访问公开页面", 1),
            new RoleData("USER", "普通用户 - 已注册用户，可查看和编辑个人信息", 2),
            new RoleData("SALES_REP", "销售代表 - 可管理自己的客户和商机", 3),
            new RoleData("SALES_MANAGER", "销售经理 - 可查看和管理团队的客户与商机", 4),
            new RoleData("ADMIN", "管理员 - 系统全部功能权限", 5)
        );
        
        // 创建或更新角色
        for (RoleData data : roleDataList) {
            Role role = roleRepository.findByName(data.name)
                .orElseGet(() -> {
                    Role newRole = new Role(data.name, data.description, data.level);
                    logger.debug("创建新角色: {}", data.name);
                    return newRole;
                });
            
            // 更新角色描述和级别（如果有变更）
            role.setDescription(data.description);
            role.setLevel(data.level);
            role = roleRepository.save(role);
            roles.put(data.name, role);
        }
        
        return roles;
    }
    
    /**
     * 为角色分配权限
     */
    private void assignPermissionsToRoles(Map<String, Role> roles, Map<String, Permission> permissions) {
        // 清空现有权限映射，重新分配
        for (Role role : roles.values()) {
            role.getPermissions().clear();
        }
        
        // 普通用户（USER）权限
        Role userRole = roles.get("USER");
        addPermissionsToRole(userRole, permissions, Arrays.asList(
            "READ_PROFILE", 
            "UPDATE_PROFILE"
        ));
        
        // 销售代表（SALES_REP）权限
        Role salesRepRole = roles.get("SALES_REP");
        addPermissionsToRole(salesRepRole, permissions, Arrays.asList(
            "READ_PROFILE", "UPDATE_PROFILE",
            "READ_OWN_CUSTOMER", "CREATE_CUSTOMER", "UPDATE_OWN_CUSTOMER", "DELETE_OWN_CUSTOMER",
            "READ_OWN_OPPORTUNITY", "CREATE_OPPORTUNITY", "UPDATE_OWN_OPPORTUNITY", "DELETE_OWN_OPPORTUNITY",
            "READ_OWN_REPORT"
        ));
        
        // 销售经理（SALES_MANAGER）权限
        Role salesManagerRole = roles.get("SALES_MANAGER");
        addPermissionsToRole(salesManagerRole, permissions, Arrays.asList(
            "READ_PROFILE", "UPDATE_PROFILE",
            "READ_OWN_CUSTOMER", "CREATE_CUSTOMER", "UPDATE_OWN_CUSTOMER", "DELETE_OWN_CUSTOMER",
            "READ_TEAM_CUSTOMER", "UPDATE_TEAM_CUSTOMER",
            "READ_OWN_OPPORTUNITY", "CREATE_OPPORTUNITY", "UPDATE_OWN_OPPORTUNITY", "DELETE_OWN_OPPORTUNITY",
            "READ_TEAM_OPPORTUNITY", "UPDATE_TEAM_OPPORTUNITY",
            "READ_OWN_REPORT", "READ_TEAM_REPORT"
        ));
        
        // 管理员（ADMIN）权限 - 拥有所有权限
        Role adminRole = roles.get("ADMIN");
        addPermissionsToRole(adminRole, permissions, new ArrayList<>(permissions.keySet()));
        
        // 保存所有角色（包含权限映射）
        roleRepository.saveAll(new ArrayList<>(roles.values()));
        
        logger.info("角色权限分配详情:");
        logger.info("  - USER: {} 个权限", userRole.getPermissions().size());
        logger.info("  - SALES_REP: {} 个权限", salesRepRole.getPermissions().size());
        logger.info("  - SALES_MANAGER: {} 个权限", salesManagerRole.getPermissions().size());
        logger.info("  - ADMIN: {} 个权限", adminRole.getPermissions().size());
    }
    
    /**
     * 为角色添加权限
     */
    private void addPermissionsToRole(Role role, Map<String, Permission> permissions, List<String> permissionNames) {
        for (String permissionName : permissionNames) {
            Permission permission = permissions.get(permissionName);
            if (permission != null) {
                role.getPermissions().add(permission);
            } else {
                logger.warn("权限 {} 不存在，无法分配给角色 {}", permissionName, role.getName());
            }
        }
    }
    
    /**
     * 权限数据内部类
     */
    private static class PermissionData {
        String name;
        String resource;
        String action;
        String description;
        
        PermissionData(String name, String resource, String action, String description) {
            this.name = name;
            this.resource = resource;
            this.action = action;
            this.description = description;
        }
    }
    
    /**
     * 角色数据内部类
     */
    private static class RoleData {
        String name;
        String description;
        Integer level;
        
        RoleData(String name, String description, Integer level) {
            this.name = name;
            this.description = description;
            this.level = level;
        }
    }
}
