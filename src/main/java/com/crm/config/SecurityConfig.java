package com.crm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security安全配置
 * 实现基于角色的访问控制(RBAC)和URL权限拦截
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    private final LoginSuccessHandler loginSuccessHandler;

    public SecurityConfig(LoginSuccessHandler loginSuccessHandler) {
        this.loginSuccessHandler = loginSuccessHandler;
    }

    /**
     * 密码加密器Bean
     * 使用BCrypt算法加密用户密码
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Security过滤器链配置
     * 定义URL访问权限规则和登录/登出行为
     *
     * 权限策略：
     * - 公开资源：登录/注册API、静态资源、HTML页面
     * - 管理员专属：/admin/** 路径
     * - 销售经理专属：/manager/** 路径
     * - 销售代表专属：/sales/** 路径
     * - 已认证用户：/api/** 路径（除公开API外）
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF（开发阶段，生产环境建议启用）
            .csrf(csrf -> csrf.disable())
            
            // 配置URL访问权限
            .authorizeHttpRequests(authorize -> authorize
                // 公开资源：无需认证
                .requestMatchers(
                    "/api/register",        // 注册接口
                    "/api/login",           // 登录接口
                    "/login.html",          // 登录页面
                    "/register.html",       // 注册页面
                    "/css/**",              // CSS样式
                    "/js/**",               // JavaScript脚本
                    "/images/**",           // 图片资源
                    "/favicon.ico",         // 网站图标
                    "/error"                // 错误页面
                ).permitAll()
                
                // 管理员专属路径（需要ADMIN角色）
                .requestMatchers("/admin/**", "/api/admin/**")
                    .hasRole("ADMIN")
                
                // 销售经理专属路径（需要SALES_MANAGER或ADMIN角色）
                .requestMatchers("/manager/**", "/api/manager/**")
                    .hasAnyRole("SALES_MANAGER", "ADMIN")
                
                // 销售代表专属路径（需要SALES_REP、SALES_MANAGER或ADMIN角色）
                .requestMatchers("/sales/**", "/api/sales/**")
                    .hasAnyRole("SALES_REP", "SALES_MANAGER", "ADMIN")
                
                // 个人页面：所有已认证用户可访问
                .requestMatchers("/profile.html", "/api/profile/**")
                    .authenticated()
                
                // 其他API请求：需要认证
                .requestMatchers("/api/**")
                    .authenticated()
                
                // 其他所有请求：需要认证
                .anyRequest().authenticated()
            )
            
            // 配置表单登录
            .formLogin(form -> form
                .loginPage("/login.html")                    // 自定义登录页面
                .loginProcessingUrl("/api/login")            // 登录处理URL
                .usernameParameter("email")                  // 用户名参数（使用邮箱）
                .passwordParameter("password")               // 密码参数
                .successHandler(loginSuccessHandler)         // 登录成功处理器（根据角色跳转）
                .failureUrl("/login.html?error=true")       // 登录失败跳转URL
                .permitAll()
            )
            
            // 配置登出
            .logout(logout -> logout
                .logoutUrl("/api/logout")                    // 登出URL
                .logoutSuccessUrl("/login.html?logout=true") // 登出成功跳转URL
                .invalidateHttpSession(true)                 // 使session失效
                .deleteCookies("JSESSIONID")                 // 删除session cookie
                .permitAll()
            )
            
            // 配置异常处理
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/403.html")               // 无权访问时跳转的页面
            );
            
        return http.build();
    }
}
