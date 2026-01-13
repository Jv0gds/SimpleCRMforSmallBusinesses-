package com.crm.repository;

import com.crm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问层接口
 * 继承 JpaRepository 提供基础的 CRUD 操作
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据邮箱查找用户
     * @param email 用户邮箱
     * @return 用户对象（如果存在）
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 检查邮箱是否已存在
     * @param email 用户邮箱
     * @return 如果邮箱已存在返回 true，否则返回 false
     */
    boolean existsByEmail(String email);
    
    /**
     * 根据邮箱和状态查找用户
     * @param email 用户邮箱
     * @param status 账户状态（1-正常，0-禁用）
     * @return 用户对象（如果存在）
     */
    Optional<User> findByEmailAndStatus(String email, Integer status);
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象（如果存在）
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 检查用户名是否已存在
     * @param username 用户名
     * @return 如果用户名已存在返回 true，否则返回 false
     */
    boolean existsByUsername(String username);
}
