package com.crm.repository;

import com.crm.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * 角色数据访问接口
 * 提供角色的CRUD操作和自定义查询
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * 根据角色名称查找角色
     * @param name 角色名称（如：ADMIN, USER, SALES_REP等）
     * @return 角色对象
     */
    Optional<Role> findByName(String name);
    
    /**
     * 检查角色名称是否存在
     * @param name 角色名称
     * @return 是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 根据权限级别查找角色
     * @param level 权限级别
     * @return 角色列表
     */
    Set<Role> findByLevel(Integer level);
    
    /**
     * 查找权限级别大于等于指定级别的所有角色
     * @param level 最低权限级别
     * @return 角色列表
     */
    @Query("SELECT r FROM Role r WHERE r.level >= :level ORDER BY r.level ASC")
    Set<Role> findByLevelGreaterThanEqual(Integer level);
    
    /**
     * 查找权限级别小于等于指定级别的所有角色
     * @param level 最高权限级别
     * @return 角色列表
     */
    @Query("SELECT r FROM Role r WHERE r.level <= :level ORDER BY r.level ASC")
    Set<Role> findByLevelLessThanEqual(Integer level);
}
