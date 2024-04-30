package femcoworking.servidor.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepció utilitzada per informar dels diversos casos en què una operació
 * pot fallar per que el client ha fet una petició al servidor no valida (error 400).
 * 
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    public BadRequestException(String missatgeError) {
        super(missatgeError);
    }
}
