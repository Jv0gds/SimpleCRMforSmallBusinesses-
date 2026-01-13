package com.crm.service;

import com.crm.model.User;

public interface UserService {
    User register(User user);
    User login(String email, String password);
}
