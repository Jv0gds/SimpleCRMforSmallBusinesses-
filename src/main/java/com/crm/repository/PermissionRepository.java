package com.crm.repository;

import com.crm.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * 权限数据访问接口
 * 提供权限的CRUD操作和自定义查询
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    /**
     * 根据权限名称查找权限
     * @param name 权限名称（如：READ_CUSTOMER, EDIT_OPPORTUNITY等）
     * @return 权限对象
     */
    Optional<Permission> findByName(String name);
    
    /**
     * 检查权限名称是否存在
     * @param name 权限名称
     * @return 是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 根据资源类型查找权限
     * @param resource 资源类型（如：CUSTOMER, OPPORTUNITY, USER等）
     * @return 权限列表
     */
    Set<Permission> findByResource(String resource);
    
    /**
     * 根据操作类型查找权限
     * @param action 操作类型（如：READ, CREATE, UPDATE, DELETE等）
     * @return 权限列表
     */
    Set<Permission> findByAction(String action);
    
    /**
     * 根据资源类型和操作类型查找权限
     * @param resource 资源类型
     * @param action 操作类型
     * @return 权限对象
     */
    Optional<Permission> findByResourceAndAction(String resource, String action);
    
    /**
     * 查找指定角色的所有权限
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Query("SELECT p FROM Permission p JOIN p.roles r WHERE r.id = :roleId")
    Set<Permission> findByRoleId(Long roleId);
    
    /**
     * 查找指定角色名称的所有权限
     * @param roleName 角色名称
     * @return 权限列表
     */
    @Query("SELECT p FROM Permission p JOIN p.roles r WHERE r.name = :roleName")
    Set<Permission> findByRoleName(String roleName);
}
