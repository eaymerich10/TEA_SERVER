
package femcoworking.servidor.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class OficinaNotFoundException extends RuntimeException {
    public OficinaNotFoundException(String idOficina)  {
        super("No s'ha trobat cap oficina amb l'identificador " + idOficina);
    }
}