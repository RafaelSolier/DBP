package com.example.proyecto.domain.service;

import com.example.proyecto.config.*;
import com.example.proyecto.domain.entity.Cliente;
import com.example.proyecto.domain.entity.Proveedor;
import com.example.proyecto.domain.entity.User;
import com.example.proyecto.domain.enums.Role;
import com.example.proyecto.dto.*;
import com.example.proyecto.email.events.WelcomeEmailEvent;
import com.example.proyecto.exception.ConflictException;
import com.example.proyecto.exception.ResourceNotFoundException;
import com.example.proyecto.exception.UnauthorizedException;
import com.example.proyecto.infrastructure.ClienteRepository;
import com.example.proyecto.infrastructure.ProveedorRepository;
import com.example.proyecto.infrastructure.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final ClienteRepository clienteRepository;
    private final ProveedorRepository proveedorRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final ApplicationEventPublisher eventPublisher;

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
        eventPublisher.publishEvent(new WelcomeEmailEvent(this, dto.getEmail(), dto.getNombre()));
        return new AuthResponseDto(token,c.getId());
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
        eventPublisher.publishEvent(new WelcomeEmailEvent(this, dto.getEmail(), dto.getNombre()));
        return new AuthResponseDto(token,p.getId());
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
        //realiza un if, para que verifique si el cliente o Proveedor y de esa manera exponer
        //el id correspondiente, esto se puede hacer mediante email-
        //return new AuthResponseDto(token);
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Correo no registrado"));

        if (user.getRoles().contains(Role.ROLE_CLIENTE)) {
            Cliente cliente = clienteRepository.findByEmail(user.getEmail())
                    .orElseThrow(() -> new UnauthorizedException("Cliente no encontrado"));
            return new AuthResponseDto(token, cliente.getId());
        } else if (user.getRoles().contains(Role.ROLE_PROVEEDOR)) {
            Proveedor proveedor = proveedorRepository.findByEmail(user.getEmail())
                    .orElseThrow(() -> new UnauthorizedException("Proveedor no encontrado"));
            return new AuthResponseDto(token, proveedor.getId());
        } else {
            throw new UnauthorizedException("Rol no válido");
        }
    }

    @Transactional
    public AuthMeDto getCurrentUserInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Set<String> roles = user.getRoles().stream()
                .map(Role::name)
                .collect(Collectors.toSet());

        if (user.getRoles().contains(Role.ROLE_CLIENTE)) {
            Cliente cliente = clienteRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

            AuthMeDto dto = new AuthMeDto();
            dto.setId(user.getId());
            dto.setNombre(cliente.getNombre());
            dto.setEmail(user.getEmail());
            dto.setTelefono(cliente.getTelefono());
            dto.setRole(roles);
            return dto;

        } else if (user.getRoles().contains(Role.ROLE_PROVEEDOR)) {
            Proveedor proveedor = proveedorRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));

            AuthMeDto dto = new AuthMeDto();
            dto.setId(user.getId());
            dto.setNombre(proveedor.getNombre());
            dto.setEmail(user.getEmail());
            dto.setTelefono(proveedor.getTelefono());
            dto.setRole(roles);
            return dto;
        } else {
            throw new UnauthorizedException("Tipo de usuario no válido");
        }
    }
}