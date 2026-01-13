package com.crm.service.impl;

import com.crm.dto.UpdateProfileRequest;
import com.crm.dto.UserProfileDto;
import com.crm.model.User;
import com.crm.repository.UserRepository;
import com.crm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User login(String email, String password) {
        // Find the user by email or throw an exception if not found
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // Check if the provided password matches the stored encoded password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // If credentials are valid, return the user object
        return user;
    }

    @Override
    public UserProfileDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        return convertToProfileDto(user);
    }

    @Override
    @Transactional
    public UserProfileDto updateUserProfile(Long userId, UpdateProfileRequest request) {
        // 查找用户
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 验证密码匹配
        if (request.shouldUpdatePassword() && !request.isPasswordsMatch()) {
            throw new IllegalArgumentException("两次输入的密码不一致");
        }
        
        // 检查用户名是否已被其他用户使用
        if (!user.getUsername().equals(request.getUsername())) {
            userRepository.findByUsername(request.getUsername())
                    .ifPresent(existingUser -> {
                        if (!existingUser.getId().equals(userId)) {
                            throw new IllegalArgumentException("用户名已被使用");
                        }
                    });
        }
        
        // 更新用户名
        user.setUsername(request.getUsername());
        
        // 如果提供了新密码，则更新密码
        if (request.shouldUpdatePassword()) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }
        
        // 保存更新
        User updatedUser = userRepository.save(user);
        
        return convertToProfileDto(updatedUser);
    }
    
    /**
     * 将User实体转换为UserProfileDto
     */
    private UserProfileDto convertToProfileDto(User user) {
        return new UserProfileDto(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
