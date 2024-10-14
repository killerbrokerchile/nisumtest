package ms.dto;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ms.entities.Role;

@Builder
@AllArgsConstructor
@NoArgsConstructor  // Añadido para permitir la creación de instancias vacías
@Data
public class UserDto {

    private UUID id;
    private String username;
    private String password;
    private String name;
    private String email;
    private String token;
    private Role role;
    private Boolean isActive;
    private Date Modified;
    private Set<PhoneDto> phones; // DTO para los teléfonos
}
