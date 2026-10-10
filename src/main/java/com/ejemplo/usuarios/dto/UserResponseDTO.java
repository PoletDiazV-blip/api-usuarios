package com.ejemplo.usuarios.dto;

import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String nombre,
        String email,
        Boolean estado
) {
}