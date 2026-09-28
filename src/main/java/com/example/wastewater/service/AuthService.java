package com.example.wastewater.service;

import com.example.wastewater.dto.AuthRequest;
import com.example.wastewater.dto.AuthResponse;
import com.example.wastewater.entity.User;
import com.example.wastewater.exception.BadRequestException;
import com.example.wastewater.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("Invalid username or password"));

        if (!user.isActive()) {
            throw new BadRequestException("User account is disabled");
        }

        if (!user.getPassword().equals(request.getPassword())) {
            throw new BadRequestException("Invalid username or password");
        }

        return new AuthResponse(user.getUsername(), user.getFullName(), user.getRole(), "Login successful");
    }
}
