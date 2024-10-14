package ms.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import ms.dto.UserDto;
import ms.entities.Phone;
import ms.entities.User;
import ms.services.PhoneService;
import ms.services.UserService;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private PhoneService phoneService;

    @Operation(summary = "Obtener todos los usuarios", description = "Retorna una lista de todos los usuarios junto con sus teléfonos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuarios obtenidos exitosamente"),
        @ApiResponse(responseCode = "404", description = "No se encontraron usuarios"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<User> users = null;
        List<Map<String, Object>> userMaps = null;
        try {
            users = userService.findAllWithPhones();
            userMaps = users.stream()
                    .map(user -> {
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("id", user.getId());
                        userMap.put("name", user.getName());
                        userMap.put("username", user.getUsername());
                        userMap.put("password", user.getPassword());
                        userMap.put("name", user.getName());
                        userMap.put("email", user.getEmail());
                        userMap.put("created", user.getCreated());
                        userMap.put("role", user.getRole());
                        userMap.put("modified", user.getModified());
                        userMap.put("lastlogin", user.getLastLogin());
                        userMap.put("isactive", user.getIsActive());
                        userMap.put("token", user.getToken());
                        // Mapeo de los teléfonos
                        List<Map<String, Object>> phoneMaps = user.getPhones().stream()
                                .map(phone -> {
                                    Map<String, Object> phoneMap = new HashMap<>();
                                    phoneMap.put("id", phone.getId());
                                    phoneMap.put("number", phone.getNumber());
                                    // Agrega otros campos del teléfono que necesites
                                    return phoneMap;
                                })
                                .collect(Collectors.toList());
                        userMap.put("phones", phoneMaps);
                        log.info("phones " + phoneMaps.toString());
                        return userMap;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("mensaje", "Hubo un problema en la solicitud" + e.getMessage()));
        }
        return new ResponseEntity<>(userMaps, HttpStatus.OK);
    }

    @Operation(summary = "Obtener usuario por ID", description = "Retorna un usuario específico con base en su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@Parameter(description = "ID del usuario a buscar") @PathVariable UUID id) {
        Optional<UserDto> userDto = userService.getUserById(id);
        if (userDto.isPresent()) {
            return ResponseEntity.ok(userDto.get()); // Devuelve el DTO
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("mensaje", "Usuario no encontrado"));
        }
    }

    @Operation(summary = "Crear un nuevo usuario", description = "Crea un nuevo usuario con uno o más teléfonos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error al crear el usuario: debe tener al menos un teléfono"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            if (user.getPhones() == null || user.getPhones().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Collections.singletonMap("mensaje", "Error al crear el usuario: debe tener al menos un teléfono"));
            }
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            user.setIsActive(false);
            User newUser = userService.createUser(user);
            if (!user.getPhones().isEmpty()) {
                Set<Phone> userPhones = user.getPhones();
                newUser.setPhones(userPhones);
                for (Phone phone : userPhones) {
                    phone.setUser_id(user.getId());
                    phone.setUser(user);
                    phoneService.createPhone(phone);
                }
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (Exception e) {
            log.error("Error al crear el usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("mensaje", "Error al crear el usuario: " + e.getMessage()));
        }
    }

    @Operation(summary = "Actualizar un usuario existente", description = "Actualiza los detalles de un usuario existente por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable UUID id, @RequestBody User userDetails) {
        try {
            Optional<UserDto> user = userService.getUserById(id);
            if (user.isPresent()) {
                UserDto updatedUser = user.get();
                updatedUser.setName(userDetails.getName());
                updatedUser.setEmail(userDetails.getEmail());
                updatedUser.setPassword(new BCryptPasswordEncoder().encode(userDetails.getPassword()));

                // Actualizar el usuario en el servicio
                userService.updateUser(updatedUser);
                return ResponseEntity.ok(updatedUser);  // Devuelve el usuario actualizado
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("mensaje", "Usuario no encontrado"));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("mensaje", "Error al actualizar el usuario: " + e.getMessage()));
        }
    }

    @Operation(summary = "Eliminar un usuario por ID", description = "Elimina un usuario específico por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        Optional<UserDto> userOpt = userService.getUserById(id);

        if (userOpt.isPresent()) {
            userService.deleteUser(id);
        } else {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
}
