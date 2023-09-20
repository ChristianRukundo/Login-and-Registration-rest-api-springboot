package com.springboot.api.service;

import com.springboot.api.payload.LoginDto;
import com.springboot.api.payload.RegisterDto;

public interface AuthService {
    String login(LoginDto loginDto);

    String register(RegisterDto registerDto);
}
