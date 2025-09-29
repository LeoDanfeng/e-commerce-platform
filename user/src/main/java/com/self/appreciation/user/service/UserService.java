// UserService.java
package com.self.appreciation.user.service;

import com.self.appreciation.user.entity.User;
import com.self.appreciation.user.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    User registerUser(String username, String password, String email, String phone);
    UserDTO getUserById(Long id);
    Page<UserDTO> getAllUsers(Pageable pageable);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    User findByUsername(String username);
}
