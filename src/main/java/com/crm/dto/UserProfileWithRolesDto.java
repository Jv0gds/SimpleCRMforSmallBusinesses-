package com.crm.dto;

import com.crm.model.User;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 包含角色信息的用户资料传输对象
 * 用于在API响应中返回用户信息及其关联的角色
 */
public class UserProfileWithRolesDto {

    private Long id;
    private String email;
    private String username;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<RoleDto> roles;

    /**
     * 无参构造函数
     */
    public UserProfileWithRolesDto() {
    }

    /**
     * 全参构造函数
     */
    public UserProfileWithRolesDto(Long id, String email, String username, Integer status,
                                   LocalDateTime createdAt, LocalDateTime updatedAt, Set<RoleDto> roles) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.roles = roles;
    }

    /**
     * 从User实体转换为UserProfileWithRolesDto
     */
    public static UserProfileWithRolesDto fromEntity(User user) {
        if (user == null) {
            return null;
        }
        return new UserProfileWithRolesDto(
            user.getId(),
            user.getEmail(),
            user.getUsername(),
            user.getStatus(),
            user.getCreatedAt(),
            user.getUpdatedAt(),
            RoleDto.fromEntities(user.getRoles())
        );
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<RoleDto> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleDto> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "UserProfileWithRolesDto{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", roles=" + roles +
                '}';
    }
}
