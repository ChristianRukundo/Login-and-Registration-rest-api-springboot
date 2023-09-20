package com.springboot.api.service.impl;

import com.springboot.api.entity.Role;
import com.springboot.api.entity.User;
import com.springboot.api.exception.BlogAPIException;
import com.springboot.api.payload.LoginDto;
import com.springboot.api.payload.RegisterDto;
import com.springboot.api.repository.RoleRepository;
import com.springboot.api.repository.UserRepository;
import com.springboot.api.security.JwtTokenProvider;
import com.springboot.api.service.AuthService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;


    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public String login(LoginDto loginDto) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsernameOrEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtTokenProvider.generateToken(authentication);
    }

   @Transactional
    @Override
    public String register(RegisterDto registerDto) {
        // Check for username and email existence

        // Create a new user entity
        User user = new User();
        user.setName(registerDto.getName());
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        // Create a set of roles and add the "ROLE_USER" role
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new BlogAPIException(HttpStatus.NOT_FOUND, "Role not found"));
        roles.add(userRole);

        // Set the roles for the user
        user.setRoles(roles);

        // Save the user, which will update the user_roles table
        userRepository.save(user);

        return "User registered successfully!";
    }
}
