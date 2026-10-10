package com.ejemplo.usuarios.controller;

import com.ejemplo.usuarios.dto.UserResponseDTO;
import com.ejemplo.usuarios.dto.UserUpdateDTO;
import com.ejemplo.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UsuarioService usuarioService;

    public UserController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> obtenerTodosUsuarios() {
        logger.info("Peticion GET recibida en /api/v1/users");

        List<UserResponseDTO> usuarios = usuarioService.obtenerTodosUsuarios();

        logger.info("Retornando {} usuarios", usuarios.size());
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> obtenerUsuarioPorId(@PathVariable UUID id) {
        logger.info("Peticion GET recibida en /api/v1/users/{}", id);

        UserResponseDTO usuario = usuarioService.obtenerUsuarioPorId(id);

        logger.info("Usuario encontrado con ID: {}", id);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> actualizarUsuario(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateDTO updateRequest) {

        logger.info("Peticion PUT recibida en /api/v1/users/{}", id);

        UserResponseDTO usuarioActualizado = usuarioService.actualizarUsuario(id, updateRequest);

        logger.info("Usuario actualizado exitosamente con ID: {}", id);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable UUID id) {
        logger.info("Peticion DELETE recibida en /api/v1/users/{}", id);

        usuarioService.eliminarUsuario(id);

        logger.info("Usuario desactivado exitosamente con ID: {}", id);
        return ResponseEntity.ok("Usuario desactivado exitosamente");
    }
}