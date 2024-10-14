package ms.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import ms.dto.PhoneDto;
import ms.dto.UserDto;
import ms.entities.Phone;
import ms.entities.User;

public class UserMapper {

    public static UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());

        // Mapea los teléfonos si es necesario
        Set<PhoneDto> phoneDtos = user.getPhones().stream().map(phone -> {
            PhoneDto phoneDto = new PhoneDto();
            phoneDto.setNumber(phone.getNumber());
            phoneDto.setCityCode(phone.getCitycode());
            phoneDto.setCountryCode(phone.getCountrycode());
            return phoneDto;
        }).collect(Collectors.toSet());

        dto.setPhones(phoneDtos);
        return dto;
    }

    public static User mapToEntity(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());  // Asegúrate de que esté cifrada antes de persistir

        // Mapea los teléfonos si es necesario
        Set<Phone> phones = dto.getPhones().stream().map(phoneDto -> {
            Phone phone = new Phone();
            phone.setNumber(phoneDto.getNumber());
            phone.setCitycode(phoneDto.getCityCode());
            phone.setCountrycode(phoneDto.getCountryCode());
            phone.setUser(user);
            return phone;
        }).collect(Collectors.toSet());

        user.setPhones(phones);
        return user;
    }
}
