package femcoworking.servidor.Services;

import femcoworking.servidor.Exceptions.BadRequestException;
import femcoworking.servidor.Models.Usuari;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Classe amb tots els métodes referents amb la lògica de negoci per fer el control d'accès d'un usuari.
 * 
 */
@Service
@Primary
public class ControlAcces {

    private final Map<String, String> codisAccesAcreditats = new HashMap<>();
    /**
    * Genera un codi d'acces aleatori per un usuari que ha iniciat una sessió correctament. 
    * @param usuari dades de l'usuari que ha iniciat una sessió.
    * @return codi d'accès de l'usuari que ha iniciat una sessió.
    */
    
    public String GenerarCodiAcces(Usuari usuari)
    {
        String codiAcces = UUID.randomUUID().toString();
        codisAccesAcreditats.put(codiAcces, usuari.getIdUsuari());
        return codiAcces;
    }
    
    /**
    * Valida el codi d'acces d'un usuari que vol fer operacions després d'haver iniciat una sessió. 
    * @param codiAcces codi d'accès d'un usuari amb sessió iniciada.
    * @return el codi d'acces de l'usuari una vegada comprovat que estigui acreditat.
    */
    public String ValidarCodiAcces(String codiAcces) {
        if (!codisAccesAcreditats.containsKey(codiAcces)){
            throw new BadRequestException("Codi d'accés no vàlid >> "+codiAcces);
        }
        return codisAccesAcreditats.get(codiAcces);
    }
    
    /**
    * Elimina el codi d'acces de la llista dels codis d'accès acreditats perque l'usuari ha surtit de la seva sessió. 
    * @param codiAcces codi d'accès de l'usuari que ha sortit de la seva sessió.
    */
    public void EliminarCodiAcces(String codiAcces) {
        codisAccesAcreditats.remove(codiAcces);
    }
    
    /**
    * Valida a un usuari que vol inicar una sessió i no la té ja iniciada.
    * @param usuari dades de l'usuari que vol iniciar la sessió.
    */
    public void ValidarUsuariSenseAccesPrevi(Usuari usuari) {
        if (codisAccesAcreditats.containsValue(usuari.getIdUsuari()))
        {
            throw new BadRequestException("Aquest usuari ja te accés. Per obtenir un nou codi primer ha de finalitzar sessió");
        }
    }
}
