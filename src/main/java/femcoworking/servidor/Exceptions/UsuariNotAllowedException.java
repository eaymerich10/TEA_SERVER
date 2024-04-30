package femcoworking.servidor.Exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepció utilitzada per informar d'una operació que no pot realitzar un usuari
 * perque no te permisos d'administrador (error 405).
 * 
 */
@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
public class UsuariNotAllowedException extends RuntimeException {

    public UsuariNotAllowedException(String missatgeError) {
        super(missatgeError);
    }
}
