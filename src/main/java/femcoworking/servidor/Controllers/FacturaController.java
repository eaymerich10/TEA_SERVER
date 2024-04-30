package femcoworking.servidor.Controllers;


import femcoworking.servidor.Controllers.UsuariController;
import femcoworking.servidor.Exceptions.BadRequestException;
import femcoworking.servidor.Exceptions.FacturaNotFoundException;
import femcoworking.servidor.Exceptions.OficinaNotFoundException;
import femcoworking.servidor.Exceptions.ReservaNotFoundException;
import femcoworking.servidor.Exceptions.UsuariNotAllowedException;
import femcoworking.servidor.Models.Factura;
import femcoworking.servidor.Models.Oficina;
import femcoworking.servidor.Models.OficinaVisualitzacio;
import femcoworking.servidor.Models.PeticioReservaOficina;
import femcoworking.servidor.Models.Reserva;
import femcoworking.servidor.Models.Rol;
import femcoworking.servidor.Models.Usuari;
import femcoworking.servidor.Models.PeticioFacturaOficina;
import femcoworking.servidor.Models.ReservaVisualitzacio;
import femcoworking.servidor.Persistence.FacturaRepository;
import femcoworking.servidor.Persistence.OficinaRepository;
import femcoworking.servidor.Persistence.ReservaRepository;
import femcoworking.servidor.Persistence.UsuariRepository;
import femcoworking.servidor.Services.ControlAcces;
import femcoworking.servidor.Services.Mapper;
import java.text.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@RestController
public class FacturaController {
    private static final Logger log = LoggerFactory.getLogger(FacturaController.class.getName());
    private final UsuariRepository usuariRepository;
    private final OficinaRepository oficinaRepository;
    private final FacturaRepository facturaRepository;
    private final ReservaRepository reservaRepository;
    private final ControlAcces controlAcces;
    private final Mapper mapper;

    public FacturaController(
        UsuariRepository usuariRepository,
        OficinaRepository oficinaRepository,
        FacturaRepository facturaRepository,
        ReservaRepository reservaRepository, 
        ControlAcces controlAcces, 
        Mapper mapper
    ) {
        this.usuariRepository = usuariRepository;
        this.oficinaRepository = oficinaRepository;
        this.facturaRepository = facturaRepository;
        this.reservaRepository = reservaRepository;
        this.controlAcces = controlAcces;
        this.mapper = mapper;
    }
 
    
    
    @PostMapping("/facturaoficina")
    public String facturaOficina(@RequestBody PeticioFacturaOficina facturaOficina) throws ParseException {
          
        log.trace("Rebuda petició de factura d'una reserva d'oficina");        
        log.info("Rebuda petició de factura d'una reserva d'oficina # codiAcces " + facturaOficina.getCodiAcces());
        String idUsuari = controlAcces.ValidarCodiAcces(facturaOficina.getCodiAcces());
        String idReserva = facturaOficina.getIdReserva();
      
        
        Factura factura = new Factura();
        
        InicialitzarCampsNovaFactura(
            factura,
            idUsuari,    
            idReserva
        );
        ValidarCampsNovaFactura(factura);

        facturaRepository.save(factura);
        log.info("Factura donada d'alta amb l'identificador " + factura.getIdFactura());
        return "Factura donada d'alta amb l'identificador " + factura.getIdFactura();
         
    }
    @GetMapping("/factures/{codiAcces}")
    public List<Factura> llistarFactures(@PathVariable String codiAcces) {
        log.info("Petició de llistar factures del codi " + codiAcces);
        
        String idUsuari = controlAcces.ValidarCodiAcces(codiAcces);
        Usuari usuari = usuariRepository.findByIdUsuari(idUsuari);
        
        List<Factura> factures = new ArrayList<>();
        
        if (usuari.getRol() == Rol.CLIENT) {
            factures = facturaRepository.findAllByIdUsuari(usuari);
        } else {
            factures = facturaRepository.findAll();
        }
        
        log.info("Retornada llista de factures");
        return factures;
    }
    
    @DeleteMapping("/esborrarfactura/{codiAcces}/{idFactura}")
    public String esborrarFactura(
        @PathVariable String codiAcces,
        @PathVariable String idFactura
    ) {
        log.info("Petició de esborrar factura amb codi " + idFactura);
        String idUsuari = controlAcces.ValidarCodiAcces(codiAcces);
        ValidarUsuariAdministrador(idUsuari);
        Factura factura = facturaRepository.findByIdFactura(idFactura);
        if (null == factura) {
            throw new FacturaNotFoundException(idFactura);
        } 
        facturaRepository.delete(factura);

        return "Factura eliminada";
    }

    private void ValidarCampsNovaFactura(Factura novaFactura) {

        if (novaFactura == null) {
            throw new BadRequestException("Factura no informada");
        }

        if (novaFactura.getIdUsuari() == null) {
            throw new BadRequestException("Usuari no informat");
        }
        
        if (novaFactura.getIdReserva() == null) {
            throw new BadRequestException("Reserva no informada");
        }
        
        
        
    }

    private void InicialitzarCampsNovaFactura(Factura novaFactura, String idUsuari, String idReserva) {
        Usuari usuari = usuariRepository.findByIdUsuari(idUsuari);
        novaFactura.setIdUsuari(usuari);
        Reserva reserva = reservaRepository.findByIdReserva (idReserva);
        novaFactura.setIdReserva (reserva);
        novaFactura.setIdFactura(UUID.randomUUID().toString());
        novaFactura.setDataCreacio(new Date());
        novaFactura.setNomUsuariReserva(usuari.getNom());
        novaFactura.setNomOficina(reserva.getIdOficina().getNom());
        novaFactura.setTipusOficina(reserva.getIdOficina().getTipus());
        novaFactura.setPreuOficina(reserva.getIdOficina().getPreu());
        novaFactura.setData_inici_reserva(reserva.getDataIniciReserva());
        novaFactura.setData_fi_reserva(reserva.getDataFiReserva());
        Double preuOficina = reserva.getIdOficina().getPreu();
        Integer dies = nombreDiesEntreDuesDates(reserva.getDataIniciReserva(), reserva.getDataFiReserva() );
        novaFactura.setSubTotal(preuOficina*dies);
        novaFactura.setImpostos(preuOficina*dies*0.21);
        novaFactura.setTotal(novaFactura.getSubTotal()+novaFactura.getImpostos());
        
        
    }
    
    private int nombreDiesEntreDuesDates(Date data1, Date data2){
     long startTime = data1.getTime();
     long endTime = data2.getTime();
     long diffTime = endTime - startTime;
     long diffDays = diffTime / (1000 * 60 * 60 * 24);
     if ((int)diffDays == 0){
         diffDays =1;
     }
     return (int)diffDays;
    }
    
    /**
     * Valida que l'usuari sigui administrador per fer operacions que requereixen permisos d'administrador.
     * @param idUsuari a id de l'usuari que es vol validar.
     */
    private void ValidarUsuariAdministrador(String idUsuari) {
        Usuari usuari = usuariRepository.findByIdUsuari(idUsuari);
        if (usuari.getRol() != Rol.ADMINISTRADOR)
        {
            throw new UsuariNotAllowedException("Aquesta funcionalitat requereix el rol d'administrador");
        }
    }
}
