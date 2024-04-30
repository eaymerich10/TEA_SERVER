package femcoworking.servidor.Persistence;

import femcoworking.servidor.Models.Factura;
import femcoworking.servidor.Models.Reserva;
import femcoworking.servidor.Models.Usuari;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Repositori per la persistencia de les dades que fan referencia al les oficines.
 * 
 */
public interface FacturaRepository extends JpaRepository<Factura, String>, JpaSpecificationExecutor<Factura> {
    
    Factura findByIdFactura(String idFactura);
    
    List<Factura> findAllByIdUsuari(Usuari idUsuari);
}