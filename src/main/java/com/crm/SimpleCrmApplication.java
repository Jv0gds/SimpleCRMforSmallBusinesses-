package com.crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Simple CRM 应用主启动类
 * 
 * @SpringBootApplication 是一个组合注解，包含：
 * - @Configuration: 标识这是一个配置类
 * - @EnableAutoConfiguration: 启用 Spring Boot 的自动配置机制
 * - @ComponentScan: 自动扫描并加载符合条件的组件（如 @Component, @Service, @Repository, @Controller 等）
 */
@SpringBootApplication
public class SimpleCrmApplication {

    /**
     * 应用程序入口点
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(SimpleCrmApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  Simple CRM Application Started!");
        System.out.println("  访问地址: http://localhost:8080");
        System.out.println("========================================\n");
    }
}
