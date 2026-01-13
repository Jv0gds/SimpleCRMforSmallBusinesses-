package com.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 更新个人资料请求 DTO
 * 用于封装从客户端发来的个人资料更新请求的数据
 */
public class UpdateProfileRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 50, message = "用户名长度必须在2-50个字符之间")
    private String username;

    /**
     * 可选：新密码（如果用户想修改密码）
     */
    @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
    private String newPassword;

    /**
     * 可选：确认新密码
     */
    private String confirmPassword;

    /**
     * 无参构造函数
     */
    public UpdateProfileRequest() {
    }

    /**
     * 带用户名的构造函数
     */
    public UpdateProfileRequest(String username) {
        this.username = username;
    }

    /**
     * 全参构造函数
     */
    public UpdateProfileRequest(String username, String newPassword, String confirmPassword) {
        this.username = username;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    // Getters and Setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    /**
     * 检查新密码和确认密码是否匹配
     */
    public boolean isPasswordsMatch() {
        if (newPassword == null || confirmPassword == null) {
            return true; // 如果没有提供密码，则认为匹配（不修改密码）
        }
        return newPassword.equals(confirmPassword);
    }

    /**
     * 检查是否需要更新密码
     */
    public boolean shouldUpdatePassword() {
        return newPassword != null && !newPassword.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "UpdateProfileRequest{" +
                "username='" + username + '\'' +
                ", newPassword='" + (newPassword != null ? "[PROTECTED]" : "null") + '\'' +
                ", confirmPassword='" + (confirmPassword != null ? "[PROTECTED]" : "null") + '\'' +
                '}';
    }
}
