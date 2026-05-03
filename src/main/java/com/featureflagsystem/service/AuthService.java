package com.featureflagsystem.service;

import com.featureflagsystem.dto.AuthRequest;
import com.featureflagsystem.dto.AuthResponse;
import com.featureflagsystem.dto.RegisterRequest;
import com.featureflagsystem.exception.DuplicateResourceException;
import com.featureflagsystem.exception.ResourceNotFoundException;
import com.featureflagsystem.model.Role;
import com.featureflagsystem.model.User;
import com.featureflagsystem.repository.UserRepository;
import com.featureflagsystem.utils.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authenticationManager, CustomUserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Username already exists");
        }

        Role role = Role.USER;
        if (request.getRole() != null && request.getRole().equalsIgnoreCase("ADMIN")) {
            role = Role.ADMIN;
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();
                
        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(userDetails, user.getRole().name());

        return new AuthResponse(token, user.getUsername(), user.getRole().name());
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(userDetails, user.getRole().name());

        return new AuthResponse(token, user.getUsername(), user.getRole().name());
    }
}
