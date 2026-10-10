package com.ejemplo.usuarios.controller;

import com.ejemplo.usuarios.dto.LoginRequestDTO;
import com.ejemplo.usuarios.dto.RegisterRequestDTO;
import com.ejemplo.usuarios.dto.UserResponseDTO;
import com.ejemplo.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registrarUsuario(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        logger.info("Peticion POST recibida en /api/v1/auth/register");

        UserResponseDTO usuarioCreado = usuarioService.registrarUsuario(registerRequest);

        logger.info("Usuario registrado exitosamente con ID: {}", usuarioCreado.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCreado);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUsuario(@Valid @RequestBody LoginRequestDTO loginRequest) {
        logger.info("Peticion POST recibida en /api/v1/auth/login");

        String mensaje = usuarioService.loginUsuario(loginRequest);

        logger.info("Login exitoso para email: {}", loginRequest.email());
        return ResponseEntity.ok(mensaje);
    }
}