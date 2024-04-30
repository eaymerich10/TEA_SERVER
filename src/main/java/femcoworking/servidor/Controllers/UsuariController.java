package femcoworking.servidor.Controllers;

import femcoworking.servidor.Models.Rol;
import femcoworking.servidor.Models.DadesAcces;
import femcoworking.servidor.Models.Usuari;
import femcoworking.servidor.Models.PeticioDeshabilitarUsuari;
import femcoworking.servidor.Models.PeticioCanviContrasenya;
import femcoworking.servidor.Exceptions.BadRequestException;
import femcoworking.servidor.Exceptions.UsuariNotAllowedException;
import femcoworking.servidor.Exceptions.UsuariNotFoundException;
import femcoworking.servidor.Models.PeticioEdicioUsuari;
import femcoworking.servidor.Persistence.UsuariRepository;
import femcoworking.servidor.Services.ControlAcces;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
public class UsuariController {
    private static final Logger log = LoggerFactory.getLogger(UsuariController.class.getName());
    private final UsuariRepository repository;
    private final ControlAcces controlAcces;

    public UsuariController(UsuariRepository repository, ControlAcces controlAcces) {
        this.repository = repository;
        this.controlAcces = controlAcces;
    }
    
    /**
     * L'usuari de l'aplicació client a través de la seva interficie demana registrar-se com  usuari omplint un formulari els camps necessaris per al seu seu registre. 
     * @param nouUsuari dades introduides per l'usuari al formulari per registrar-se.
     * @return missatge de que el usuari s'ha registrat correctament.
     */
    @PostMapping("/registre")
    public String registrarUsuari(@RequestBody Usuari nouUsuari) {

        log.trace("Rebuda petició de registre d'usuari");

        ValidarCampsNouUsuari(nouUsuari);
        InicialitzarCampsNouUsuari(nouUsuari);

        repository.save(nouUsuari);

        log.info("Nou usuari registrat amb l'identificador " + nouUsuari.getIdUsuari());

        return "Usuari registrat correctament amb el id " + nouUsuari.getIdUsuari();
    }
    
    /**
     * L'usuari de l'aplicació client a través de la seva interficie inicia una sessió i la seva interficie l'hi demana el seu email i contrasenya. 
     * @param dadesAccesUsuari son les dades d'acces per iniciar la sessió: 
     * e-mail i contrasenya.
     * @return codi d'acces que permet a l'aplicació realitzar la resta d'accessos
     * sense haver de demanar novament el e-mail i la contrasenya.
     */
    @PostMapping("/login")
    public String iniciarSessio(@RequestBody DadesAcces dadesAccesUsuari) {

        log.trace("Petició d'inici de sessió del usuari amb email " + dadesAccesUsuari.getEmail());

        ValidarCampsObligatorisPeticioLogin(dadesAccesUsuari);
        Usuari usuari = ValidarUsuariICredencials(dadesAccesUsuari);
        controlAcces.ValidarUsuariSenseAccesPrevi(usuari);
        ActualitzarDataUltimAcces(usuari);
        String codiAcces = controlAcces.GenerarCodiAcces(usuari);

        log.info("Retornat codi d'accés per l'usuari amb identificador "+ usuari.getIdUsuari() +" : " + codiAcces);
        String loggedUser = "{\"codiAcces\":\""+codiAcces+"\", \"rolUsuari\":\""+usuari.getRol()+"\"}";
        return loggedUser;
    }
    
    /**
     * L'usuari de l'aplicació client a través de la seva interficie demana finalitzar la seva sessió
     * @param codiAcces el codi d'acces de l'usuari que te iniciada la sessió. 
     * @return missatge de sessió finalitzada.
     */
    @GetMapping("/logout/{codiAcces}")
    public String finalitzarSessio(@PathVariable String codiAcces) {

        log.trace("Petició de finalitzar sessió del codi " + codiAcces);

        controlAcces.ValidarCodiAcces(codiAcces);
        controlAcces.EliminarCodiAcces(codiAcces);

        log.info("Sessió finalitzada correctament pel codi " + codiAcces);
        return "Sessió finalitzada";
    }
    
    /**
     * L'usuari de l'aplicació client a través de la seva interficie demana canviar la seva contrasenya
     * @param peticio la nova contrasenya.
     * @param codiAcces el codi d'acces del usuari que fa la petició. 
     * @return missatge de contrasenya modificada.
     */
    @PutMapping("/canviarContrasenya/{codiAcces}")
    public String canviarContrasenya(@RequestBody PeticioCanviContrasenya peticio, @PathVariable String codiAcces)
    {
        log.trace("Petició de canvi de contrasenya del codi " + codiAcces);

        String idUsuari = controlAcces.ValidarCodiAcces(codiAcces);
        ValidarCampsObligatorisPeticioCanviarContrasenya(peticio);
        ActualitzarContrasenya(peticio, idUsuari);

        return "Contrasenya modificada";
    }
    
