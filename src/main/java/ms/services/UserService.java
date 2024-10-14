package ms.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import ms.dto.UserDto;
import ms.entities.Phone;
import ms.entities.User;
import ms.exception.UserNotFoundException;
import ms.mapper.UserMapper;
import ms.repository.UserRepository;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAllWithPhones() {
        return userRepository.findAllWithPhones();
    }

    @Transactional
    public Optional<UserDto> getUserById(UUID id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(UserMapper::mapToDto);
    }

    public User createUser(User user) throws Exception {

        if (!user.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new Exception("El formato del correo electrónico es incorrecto.");
        }
        // Verificar si el correo ya está registrado
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new Exception("El correo ya está registrado.");
        }

        return userRepository.save(user);
    }

    public User updateUser(UserDto userDto) {

        Optional<User> existingUserOpt = userRepository.findById(userDto.getId());

        if (!existingUserOpt.isPresent()) {
            throw new UserNotFoundException("Usuario no encontrado con ID: " + userDto.getId());
        }

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            // Actualizamos los campos del usuario con los valores del DTO
            existingUser.setName(userDto.getName());
            existingUser.setEmail(userDto.getEmail());

            // Encriptar la contraseña solo si fue modificada
            if (userDto.getPassword() != null) {
                existingUser.setPassword(new BCryptPasswordEncoder().encode(userDto.getPassword()));
            }

            // Mapea y actualiza los teléfonos si es necesario
            Set<Phone> updatedPhones = userDto.getPhones().stream().map(phoneDto -> {
                Phone phone = new Phone();
                phone.setNumber(phoneDto.getNumber());
                phone.setCitycode(phoneDto.getCityCode());
                phone.setCountrycode(phoneDto.getCountryCode());
                phone.setUser(existingUser); // Establecer la relación inversa
                return phone;
            }).collect(Collectors.toSet());

            existingUser.setPhones(updatedPhones);

            // Guardamos y devolvemos el usuario actualizado
            return userRepository.save(existingUser);
        } else {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

    }

    public void deleteUser(UUID id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        user.ifPresent(u -> Hibernate.initialize(u.getPhones()));  // Inicializa los teléfonos manualmente
        return user;
    }
}
