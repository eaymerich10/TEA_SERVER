/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package femcoworking.servidor.Services;

import femcoworking.servidor.Models.Oficina;
import femcoworking.servidor.Models.OficinaVisualitzacio;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Classe per mapejar les dades de una oficina donada o d'un llistat d'oficines.
 *
 */
@Service
@Primary
public class Mapper {
    public List<OficinaVisualitzacio> OficinesAMostrar(List<Oficina> oficines)
    {
        List<OficinaVisualitzacio> oficinesAMostrar = new ArrayList<>();
        for (Oficina oficina: oficines) {
            oficinesAMostrar.add(OficinaAMostrar(oficina));
        }
        return oficinesAMostrar;
    }

    public OficinaVisualitzacio OficinaAMostrar(Oficina oficina)
    {
        return new OficinaVisualitzacio(
            oficina.getIdOficina(), 
            oficina.getNom(), 
            oficina.getTipus(),
            oficina.getCapacitat(), 
            oficina.getPreu(), 
            oficina.getServeis(), 
            oficina.isHabilitada(), 
            oficina.getProvincia(),
            oficina.getPoblacio(), 
            oficina.getDireccio(),
            oficina.getEliminat()
        );
    }
    
}