    /**
     * L'usuari de l'aplicació client amb rol administrador demana donar de baixa a un usuari registrat. 
     * @param peticio la id del usuari al que el administrador vol donar de baixa.
     * @param codiAcces el codi d'acces del usuari que fa la petició. 
     * @return missatge d'usuari deshabilitat.
     */
    @DeleteMapping("/baixa/{codiAcces}")
    public String deshabilitarUsuari(@RequestBody PeticioDeshabilitarUsuari peticio, @PathVariable String codiAcces)
    {
        log.trace("Petició de deshabilitar usuari amb identificador del codi "+ codiAcces);

        String idUsuari = controlAcces.ValidarCodiAcces(codiAcces);
        ValidarCampsObligatorisPeticioDeshabilitarUsuari(peticio);
        ValidarUsuariAmbPermisosAdministrador(idUsuari);
        DeshabilitarUsuari(peticio);

        return "Usuari deshabilitat";
    }
    
    /**
     * L'usuari de l'aplicació client amb rol administrador demana un llistat de tots els usuaris registrats.
     * @param codiAcces el codi d'acces del usuari que fa la petició. 
     * @return missatge de resposta amb tots els usuaris registrats.
     */
    @GetMapping("/usuaris/{codiAcces}")
    public List<Usuari> llistarUsuaris(@PathVariable String codiAcces) {

        log.trace("Petició de llistar usuaris del codi " + codiAcces);

        String idUsuari = controlAcces.ValidarCodiAcces(codiAcces);
        ValidarUsuariAmbPermisosAdministrador(idUsuari);

        List<Usuari> usuaris = repository.findAll();

        log.trace("Retornada llista d'usuaris");
        return usuaris;
    }
     @PutMapping("/editarusuari/{codiAcces}")
    public Usuari editarUsuari(@RequestBody PeticioEdicioUsuari usuariEditat, @PathVariable String codiAcces)
    {
        log.trace("Petició d'edició d'usuari del codi " + codiAcces);

        String idUsuari = controlAcces.ValidarCodiAcces(codiAcces);
        Usuari usuariActualitzat = ActualitzarUsuari(idUsuari, usuariEditat);

        return usuariActualitzat;
    }

    
    /**
     * Inicialitza el id d'un nou usuari registrat, la data de creació del registre i encripta la contrasenya introduida per l'usuari al formulari.
     * @param codiAcces el codi d'acces del usuari que fa la petició.
     */
    private void InicialitzarCampsNouUsuari(Usuari nouUsuari) {
        nouUsuari.setIdUsuari(UUID.randomUUID().toString());
        nouUsuari.setDataCreacio(new Date());
        nouUsuari.setContrasenya(BCrypt.hashpw(nouUsuari.getContrasenya(), BCrypt.gensalt()));
    }
    
    /**
     * Valida els camps de registre d'un nou usuari i llança les misatjes d'error si no s'omplenen correctament.
     * @param nouUsuari dades introduides per l'usuari al formulari per registrar-se.
     */
    private void ValidarCampsNouUsuari(Usuari nouUsuari) {

        if (nouUsuari.getEmail() == null || nouUsuari.getEmail().isEmpty())
        {
            throw new BadRequestException("El camp email és obligatori");
        }

        if (nouUsuari.getContrasenya() == null || nouUsuari.getContrasenya().isEmpty())
        {
            throw new BadRequestException("El camp contrasenya és obligatori");
        }

        if (nouUsuari.getRol() == null)
        {
            throw new BadRequestException("El camp rol és obligatori");
        }

        if (!repository.findUsuariByEmailAndDeshabilitatIsFalse(nouUsuari.getEmail()).isEmpty())
        {
            throw new BadRequestException("Ja existeix un usuari actiu amb aquest email");
        }
    }
    
    /**
     * Valida els camps per iniciar sessió d'un usuari registrat.
     * @param dadesAccesUsuari dades introduides per l'usuari per iniciar sessió.
     */
    private void ValidarCampsObligatorisPeticioLogin(DadesAcces dadesAccesUsuari) {

        if (dadesAccesUsuari.getEmail() == null || dadesAccesUsuari.getEmail().isEmpty())
        {
            throw new BadRequestException("El camp email és obligatori");
        }

        if (dadesAccesUsuari.getContrasenya() == null || dadesAccesUsuari.getContrasenya().isEmpty())
        {
            throw new BadRequestException("El camp contrasenya és obligatori");
        }
    }
    
