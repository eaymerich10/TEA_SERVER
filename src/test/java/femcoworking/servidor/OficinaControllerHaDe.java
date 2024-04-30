/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package femcoworking.servidor;
import femcoworking.servidor.Models.OficinaVisualitzacio;
import femcoworking.servidor.Models.PeticioAltaOficina;
import femcoworking.servidor.Models.Oficina;
import femcoworking.servidor.Models.Categoria;
import femcoworking.servidor.Models.Rol;
import femcoworking.servidor.Models.Usuari;
import femcoworking.servidor.Controllers.OficinaController;
import femcoworking.servidor.Persistence.OficinaRepository;
import femcoworking.servidor.Persistence.UsuariRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(OficinaController.class)
public class OficinaControllerHaDe {

    private final String idUsuari = "idUsuari";
    private final String codiAcces = "unCodiAcces";
    private final String nom = "unaOficina";
    private final Categoria tipus = Categoria.OFICINA_PRIVADA;
    private final double preu = 10.4;
    private final Integer capacitat = 4;
    private final boolean habilitada = true;
    private final String idOficina = "idOficina";
    private final String provicia = "unaProvincia";
    private final String poblacio = "unaPoblacio";
    private final String direccio = "unaDireccio";
    private final boolean eliminat = false;

    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper objectmapper;
    @MockBean
    private UsuariRepository usuariRepository;
    @MockBean
    private OficinaRepository oficinaRepository;
    @MockBean
    private ControlAcces controlAcces;
    @MockBean
    private Mapper mapper;

