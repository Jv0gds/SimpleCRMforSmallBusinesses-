package com.crm.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 角色实体类
 * 定义系统中的五类角色：访客、普通用户、销售代表、销售经理、管理员
 */
@Entity
@Table(name = "roles", indexes = {
    @Index(name = "idx_role_name", columnList = "name"),
    @Index(name = "idx_role_level", columnList = "level")
})
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT COMMENT '角色ID，主键自增'")
    private Long id;
    
    @Column(name = "name", nullable = false, unique = true, length = 50,
            columnDefinition = "VARCHAR(50) COMMENT '角色名称：GUEST/USER/SALES_REP/SALES_MANAGER/ADMIN'")
    private String name;
    
    @Column(name = "description", length = 200,
            columnDefinition = "VARCHAR(200) COMMENT '角色描述'")
    private String description;
    
    @Column(name = "level", nullable = false,
            columnDefinition = "INT COMMENT '权限级别：1-访客, 2-普通用户, 3-销售代表, 4-销售经理, 5-管理员'")
    private Integer level;
    
    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'")
    private LocalDateTime updatedAt;
    
    /**
     * 角色关联的用户集合（多对多关系）
     * 使用@JsonIgnoreProperties忽略User中的roles字段，防止循环引用
     */
    @ManyToMany(mappedBy = "roles")
    @JsonIgnoreProperties({"roles", "password"})
    private Set<User> users = new HashSet<>();
    
    /**
     * 角色关联的权限集合（多对多关系）
     * 使用@JsonIgnoreProperties忽略Permission中的roles字段，防止循环引用
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id", referencedColumnName = "id")
    )
    @JsonIgnoreProperties("roles")
    private Set<Permission> permissions = new HashSet<>();
    
    /**
     * JPA 要求的无参构造函数
     */
    public Role() {
    }
    
    /**
     * 创建角色时使用的构造函数
     */
    public Role(String name, String description, Integer level) {
        this.name = name;
        this.description = description;
        this.level = level;
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
    
    /**
     * 添加权限到角色
     */
    public void addPermission(Permission permission) {
        this.permissions.add(permission);
        permission.getRoles().add(this);
    }
    
    /**
     * 从角色移除权限
     */
    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
        permission.getRoles().remove(this);
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
    
    public Set<User> getUsers() {
        return users;
    }
    
    public void setUsers(Set<User> users) {
        this.users = users;
    }
    
    public Set<Permission> getPermissions() {
        return permissions;
    }
    
    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }
    
    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", level=" + level +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role role = (Role) o;
        return name != null && name.equals(role.getName());
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
