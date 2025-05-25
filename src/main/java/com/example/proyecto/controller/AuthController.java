package com.example.proyecto.controller;

import com.example.proyecto.domain.service.AuthService;
import com.example.proyecto.dto.AuthResponseDto;
import com.example.proyecto.dto.ClienteRequestDTO;
import com.example.proyecto.dto.LoginDTO;
import com.example.proyecto.dto.ProveedorRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

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
}