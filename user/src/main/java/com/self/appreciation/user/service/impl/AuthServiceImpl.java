// AuthServiceImpl.java
package com.self.appreciation.user.service.impl;

import com.self.appreciation.user.entity.User;
import com.self.appreciation.user.repository.UserRepository;
import com.self.appreciation.user.service.AuthService;
import com.self.appreciation.user.service.UserService;
import com.self.appreciation.user.dto.LoginRequest;
import com.self.appreciation.user.dto.LoginResponse;
import com.self.appreciation.user.dto.RegisterRequest;
import com.self.appreciation.user.dto.UserDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserService userService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean register(RegisterRequest registerRequest) {
        try {
            userService.registerUser(
                    registerRequest.getUsername(),
                    registerRequest.getPassword(),
                    registerRequest.getEmail(),
                    registerRequest.getPhone()
            );
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userService.findByUsername(loginRequest.getUsername());

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已被禁用");
        }

        // 生成token（实际项目中建议使用JWT）
        String token = UUID.randomUUID().toString().replace("-", "");

        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUser(userDTO);

        return response;
    }

    @Override
    public void logout(String token) {
        // 在实际项目中，需要将token加入黑名单或从缓存中移除
    }
}
