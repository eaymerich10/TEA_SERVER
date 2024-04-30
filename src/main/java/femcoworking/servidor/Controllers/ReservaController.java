/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package femcoworking.servidor.Controllers;


import femcoworking.servidor.Controllers.UsuariController;
import femcoworking.servidor.Exceptions.BadRequestException;
import femcoworking.servidor.Exceptions.OficinaNotFoundException;
import femcoworking.servidor.Exceptions.ReservaNotFoundException;
import femcoworking.servidor.Exceptions.UsuariNotAllowedException;
import femcoworking.servidor.Models.Oficina;
import femcoworking.servidor.Models.OficinaVisualitzacio;
import femcoworking.servidor.Models.PeticioEsborrarReserva;
import femcoworking.servidor.Models.PeticioReservaOficina;
import femcoworking.servidor.Models.Reserva;
import femcoworking.servidor.Models.Rol;
import femcoworking.servidor.Models.Usuari;
import femcoworking.servidor.Persistence.OficinaRepository;
import femcoworking.servidor.Persistence.ReservaRepository;
import femcoworking.servidor.Persistence.UsuariRepository;
import femcoworking.servidor.Services.ControlAcces;
import femcoworking.servidor.Services.Mapper;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@RestController
public class ReservaController {
    private static final Logger log = LoggerFactory.getLogger(ReservaController.class.getName());
    private final UsuariRepository usuariRepository;
    private final OficinaRepository oficinaRepository;
    private final ReservaRepository reservaRepository;
    private final ControlAcces controlAcces;
    private final Mapper mapper;

    public ReservaController(
        UsuariRepository usuariRepository,
        OficinaRepository oficinaRepository, 
        ReservaRepository reservaRepository, 
        ControlAcces controlAcces, 
        Mapper mapper
    ) {
        this.usuariRepository = usuariRepository;
        this.oficinaRepository = oficinaRepository;
        this.reservaRepository = reservaRepository;
        this.controlAcces = controlAcces;
        this.mapper = mapper;
    }

    @PostMapping("/reservaoficina")
    public String reservaOficina(@RequestBody PeticioReservaOficina reservaOficina) throws ParseException {
          
        log.trace("Rebuda petició de reserva d'oficina");    
        log.info("Rebuda petició de reserva d'oficina # codiAcces " + reservaOficina.getCodiAcces());
        
        String idUsuari = controlAcces.ValidarCodiAcces(reservaOficina.getCodiAcces());
        String idOficina = reservaOficina.getIdOficina();
        String dataInici = reservaOficina.getDataIniciReserva();
        String dataFi = reservaOficina.getDataFiReserva();
        
        Reserva reserva = new Reserva();
        InicialitzarCampsNovaReserva(
            reserva,
            idUsuari,
            idOficina,
            dataInici,
            dataFi
        );
        ValidarCampsNovaReserva(reserva);

        reservaRepository.save(reserva);

        log.info("Reserva efectuada amb l'identificador " + reserva.getIdReserva());

        String facturaReserva = "{\"idReserva\":\""+reserva.getIdReserva()+"\", \"idUsuari\":\""+idUsuari+"\"}";
        return facturaReserva;
    }
    
    @GetMapping("/reserves/{codiAcces}")
    public List<Reserva> llistarReserves(@PathVariable String codiAcces) {
        log.info("Petició de llistar reserves del codi " + codiAcces);
        
        String idUsuari = controlAcces.ValidarCodiAcces(codiAcces);
        Usuari usuari = usuariRepository.findByIdUsuari(idUsuari);
        
        List<Reserva> reserves = new ArrayList<>();
        
        if (usuari.getRol() == Rol.CLIENT) {
            reserves = reservaRepository.findAllByIdUsuari(usuari);
        } else {
            reserves = reservaRepository.findAll();
        }
        
        log.info("Retornada llista de reserves");
        return reserves;
    }
    
    @DeleteMapping("/esborrarreserva/{codiAcces}/{idReserva}")
    public String esborrarReserva(
        @PathVariable String codiAcces,
        @PathVariable String idReserva
    ) {
        log.info("Petició de esborrar reserva amb codi " + idReserva);
        
        Reserva reserva = reservaRepository.findByIdReserva(idReserva);
        if (null == reserva) {
            throw new ReservaNotFoundException(idReserva);
        }
        reservaRepository.delete(reserva);
        
        return "Reserva eliminada";
    }

    private void ValidarCampsNovaReserva(Reserva novaReserva) {

        if (novaReserva == null) {
            throw new BadRequestException("Reserva no informada");
        }

        if (novaReserva.getDataIniciReserva() == null) {
            throw new BadRequestException("El camp data_inici_reserva és obligatori");
        }
        
        if (novaReserva.getDataFiReserva() == null) {
            throw new BadRequestException("El camp data_fi_reseva és obligatori");
        }
        
        if (novaReserva.getDataIniciReserva().compareTo(novaReserva.getDataFiReserva()) > 0) {
            throw new BadRequestException("El camp data_fi_reseva és anterior a data_inici_reserva");
        }
        
        if (novaReserva.getIdOficina() == null) {
            throw new BadRequestException("Oficina no informada");
        }
        
        if (novaReserva.getIdUsuari() == null) {
            throw new BadRequestException("Usuari no informat");
        }
    }

    private void InicialitzarCampsNovaReserva(
        Reserva novaReserva, 
        String idUsuari,
        String idOficina,
        String dataInici,
        String dataFi
    ) throws ParseException {
        novaReserva.setIdReserva(UUID.randomUUID().toString());
        
        Usuari usuari = usuariRepository.findByIdUsuari(idUsuari);
        novaReserva.setIdUsuari(usuari);

        Oficina oficina = oficinaRepository.findByIdOficina (idOficina);
        novaReserva.setIdOficina (oficina);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date inici = dateFormat.parse(dataInici);
        Date fi = dateFormat.parse(dataFi);
        novaReserva.setDataIniciReserva(inici);
        novaReserva.setDataFiReserva(fi);
    }
    
    
    private void ValidarUsuariClient(String idUsuari) {
        Usuari usuari = usuariRepository.findByIdUsuari(idUsuari);
        if (usuari.getRol() != Rol.CLIENT)
        {
            throw new UsuariNotAllowedException("Aquesta funcionalitat requereix el rol de client");
        }
    }
}

    

