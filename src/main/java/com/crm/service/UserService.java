package com.crm.service;

import com.crm.dto.UpdateProfileRequest;
import com.crm.dto.UserProfileDto;
import com.crm.model.User;

public interface UserService {
    User register(User user);
    User login(String email, String password);
    
    /**
     * 根据用户ID获取用户个人资料
     * @param userId 用户ID
     * @return 用户个人资料DTO
     */
    UserProfileDto getUserProfile(Long userId);
    
    /**
     * 更新用户个人资料
     * @param userId 用户ID
     * @param request 更新请求（包含用户名和可选的新密码）
     * @return 更新后的用户个人资料DTO
     */
    UserProfileDto updateUserProfile(Long userId, UpdateProfileRequest request);
    /**
     * 处理用户角色升级请求
     * @param userId 用户ID
     * @param newRoleName 新的角色名称
     */
    void requestRoleUpgrade(Long userId, String newRoleName);
}
