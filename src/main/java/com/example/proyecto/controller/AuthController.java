package com.example.proyecto.controller;

import com.example.proyecto.config.JwtTokenProvider;
import com.example.proyecto.domain.service.AuthService;
import com.example.proyecto.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/register/cliente")
    public ResponseEntity<AuthResponseDto> registerCliente(@Valid @RequestBody ClienteRequestDTO dto) {
        AuthResponseDto resp = authService.registerCliente(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PostMapping("/register/proveedor")
    public ResponseEntity<AuthResponseDto> registerProveedor(@Valid @RequestBody ProveedorRequestDto dto) {
        AuthResponseDto resp = authService.registerProveedor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginDTO dto) {
        AuthResponseDto resp = authService.login(dto);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/me")
    public ResponseEntity<AuthMeDto> getCurrentUser(Principal principal) {
        String email = principal.getName();
        AuthMeDto userInfo = authService.getCurrentUserInfo(email);
        return ResponseEntity.ok(userInfo);
    }
}