    /**
     * Valida si l'usuari que vol iniciar la sessió es un usuari registrat i no esta deshabilitat.
     * @param dadesAccesUsuari dades introduides per l'usuari per iniciar sessió.
     * @return l'usuari que es vol validar.
     */
    private Usuari ValidarUsuariICredencials(DadesAcces dadesAccesUsuari) {

        List<Usuari> usuaris = repository.findUsuariByEmailAndDeshabilitatIsFalse(dadesAccesUsuari.getEmail());

        if (usuaris == null || usuaris.size() != 1)
        {
            throw new BadRequestException("L'usuari no existeix");
        }
        Usuari usuari = usuaris.get(0);

        if (!BCrypt.checkpw(dadesAccesUsuari.getContrasenya(), usuari.getContrasenya()))
        {
            throw new BadRequestException("La contrasenya no és vàlida");
        }

        if (usuari.isDeshabilitat())
        {
            throw new BadRequestException("L'usuari està deshabilitat");
        }

        return usuari;
    }
    
    /**
     * Actualitza la dada d'ultim acces d'un usuari.
     * @param usuari l'usuari que es vol actualitzar el seu ultim accès.
     */
    private void ActualitzarDataUltimAcces(Usuari usuari) {
        usuari.setUltimAcces(new Date());
        repository.save(usuari);
    }
    
    /**
     * Quan un usuari demana canviar la contrasenya, valida que la nova contrasenya estigui emplenada.
     * @param peticio la nova contrasenya.
     */
    private void ValidarCampsObligatorisPeticioCanviarContrasenya(PeticioCanviContrasenya peticio) {
        if (peticio.getContrasenya() == null || peticio.getContrasenya().isEmpty())
        {
            throw new BadRequestException("El camp contrasenya és obligatori");
        }
    }
    
    /**
     * Actualitza la nova contrasenya i la salva a la base de dades encriptada.
     * @param peticio la nova contrasenya.
     * @param idUsuari la id de l'usuari que vol canviar la seva contrasenya.
     */
    private void ActualitzarContrasenya(PeticioCanviContrasenya peticio, String idUsuari) {
        Usuari usuari = repository.findByIdUsuari(idUsuari);
        usuari.setContrasenya(BCrypt.hashpw(peticio.getContrasenya(), BCrypt.gensalt()));
        repository.save(usuari);
        log.info("Contrasenya modificada correctament per l'usuari amb identificador " + usuari.getIdUsuari());
    }
    
    /**
     * Per deshabilitar un usuari valida que l'usuari estigui registrat.
     * @param peticio la nova contrasenya.
     */
    private void ValidarCampsObligatorisPeticioDeshabilitarUsuari(PeticioDeshabilitarUsuari peticio) {
        if (peticio.getIdUsuari() == null || peticio.getIdUsuari().isEmpty())
        {
            throw new BadRequestException("El camp id usuari és obligatori");
        }
    }
    
    /**
     * Valida que l'usuari sigui administrador per fer operacions que requereixen permisos d'administrador.
     * @param idUsuari a id de l'usuari que es vol validar.
     */
    private void ValidarUsuariAmbPermisosAdministrador(String idUsuari) {
        Usuari usuari = repository.findByIdUsuari(idUsuari);
        if (usuari.getRol() != Rol.ADMINISTRADOR)
        {
            throw new UsuariNotAllowedException("Aquest usuari no té permís d'administrador");
        }
    }
    
    /**
     * Deshabilita a l'usuari que un administrador ha donat de baixa.
     * @param idUsuari Id de l'usuari al que es vol deshabilitar.
     */
    private void DeshabilitarUsuari(PeticioDeshabilitarUsuari peticio) {
        Usuari usuari = repository.findByIdUsuari(peticio.getIdUsuari());

        if (usuari == null)
        {
            throw new UsuariNotFoundException(peticio.getIdUsuari());
        }   
     
    /**
     * Al registre de la base de dades de l'usuari extableix true com a valor del camp deshabilitat.
     */ 
        usuari.setDeshabilitat(true);
        repository.save(usuari);
        log.info("Deshabilitat l'usuari amb identificador "+usuari.getIdUsuari());
    }
    

    private Usuari ActualitzarUsuari(String idUsuari, PeticioEdicioUsuari usuariEditat) {

        Usuari usuari = repository.findByIdUsuari(idUsuari);

        if (usuariEditat.getNom() != null && !usuariEditat.getNom().isEmpty())
        {
            usuari.setNom(usuariEditat.getNom());
        }

        if (usuariEditat.getCifEmpresa() != null && !usuariEditat.getCifEmpresa().isEmpty())
        {
            usuari.setCifEmpresa(usuariEditat.getCifEmpresa());
        }

        if (usuariEditat.getDireccio() != null && !usuariEditat.getDireccio().isEmpty())
        {
            usuari.setDireccio(usuariEditat.getDireccio());
        }

        if (usuariEditat.getPoblacio() != null && !usuariEditat.getPoblacio().isEmpty())
        {
            usuari.setPoblacio(usuariEditat.getPoblacio());
        }

        if (usuariEditat.getProvincia() != null && !usuariEditat.getProvincia().isEmpty())
        {
            usuari.setProvincia(usuariEditat.getProvincia());
        }

        repository.save(usuari);
        return usuari;
    }
}
