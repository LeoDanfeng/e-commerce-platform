// LoginResponse.java
package com.self.appreciation.user.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private UserDTO user;
}
