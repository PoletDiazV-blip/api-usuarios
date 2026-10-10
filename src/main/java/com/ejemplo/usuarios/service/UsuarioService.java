package com.ejemplo.usuarios.service;

import com.ejemplo.usuarios.dto.LoginRequestDTO;
import com.ejemplo.usuarios.dto.RegisterRequestDTO;
import com.ejemplo.usuarios.dto.UserResponseDTO;
import com.ejemplo.usuarios.dto.UserUpdateDTO;

import java.util.List;
import java.util.UUID;

public interface UsuarioService {

    UserResponseDTO registrarUsuario(RegisterRequestDTO registerRequest);

    String loginUsuario(LoginRequestDTO loginRequest);

    List<UserResponseDTO> obtenerTodosUsuarios();

    UserResponseDTO obtenerUsuarioPorId(UUID id);

    UserResponseDTO actualizarUsuario(UUID id, UserUpdateDTO updateRequest);

    void eliminarUsuario(UUID id);
}