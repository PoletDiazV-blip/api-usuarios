package com.ejemplo.usuarios.service.impl;

import com.ejemplo.usuarios.dto.LoginRequestDTO;
import com.ejemplo.usuarios.dto.RegisterRequestDTO;
import com.ejemplo.usuarios.dto.UserResponseDTO;
import com.ejemplo.usuarios.dto.UserUpdateDTO;
import com.ejemplo.usuarios.entity.Usuario;
import com.ejemplo.usuarios.exception.EmailAlreadyExistsException;
import com.ejemplo.usuarios.exception.InvalidCredentialsException;
import com.ejemplo.usuarios.exception.UserNotFoundException;
import com.ejemplo.usuarios.repository.UsuarioRepository;
import com.ejemplo.usuarios.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponseDTO registrarUsuario(RegisterRequestDTO registerRequest) {
        logger.info("Iniciando registro de usuario con email: {}", registerRequest.email());

        if (usuarioRepository.existsByEmail(registerRequest.email())) {
            logger.warn("Intento de registro con email duplicado: {}", registerRequest.email());
            throw new EmailAlreadyExistsException("El email ya esta registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(registerRequest.nombre());
        usuario.setEmail(registerRequest.email());
        usuario.setPassword(passwordEncoder.encode(registerRequest.password()));
        usuario.setEstado(true);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        logger.info("Usuario registrado exitosamente con ID: {}", usuarioGuardado.getId());

        return convertirADTO(usuarioGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public String loginUsuario(LoginRequestDTO loginRequest) {
        logger.info("Intento de login para email: {}", loginRequest.email());

        Usuario usuario = usuarioRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> {
                    logger.warn("Login fallido: email no encontrado");
                    return new InvalidCredentialsException("Credenciales invalidas");
                });

        if (!passwordEncoder.matches(loginRequest.password(), usuario.getPassword())) {
            logger.warn("Login fallido: contrasena incorrecta para email: {}", loginRequest.email());
            throw new InvalidCredentialsException("Credenciales invalidas");
        }

        if (!usuario.getEstado()) {
            logger.warn("Login fallido: usuario desactivado con email: {}", loginRequest.email());
            throw new InvalidCredentialsException("Cuenta desactivada");
        }

        logger.info("Login exitoso para usuario ID: {}", usuario.getId());
        return "Login exitoso";
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> obtenerTodosUsuarios() {
        logger.info("Obteniendo lista de todos los usuarios");

        return usuarioRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO obtenerUsuarioPorId(UUID id) {
        logger.info("Buscando usuario con ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Usuario no encontrado con ID: {}", id);
                    return new UserNotFoundException("Usuario no encontrado");
                });

        return convertirADTO(usuario);
    }

    @Override
    @Transactional
    public UserResponseDTO actualizarUsuario(UUID id, UserUpdateDTO updateRequest) {
        logger.info("Actualizando usuario con ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Usuario no encontrado para actualizar con ID: {}", id);
                    return new UserNotFoundException("Usuario no encontrado");
                });

        if (!usuario.getEmail().equals(updateRequest.email()) &&
                usuarioRepository.existsByEmail(updateRequest.email())) {
            logger.warn("Email duplicado en actualizacion: {}", updateRequest.email());
            throw new EmailAlreadyExistsException("El email ya esta registrado");
        }

        usuario.setNombre(updateRequest.nombre());
        usuario.setEmail(updateRequest.email());

        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        logger.info("Usuario actualizado exitosamente con ID: {}", id);

        return convertirADTO(usuarioActualizado);
    }

    @Override
    @Transactional
    public void eliminarUsuario(UUID id) {
        logger.info("Eliminando usuario con ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Usuario no encontrado para eliminar con ID: {}", id);
                    return new UserNotFoundException("Usuario no encontrado");
                });

        usuario.desactivar();
        usuarioRepository.save(usuario);
        logger.info("Usuario desactivado exitosamente con ID: {}", id);
    }

    private UserResponseDTO convertirADTO(Usuario usuario) {
        return new UserResponseDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getEstado()
        );
    }
}