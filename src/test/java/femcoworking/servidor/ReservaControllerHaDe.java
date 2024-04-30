/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package femcoworking.servidor;

import femcoworking.servidor.Models.PeticioReservaOficina;
import femcoworking.servidor.Models.Oficina;
import femcoworking.servidor.Models.Categoria;
import femcoworking.servidor.Models.Reserva;
import femcoworking.servidor.Models.Rol;
import femcoworking.servidor.Models.Usuari;
import com.fasterxml.jackson.annotation.JsonFormat;
import femcoworking.servidor.Controllers.ReservaController;
import femcoworking.servidor.Persistence.OficinaRepository;
import femcoworking.servidor.Persistence.UsuariRepository;
import femcoworking.servidor.Persistence.ReservaRepository;
import femcoworking.servidor.Services.ControlAcces;
import femcoworking.servidor.Services.Mapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import static java.lang.String.format;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ReservaController.class)
public class ReservaControllerHaDe {
    private final String nom = "unaOficina";
    private final Categoria tipus = Categoria.OFICINA_PRIVADA;
    private final double preu = 10.4;
    private final Integer capacitat = 4;
    private final boolean habilitada = true;
    
    private final Date data_inici_reserva = donadaUnaData("2021-04-10");
    private final Date data_fi_reserva = donadaUnaData("2021-04-15");
    private final String sIdOficina = "idOficina";
    private final String sIdUsuari = "idUsuari";
    private final String idReserva = "idReserva";
    private final String codiAcces = "unCodiAcces";
  
 
    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper objectmapper;
    @MockBean
    private UsuariRepository usuariRepository;
    @MockBean
    private OficinaRepository oficinaRepository;
    @MockBean
    private ReservaRepository reservaRepository;
    @MockBean
    private ControlAcces controlAcces;
    @MockBean
    private Mapper mapper;
    
  
   

