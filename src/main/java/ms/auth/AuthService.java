package ms.auth;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ms.dto.UserDto;
import ms.entities.Role;
import ms.entities.User;
import ms.error.PrimaryKeyViolationException;
import ms.jwt.JwtService;
import ms.mapper.UserMapper;
import ms.services.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {
        try {
            log.info("Iniciando login para: " + request.getUsername());

            // Autenticar al usuario
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // Recuperar el usuario por el username
            Optional<User> optionalUser = userService.findByUsername(request.getUsername());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                UserDto userDto = UserMapper.mapToDto(user);
                // Generar token JWT
                String token = jwtService.generateToken(user);
                userDto.setToken(token);
                userDto.setIsActive(true);

                userService.updateUser(userDto);

                return AuthResponse.builder()
                        .token(token)
                        .code(200)
                        .message("Usuario autenticado exitosamente")
                        .build();
            } else {
                log.warn("Usuario no encontrado: " + request.getUsername());
                return AuthResponse.builder()
                        .code(403)
                        .message("Usuario no encontrado")
                        .build();
            }
        } catch (AuthenticationException ex) {
            log.error("Error de autenticación: " + ex.getMessage());
            return AuthResponse.builder()
                    .code(403)
                    .message("Error de autenticación: " + ex.getMessage())
                    .build();
        }
    }

    public AuthResponse register(RegisterRequest request) {
        try {
            // Verificar si el usuario ya existe por email o username
            Optional<User> existingUser = userService.findByEmail(request.getUsername());

            if (existingUser.isPresent()) {
                User user = existingUser.get();

                UserDto userDto = UserMapper.mapToDto(user);
                // Actualizar el usuario si ya existe
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setIsActive(true);
                user.setModified(new Date());

                // Asignar el rol si no tiene
                if (user.getRole() == null) {
                    user.setRole(Role.USER);
                    userDto.setRole(Role.USER);
                }

                userDto.setModified(user.getModified());
                userDto.setPassword(user.getPassword());
                userDto.setIsActive(user.getIsActive());

                userService.updateUser(userDto);

                // Generar token
                String token = jwtService.generateToken(user);

                return AuthResponse.builder()
                        .token(token)
                        .code(201)
                        .message("Usuario actualizado correctamente")
                        .build();
            } else {
                // El usuario no existe, manejar como error
                log.warn("Usuario no encontrado: " + request.getUsername());
                return AuthResponse.builder()
                        .code(404)
                        .message("Usuario no encontrado")
                        .build();
            }
        } catch (DataIntegrityViolationException ex) {
            log.error("Error en la consulta del usuario: " + ex.getMessage());
            return AuthResponse.builder()
                    .code(403)
                    .message("Error al consultar el usuario: " + ex.getMessage())
                    .build();
        } catch (PrimaryKeyViolationException e) {
            log.error("Error en el registro del usuario: " + e.getMessage());
            return AuthResponse.builder()
                    .code(400)
                    .message("Error en el registro del usuario: " + e.getMessage())
                    .build();
        }
    }
}
