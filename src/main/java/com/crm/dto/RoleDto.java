package com.crm.dto;

import com.crm.model.Role;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色传输对象
 * 用于在API响应中安全地传输角色信息
 */
public class RoleDto {

    private Long id;
    private String name;
    private String description;
    private Integer level;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 无参构造函数
     */
    public RoleDto() {
    }

    /**
     * 全参构造函数
     */
    public RoleDto(Long id, String name, String description, Integer level, 
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.level = level;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 从Role实体转换为RoleDto
     */
    public static RoleDto fromEntity(Role role) {
        if (role == null) {
            return null;
        }
        return new RoleDto(
            role.getId(),
            role.getName(),
            role.getDescription(),
            role.getLevel(),
            role.getCreatedAt(),
            role.getUpdatedAt()
        );
    }

    /**
     * 批量转换Role集合为RoleDto集合
     */
    public static Set<RoleDto> fromEntities(Set<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(RoleDto::fromEntity)
                .collect(Collectors.toSet());
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
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

    @Override
    public String toString() {
        return "RoleDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", level=" + level +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
