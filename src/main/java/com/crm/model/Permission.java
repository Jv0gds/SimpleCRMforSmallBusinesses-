package com.crm.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 权限实体类
 * 定义系统中的细粒度操作权限
 */
@Entity
@Table(name = "permissions", indexes = {
    @Index(name = "idx_permission_name", columnList = "name"),
    @Index(name = "idx_permission_resource", columnList = "resource")
})
public class Permission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT COMMENT '权限ID，主键自增'")
    private Long id;
    
    @Column(name = "name", nullable = false, unique = true, length = 100,
            columnDefinition = "VARCHAR(100) COMMENT '权限名称：如READ_CUSTOMER、EDIT_OPPORTUNITY'")
    private String name;
    
    @Column(name = "resource", nullable = false, length = 50,
            columnDefinition = "VARCHAR(50) COMMENT '资源类型：CUSTOMER/OPPORTUNITY/USER/REPORT/SYSTEM'")
    private String resource;
    
    @Column(name = "action", nullable = false, length = 50,
            columnDefinition = "VARCHAR(50) COMMENT '操作类型：READ/CREATE/UPDATE/DELETE/MANAGE'")
    private String action;
    
    @Column(name = "description", length = 200,
            columnDefinition = "VARCHAR(200) COMMENT '权限描述'")
    private String description;
    
    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'")
    private LocalDateTime updatedAt;
    
    /**
     * 权限关联的角色集合（多对多关系）
     * 使用@JsonIgnoreProperties忽略Role中的permissions和users字段，防止循环引用
     */
    @ManyToMany(mappedBy = "permissions")
    @JsonIgnoreProperties({"permissions", "users"})
    private Set<Role> roles = new HashSet<>();
    
    /**
     * JPA 要求的无参构造函数
     */
    public Permission() {
    }
    
    /**
     * 创建权限时使用的构造函数
     */
    public Permission(String name, String resource, String action, String description) {
        this.name = name;
        this.resource = resource;
        this.action = action;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 在持久化之前自动设置创建时间
     */
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    /**
     * 在更新之前自动设置更新时间
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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
    
    public String getResource() {
        return resource;
    }
    
    public void setResource(String resource) {
        this.resource = resource;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    public Set<Role> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    
    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", resource='" + resource + '\'' +
                ", action='" + action + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permission)) return false;
        Permission that = (Permission) o;
        return name != null && name.equals(that.getName());
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
