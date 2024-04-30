package femcoworking.servidor.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepció utilitzada per informar que la operació que vol fer el client no es
 * pot realitzar ja que no s'ha trobat cap factura amb identificador de factura
 * sobre el que es vol fer l'operació. (error 404)
 * 
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class FacturaNotFoundException extends RuntimeException {
    public FacturaNotFoundException(String idFactura)  {
        super("No s'ha trobat cap factura amb l'identificador " + idFactura);
    }
}
