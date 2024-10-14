package ms.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private Date timestamp;
    private int codigo;
    private String detail;
    private String mensaje;

    public ErrorResponse(Date timestamp, int codigo, String detail, String mensaje) {
        this.timestamp = timestamp;
        this.codigo = codigo;
        this.detail = detail;
        this.mensaje = mensaje;
    }

    // Getters y setters
}
