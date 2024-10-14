package ms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor  // Añadido para permitir la creación de instancias vacías
@Data
public class PhoneDto {

    private String number;
    private String cityCode;
    private String countryCode;

}
