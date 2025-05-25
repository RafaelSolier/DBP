package com.example.proyecto.domain.service;

import com.example.proyecto.config.*;
import com.example.proyecto.domain.entity.Cliente;
import com.example.proyecto.domain.entity.Proveedor;
import com.example.proyecto.domain.entity.User;
import com.example.proyecto.domain.enums.Role;
import com.example.proyecto.dto.AuthResponseDto;
import com.example.proyecto.dto.ClienteRequestDTO;
import com.example.proyecto.dto.LoginDTO;
import com.example.proyecto.dto.ProveedorRequestDto;
import com.example.proyecto.exception.ConflictException;
import com.example.proyecto.exception.UnauthorizedException;
import com.example.proyecto.infrastructure.ClienteRepository;
import com.example.proyecto.infrastructure.ProveedorRepository;
import com.example.proyecto.infrastructure.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final ClienteRepository clienteRepository;
    private final ProveedorRepository proveedorRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public AuthResponseDto registerCliente(ClienteRequestDTO dto) {
        // verificar si el email ya existe
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Correo registrado");
        }
        // crear User
        User u = new User();
        u.setEmail(dto.getEmail());
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        u.getRoles().add(Role.ROLE_CLIENTE);
        userRepository.save(u);
        // crear perfil Cliente
        Cliente c = new Cliente();
        c.setNombre(dto.getNombre());
        c.setApellido(dto.getApellido());
        c.setTelefono(dto.getTelefono());
        c.setUser(u);
        clienteRepository.save(c);
        // autenticar y generar token
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );
        String token = tokenProvider.generateToken(auth);
        return new AuthResponseDto(token);
    }

    @Transactional
    public AuthResponseDto registerProveedor(ProveedorRequestDto dto) {
        // verificar si el email ya existe
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Correo registrado");
        }
        // crear User
        User u = new User();
        u.setEmail(dto.getEmail());
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        u.getRoles().add(Role.ROLE_PROVEEDOR);
        userRepository.save(u);
        Proveedor p = new Proveedor();
        p.setNombre(dto.getNombre());
        p.setDescripcion(dto.getDescripcion());
        p.setTelefono(dto.getTelefono());
        p.setUser(u);
        proveedorRepository.save(p);
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );
        String token = tokenProvider.generateToken(auth);
        return new AuthResponseDto(token);
    }
    @Transactional
    public AuthResponseDto login(LoginDTO dto) {
        // verificar si el email ya existe, caso contrario lanzar exception
        if (!userRepository.existsByEmail(dto.getEmail())) {
            throw new UnauthorizedException("Correo no registrado");
        }
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );
        String token = tokenProvider.generateToken(auth);
        return new AuthResponseDto(token);
    }
}