    @Test
    public void DonarDAltaUnaNovaReserva() throws Exception {

        Usuari usuari = donatUnUsuari(Rol.CLIENT);
        Oficina oficina = donadaUnaOficinaExistent();
        donatUnCodiDAccesValid();
        Reserva novaReserva = donadaUnaReserva(usuari, oficina, data_inici_reserva, data_fi_reserva);
        PeticioReservaOficina peticio = donadaUnaPeticioReservaOficina(novaReserva);

        mvc.perform(post("/reservaoficina")
                .content(objectmapper.writeValueAsString(peticio))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Reserva efectuada amb l'identificador ")));
    }
    
    
    
    @Test
    public void retornarErrorSiNoSInformaLaDataIniciReservaAlCrearUnaNovaReserva()
            throws Exception {

        Usuari usuari = donatUnUsuari(Rol.CLIENT);
        donatUnCodiDAccesValid();
        Oficina oficina = donadaUnaOficinaExistent();
        Reserva novaReserva = donadaUnaReserva(usuari, oficina, null, data_fi_reserva);
        PeticioReservaOficina peticio = donadaUnaPeticioReservaOficina(novaReserva);

        mvc.perform(post("/reservaoficina")
                .content(objectmapper.writeValueAsString(peticio))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("El camp data_inici_reserva és obligatori", result.getResolvedException().getMessage()));
    }
    
    @Test
    public void retornarErrorSiNoSInformaLaDataFiReservaAlCrearUnaNovaReserva()
            throws Exception {

        Usuari usuari = donatUnUsuari(Rol.CLIENT);
        donatUnCodiDAccesValid();
        Oficina oficina = donadaUnaOficinaExistent();
        Reserva novaReserva = donadaUnaReserva(usuari, oficina, data_inici_reserva, null);
        PeticioReservaOficina peticio = donadaUnaPeticioReservaOficina(novaReserva);

        mvc.perform(post("/reservaoficina")
                .content(objectmapper.writeValueAsString(peticio))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("El camp data_fi_reserva és obligatori", result.getResolvedException().getMessage()));
    }
    
    @Test
    public void guardarLaNovaReserva()
            throws Exception {

        Usuari usuari = donatUnUsuari(Rol.CLIENT);
        Oficina oficina = donadaUnaOficinaExistent();
        donatUnCodiDAccesValid();
        Reserva novaReserva= donadaUnaReserva(usuari, oficina, data_inici_reserva, data_fi_reserva);
        PeticioReservaOficina  peticio = donadaUnaPeticioReservaOficina(novaReserva);

        mvc.perform(post("/reservaoficina")
                .content(objectmapper.writeValueAsString(peticio))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Reserva efectuada amb l'identificador ")));

        Mockito.verify(reservaRepository).save(Mockito.any());
    }
    
    @Test
    public void retornarLaLlistaDEReservesAUnAdministrador()
            throws Exception {
        donatUnUsuari(Rol.ADMINISTRADOR);
        donatUnCodiDAccesValid();

        mvc.perform(get("/reserves/" + codiAcces)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(reservaRepository).findAll();
    }
    
    @Test
    public void retornarLaLlistaDEReservesAUnClient()
            throws Exception {
        Usuari usuari = donatUnUsuari(Rol.CLIENT);
        donatUnCodiDAccesValid();

        mvc.perform(get("/reserves/" + codiAcces)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(reservaRepository).findAllByIdUsuari(usuari);
    }
    
    private void donatUnCodiDAccesValid() {
        Mockito.when(controlAcces.ValidarCodiAcces(codiAcces)).thenReturn(sIdUsuari);
    }
  
    private Usuari donatUnUsuari(Rol rol) {
        Usuari usuari = new Usuari();
        usuari.setIdUsuari(sIdUsuari);
        usuari.setRol(rol);

        Mockito.when(usuariRepository.findByIdUsuari(sIdUsuari)).thenReturn(usuari);

        return usuari;
    }
    

    private PeticioReservaOficina donadaUnaPeticioReservaOficina(Reserva novaReserva) {
        PeticioReservaOficina peticio = new PeticioReservaOficina(); 
        peticio.setCodiAcces(codiAcces);
        peticio.setIdOficina(novaReserva.getIdOficina().getIdOficina());
        peticio.setDataIniciReserva(novaReserva.getDataIniciReserva().toString());
        return peticio;
    }
    
    private Date donadaUnaData (String date){
        Date testDate = null;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try{
        testDate = df.parse(date);
     
    } catch (ParseException e){ System.out.println("invalid format");}
 
    if (!df.format(testDate).equals(date)){
        System.out.println("invalid date!!");
    } else {
        System.out.println("valid date");
    }
    
    return testDate;
    
    }
    
    
     private Reserva donadaUnaReserva(Usuari usuari, Oficina oficina, Date data_inici_reserva, Date data_fi_reserva) throws IOException {
      
        Reserva reserva = new Reserva();
        reserva.setIdUsuari(usuari);
        reserva.setIdOficina(oficina);
        reserva.setDataIniciReserva(data_inici_reserva);
        reserva.setDataFiReserva(data_fi_reserva);
        
        return reserva;
    }
     
     private Oficina donadaUnaOficinaExistent() throws IOException {
        Oficina oficina = donadaUnaOficina(nom, tipus, preu, capacitat, habilitada);
        Mockito.when(oficinaRepository.findByIdOficina(sIdOficina)).thenReturn(oficina);
        return oficina;
    }
     
      private Reserva donadaUnaReservaExistent() throws IOException {
        Reserva reserva = donadaUnaReserva(donatUnUsuari(Rol.CLIENT), donadaUnaOficinaExistent(), data_inici_reserva, data_fi_reserva);
        Mockito.when(reservaRepository.findByIdReserva(idReserva)).thenReturn(reserva);
        return reserva;
    }
     
     private Oficina donadaUnaOficina(String nom, Categoria tipus, double preu, Integer capacitat, Boolean habilitada) throws IOException {
      
        Oficina oficina = new Oficina();
        oficina.setNom(nom);
        oficina.setTipus(tipus);
        oficina.setPreu(preu);
        oficina.setCapacitat(capacitat);
        oficina.setHabilitada(habilitada);

        return oficina;
    }


    
}
