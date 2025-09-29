// AuthService.java
package com.self.appreciation.user.service;

import com.self.appreciation.user.dto.LoginRequest;
import com.self.appreciation.user.dto.LoginResponse;
import com.self.appreciation.user.dto.RegisterRequest;

public interface AuthService {
    boolean register(RegisterRequest registerRequest);
    LoginResponse login(LoginRequest loginRequest);
    void logout(String token);
}