    @Test
    public void DonarDAltaUnaNovaOficina() throws Exception {

        donatUnUsuari(Rol.ADMINISTRADOR);
        donatUnCodiDAccesValid();
        Oficina novaOficina = donadaUnaOficina(nom, tipus, preu, capacitat, habilitada);
        PeticioAltaOficina peticio = donadaUnaPeticioDAltaDOficina(novaOficina);

        mvc.perform(post("/altaoficina")
                .content(objectmapper.writeValueAsString(peticio))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Oficina donada d'alta amb l'identificador ")));
    }

    @Test
    public void retornarErrorSiUnUsuariNoAdministradorIntentaDonarDAltaUnaOficina()
            throws Exception {

        donatUnUsuari(Rol.CLIENT);
        donatUnCodiDAccesValid();
        donadaUnaOficinaExistent();
        Oficina novaOficina = donadaUnaOficina(nom, tipus, preu, capacitat, habilitada);
        PeticioAltaOficina peticio = donadaUnaPeticioDAltaDOficina(novaOficina);

        mvc.perform(post("/altaoficina")
                .content(objectmapper.writeValueAsString(peticio))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(result -> assertEquals("Aquesta funcionalitat requereix el rol d'administrador", result.getResolvedException().getMessage()));
    }

    @Test
    public void retornarErrorSiNoSInformaLOficinaALaPeticioDAltaDOficina()
            throws Exception {

        donatUnUsuari(Rol.ADMINISTRADOR);
        donatUnCodiDAccesValid();
        PeticioAltaOficina peticio = donadaUnaPeticioDAltaDOficina(null);

        mvc.perform(post("/altaoficina")
                .content(objectmapper.writeValueAsString(peticio))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("Oficina no informada", result.getResolvedException().getMessage()));
    }

    @Test
    public void retornarErrorSiNoSInformaElNomAlCrearUnaNovaOficina()
            throws Exception {

        donatUnUsuari(Rol.ADMINISTRADOR);
        donatUnCodiDAccesValid();
        Oficina novaOficina = donadaUnaOficina(null, tipus, preu, capacitat, habilitada);
        PeticioAltaOficina peticio = donadaUnaPeticioDAltaDOficina(novaOficina);

        mvc.perform(post("/altaoficina")
                .content(objectmapper.writeValueAsString(peticio))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("El camp nom és obligatori", result.getResolvedException().getMessage()));
    }

    @Test
    public void retornarErrorSiNoSInformaElTipusAlCrearUnaNovaOficina()
            throws Exception {

        donatUnUsuari(Rol.ADMINISTRADOR);
        donatUnCodiDAccesValid();
        Oficina novaOficina = donadaUnaOficina(nom, null, preu, capacitat, habilitada);
        PeticioAltaOficina peticio = donadaUnaPeticioDAltaDOficina(novaOficina);

        mvc.perform(post("/altaoficina")
                .content(objectmapper.writeValueAsString(peticio))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("El camp tipus és obligatori", result.getResolvedException().getMessage()));
    }

    @Test
    public void retornarErrorSiSInformaElPreuAmbValorNegatiuAlCrearUnaOficina()
            throws Exception {

        donatUnUsuari(Rol.ADMINISTRADOR);
        donatUnCodiDAccesValid();
        Oficina novaOficina = donadaUnaOficina(nom, tipus, -1, capacitat, habilitada);
        PeticioAltaOficina peticio = donadaUnaPeticioDAltaDOficina(novaOficina);

        mvc.perform(post("/altaoficina")
                .content(objectmapper.writeValueAsString(peticio))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("El camp preu és obligatori i no pot ser igual o inferior a 0", result.getResolvedException().getMessage()));
    }

    @Test
    public void retornarErrorSiSInformaElPreuIgualA0AlCrearUnaNovaOficina()
            throws Exception {

        donatUnUsuari(Rol.ADMINISTRADOR);
        donatUnCodiDAccesValid();
        Oficina novaOficina = donadaUnaOficina(nom, tipus, 0, capacitat, habilitada);
        PeticioAltaOficina peticio = donadaUnaPeticioDAltaDOficina(novaOficina);

        mvc.perform(post("/altaoficina")
                .content(objectmapper.writeValueAsString(peticio))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("El camp preu és obligatori i no pot ser igual o inferior a 0", result.getResolvedException().getMessage()));
    }

    @Test
    public void retornarErrorSiNoSInformaLaCapacitatAlCrearUnaNovaOficina()
            throws Exception {

        donatUnUsuari(Rol.ADMINISTRADOR);
        donatUnCodiDAccesValid();
        Oficina novaOficina = donadaUnaOficina(nom, tipus, preu, null, habilitada);
        PeticioAltaOficina peticio = donadaUnaPeticioDAltaDOficina(novaOficina);

        mvc.perform(post("/altaoficina")
                .content(objectmapper.writeValueAsString(peticio))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("El camp capacitat és obligatori", result.getResolvedException().getMessage()));
    }

    
    @Test
    public void guardarLaNovaOficina()
            throws Exception {

        donatUnUsuari(Rol.ADMINISTRADOR);
        donatUnCodiDAccesValid();
        Oficina novaOficina = donadaUnaOficina(nom, tipus, preu, capacitat, habilitada);
        PeticioAltaOficina peticio = donadaUnaPeticioDAltaDOficina(novaOficina);

        mvc.perform(post("/altaoficina")
                .content(objectmapper.writeValueAsString(peticio))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Oficina donada d'alta amb l'identificador ")));

        Mockito.verify(oficinaRepository).save(Mockito.any());
    }

    @Test
    public void retornarLaLlistaDOficinessAUnAdministrador()
            throws Exception {
        donatUnUsuari(Rol.ADMINISTRADOR);
        donatUnCodiDAccesValid();

        mvc.perform(get("/oficines/" + codiAcces)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(oficinaRepository).findAll();
    }

    @Test
    public void retornarLaLlistaDOficinesNoEliminadesAUnClient()
            throws Exception {
        
        donatUnUsuari(Rol.CLIENT);
        donatUnCodiDAccesValid();

        mvc.perform(get("/oficines/" + codiAcces)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(oficinaRepository).findOficinaByEliminatIsFalse();
    }

   
    @Test
    public void DonarDeBaixaUnaOficina()
            throws Exception {
        Usuari usuari = donatUnUsuari(Rol.ADMINISTRADOR);
        donatUnCodiDAccesValid();
        Oficina oficina = donadaUnaOficinaExistent();;

        mvc.perform(delete("/baixaoficina/" + codiAcces + "/" + idOficina)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Oficina donada de baixa")));

        assertThat(oficina.getEliminat()).isTrue();
    }

    @Test
    public void retornarErrorSiUnUusariClientVolDonarDeBaixaUnaOficina()
            throws Exception {

        donatUnUsuari(Rol.CLIENT);
        donatUnCodiDAccesValid();

        mvc.perform(delete("/baixaoficina/" + codiAcces + "/" + idOficina)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(result -> assertEquals("Aquesta funcionalitat requereix el rol d'administrador", result.getResolvedException().getMessage()));
    }

    @Test
    public void retornarErrorSiSIntentaDonarDeBaixaUnaOficinaQueNoExisteix()
            throws Exception {

        donatUnUsuari(Rol.ADMINISTRADOR);
        donatUnCodiDAccesValid();

        mvc.perform(delete("/baixaoficina/" + codiAcces + "/unIdProducteNoExistent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals("No s'ha trobat cap oficina amb l'identificador unIdProducteNoExistent", result.getResolvedException().getMessage()));
    }


    @Test
    public void guardarLOficinaComEliminadaAlDonarlaDeBaixa()
            throws Exception {
        Usuari usuari = donatUnUsuari(Rol.ADMINISTRADOR);
        donatUnCodiDAccesValid();
        donadaUnaOficinaExistent();

        mvc.perform(delete("/baixaoficina/" + codiAcces + "/" + idOficina)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Oficina donada de baixa")));

        Mockito.verify(oficinaRepository).save(Mockito.any());
    }

    @Test
    public void retornarErrorSiUnUsuariClientVolEditarUnaOficina()
            throws Exception {
        donatUnUsuari(Rol.CLIENT);
        donatUnCodiDAccesValid();
        OficinaVisualitzacio oficina = donadaUnaOficinaAEditar(idOficina, nom, tipus, capacitat, preu, null, habilitada, null, null, null,null);

        mvc.perform(put("/editaroficina/" + codiAcces)
                .content(objectmapper.writeValueAsString(oficina))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(result -> assertEquals("Aquesta funcionalitat requereix el rol d'administrador", result.getResolvedException().getMessage()));
    }

    @Test
    public void retornarErrorSiNoSInformaElIdDOficinaAlEditarUnaOficina()
            throws Exception {
        donatUnUsuari(Rol.ADMINISTRADOR);
        donatUnCodiDAccesValid();
        OficinaVisualitzacio oficina = donadaUnaOficinaAEditar(null, null, null, null, null, null, null, null, null, null, null);

        mvc.perform(put("/editaroficina/" + codiAcces)
                .content(objectmapper.writeValueAsString(oficina))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("El camp id oficina és obligatori", result.getResolvedException().getMessage()));
    }

    @Test
    public void retornarErrorSiSIntentaEditarUnaOficinaQueNoExisteix()
            throws Exception {

        donatUnUsuari(Rol.ADMINISTRADOR);
        donatUnCodiDAccesValid();
        donadaUnaOficinaExistent();
        OficinaVisualitzacio oficina = donadaUnaOficinaAEditar("unIdDeOficinaNoExistent", nom, tipus, capacitat, preu, null, habilitada, null, null, null, null);

        mvc.perform(put("/editaroficina/" + codiAcces)
                .content(objectmapper.writeValueAsString(oficina))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals("No s'ha trobat cap oficina amb l'identificador unIdDeOficinaNoExistent", result.getResolvedException().getMessage()));
    }

   
   
    @Test
    public void retornarErrorSiEsVolEditarElPreuDUnaOficinaAmbUnValorNegatiu()
            throws Exception {

        double nouPreu = -23.4;
        Usuari usuari = donatUnUsuari(Rol.ADMINISTRADOR);
        donatUnCodiDAccesValid();
        donadaUnaOficinaExistent();
        OficinaVisualitzacio oficinaAEditar = donadaUnaOficinaAEditar(idOficina, null, null, null, nouPreu, null, null, null, null, null, null);

        mvc.perform(put("/editaroficina/" + codiAcces)
                .content(objectmapper.writeValueAsString(oficinaAEditar))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("El valor del preu no pot ser negatiu", result.getResolvedException().getMessage()));
    }
    
    @Test
    public void editarElNomDUnaOficina()
            throws Exception {

        String nouNom = "nouNom";
        Usuari usuari = donatUnUsuari(Rol.ADMINISTRADOR);
        donatUnCodiDAccesValid();
        donadaUnaOficinaExistent();
        OficinaVisualitzacio oficinaAEditar = donadaUnaOficinaAEditar(idOficina, nouNom, null, null, null, null, null, null, null, null, null);

        mvc.perform(put("/editaroficina/" + codiAcces)
                .content(objectmapper.writeValueAsString(oficinaAEditar))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ArgumentCaptor<Oficina> argument = ArgumentCaptor.forClass(Oficina.class);
        Mockito.verify(oficinaRepository).save(argument.capture());
        assertThat(argument.getValue().getNom()).isEqualTo(nouNom);
    }

    @Test
    public void editarElTipusDUnaOficina()
            throws Exception {

        Categoria nouTipus = Categoria.SUITE_OFICINES;
        Usuari usuari = donatUnUsuari(Rol.ADMINISTRADOR);
        donatUnCodiDAccesValid();
        donadaUnaOficinaExistent();
        OficinaVisualitzacio oficinaAEditar = donadaUnaOficinaAEditar(idOficina, null, nouTipus, null, null, null, null, null, null, null, null);

        mvc.perform(put("/editaroficina/" + codiAcces)
                .content(objectmapper.writeValueAsString(oficinaAEditar))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ArgumentCaptor<Oficina> argument = ArgumentCaptor.forClass(Oficina.class);
        Mockito.verify(oficinaRepository).save(argument.capture());
        assertThat(argument.getValue().getTipus()).isEqualTo(nouTipus);
    }
    
    @Test
    public void editarElPreuDUnaOficina()
            throws Exception {

        double nouPreu = 23.4;
        Usuari usuari = donatUnUsuari(Rol.ADMINISTRADOR);
        donatUnCodiDAccesValid();
        donadaUnaOficinaExistent();
        OficinaVisualitzacio oficinaAEditar = donadaUnaOficinaAEditar(idOficina, null, null, null, nouPreu, null, null, null, null, null, null);

        mvc.perform(put("/editaroficina/" + codiAcces)
                .content(objectmapper.writeValueAsString(oficinaAEditar))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ArgumentCaptor<Oficina> argument = ArgumentCaptor.forClass(Oficina.class);
        Mockito.verify(oficinaRepository).save(argument.capture());
        assertThat(argument.getValue().getPreu()).isEqualTo(nouPreu);
    }

    @Test
    public void editarCapacitatDUnaOficina()
            throws Exception {

        Integer novaCapacitat = 8;
        Usuari usuari = donatUnUsuari(Rol.ADMINISTRADOR);
        donatUnCodiDAccesValid();
        donadaUnaOficinaExistent();
        OficinaVisualitzacio oficinaAEditar = donadaUnaOficinaAEditar(idOficina, null, null, novaCapacitat, null, null, null, null, null, null, null);

        mvc.perform(put("/editaroficina/" + codiAcces)
                .content(objectmapper.writeValueAsString(oficinaAEditar))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ArgumentCaptor<Oficina> argument = ArgumentCaptor.forClass(Oficina.class);
        Mockito.verify(oficinaRepository).save(argument.capture());
        assertThat(argument.getValue().getCapacitat()).isEqualTo(novaCapacitat);
    }
    private OficinaVisualitzacio donadaUnaOficinaAEditar(String idOficina, String nom, Categoria tipus, Integer capacitat, Double preu, String serveis, Boolean habilitada, String provincia, String poblacio, String direccio, Boolean eliminat) {
        return new OficinaVisualitzacio(idOficina, nom, tipus, capacitat, preu, serveis, habilitada, provincia, poblacio, direccio, eliminat);
    }
    
    @Test
    public void editarHabitabilitatDUnaOficina()
            throws Exception {

        Boolean habilitada = false;
        Usuari usuari = donatUnUsuari(Rol.ADMINISTRADOR);
        donatUnCodiDAccesValid();
        donadaUnaOficinaExistent();
        OficinaVisualitzacio oficinaAEditar = donadaUnaOficinaAEditar(idOficina, null, null, null, null, null, habilitada, null, null, null, null);

        mvc.perform(put("/editaroficina/" + codiAcces)
                .content(objectmapper.writeValueAsString(oficinaAEditar))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ArgumentCaptor<Oficina> argument = ArgumentCaptor.forClass(Oficina.class);
        Mockito.verify(oficinaRepository).save(argument.capture());
        assertThat(argument.getValue().isHabilitada()).isFalse();
    }
    

    private void donatUnCodiDAccesValid() {
        Mockito.when(controlAcces.ValidarCodiAcces(codiAcces)).thenReturn(idUsuari);
    }

    private PeticioAltaOficina donadaUnaPeticioDAltaDOficina(Oficina oficina) {
        PeticioAltaOficina peticio = new PeticioAltaOficina();
        peticio.setCodiAcces(codiAcces);
        peticio.setOficina(oficina);
        return peticio;
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

   

    private Usuari donatUnUsuari(Rol rol) {
        Usuari usuari = new Usuari();
        usuari.setIdUsuari(idUsuari);
        usuari.setRol(rol);

        Mockito.when(usuariRepository.findByIdUsuari(idUsuari)).thenReturn(usuari);

        return usuari;
    }
    private Oficina donadaUnaOficinaExistent() throws IOException {
        Oficina oficina = donadaUnaOficina(nom, tipus, preu, capacitat, habilitada);
        Mockito.when(oficinaRepository.findByIdOficina(idOficina)).thenReturn(oficina);
        return oficina;
    }
   
}
