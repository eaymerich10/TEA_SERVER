/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package femcoworking.servidor.Persistence;

import femcoworking.servidor.Models.Oficina;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface OficinaRepository extends JpaRepository<Oficina, String>, JpaSpecificationExecutor<Oficina> {
    List<Oficina> findOficinaByEliminatIsFalse();
    
    Oficina findByIdOficina(String idOficina);
    
    @Query(
        value = "SELECT * FROM oficines WHERE eliminat=false AND habilitada=true"
        + " AND id_oficina NOT IN (" 
        + " SELECT id_oficina FROM reserva_oficina"
        + " WHERE "
        + "     data_inici_reserva >= cast(:dataInici as timestamp) AND "
        + "     data_fi_reserva <= cast(:dataFi as timestamp)"
        + ")",            
        nativeQuery = true
    )
    List<Oficina> findOficinesDisponibles(
        @Param("dataInici") String inici,
        @Param("dataFi") String fi);
}