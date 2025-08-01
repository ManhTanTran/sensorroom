package com.example.sensorroom.service;

import com.example.sensorroom.dto.user.AuthenticationRequest;
import com.example.sensorroom.dto.user.AuthenticationResponse;
import com.example.sensorroom.dto.user.RegisterRequest;


public interface AuthenticationService {
    void register(RegisterRequest input) throws Exception;
    AuthenticationResponse login(AuthenticationRequest request);
}
