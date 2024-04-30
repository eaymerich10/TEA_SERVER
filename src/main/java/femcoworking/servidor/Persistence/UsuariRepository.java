package femcoworking.servidor.Persistence;

import femcoworking.servidor.Models.Usuari;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Classe per utilitzar les dades a memoria que fan referencia als usuaris.
 * 
 */
public interface UsuariRepository extends JpaRepository<Usuari, String>{
    /**
    * Cerca un usuari pel seu e-mail i commprova que no estigui deshabilitat.
    * @param email L'e-mail de l'usuari que es vol fer la comprovació.
    * @return L'usuari que s'esta buscant pel seu e-mail
    */
    List<Usuari> findUsuariByEmailAndDeshabilitatIsFalse(String email);
    /**
    * Cerca un usuari pel seu identificador únic.
    * @param idUsuari L'id d'usuari que se està cercant.
    * @return objecte usuari que s'està cencant per la seva id.
    */
    Usuari findByIdUsuari(String idUsuari);
}
