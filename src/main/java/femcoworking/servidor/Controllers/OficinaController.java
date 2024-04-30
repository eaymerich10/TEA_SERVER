package femcoworking.servidor.Controllers;
import femcoworking.servidor.Controllers.UsuariController;
import femcoworking.servidor.Exceptions.BadRequestException;
import femcoworking.servidor.Exceptions.OficinaNotFoundException;
import femcoworking.servidor.Exceptions.UsuariNotAllowedException;
import femcoworking.servidor.Models.Oficina;
import femcoworking.servidor.Models.OficinaVisualitzacio;
import femcoworking.servidor.Models.PeticioAltaOficina;
import femcoworking.servidor.Models.PeticioLlistatOficinesDisponibles;
import femcoworking.servidor.Models.Rol;
import femcoworking.servidor.Models.Usuari;
import femcoworking.servidor.Persistence.OficinaRepository;
import femcoworking.servidor.Persistence.UsuariRepository;
import femcoworking.servidor.Services.ControlAcces;
import femcoworking.servidor.Services.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@RestController
public class OficinaController {
    private static final Logger log = LoggerFactory.getLogger(UsuariController.class.getName());
    private final UsuariRepository usuariRepository;
    private final OficinaRepository oficinaRepository;
    private final ControlAcces controlAcces;
    private final Mapper mapper;

    public OficinaController(UsuariRepository usuariRepository, OficinaRepository oficinaRepository, ControlAcces controlAcces, Mapper mapper) {
        this.usuariRepository = usuariRepository;
        this.oficinaRepository = oficinaRepository;
        this.controlAcces = controlAcces;
        this.mapper = mapper;
    }
    
    /**
     * L'usuari administrador a través de la seva interficie demana fer el registre d'una oficina 
     * @param altaOficina codi d'acces de l'usuari administrador i les dades de l'oficina que es vol 
     * registrar.
     * @return missatge de que l'oficina s'ha registrat correctament.
     */
    @PostMapping("/altaoficina")
    public String altaOficina(@RequestBody PeticioAltaOficina altaOficina) {

        log.trace("Rebuda petició d'alta d'oficina");

        String idUsuari = controlAcces.ValidarCodiAcces(altaOficina.getCodiAcces());
        ValidarUsuariAdministrador(idUsuari);
        ValidarCampsNovaOficina(altaOficina.getOficina());
        InicialitzarCampsNovaOficina(altaOficina.getOficina());

        oficinaRepository.save(altaOficina.getOficina());

        log.info("Oficina donada d'alta amb l'identificador " + altaOficina.getOficina().getIdOficina());

        return "Oficina donada d'alta amb l'identificador " + altaOficina.getOficina().getIdOficina();
    }
    
    /**
     * L'usuari administrador a través de la seva interficie demana llistar totes
     * les oficines registrades, oficines eliminades i no habilitades incloses.
     * @param codiAcces codi d'acces del usuari administrador.
     * @return missatge de que l'oficina s'ha registrat correctament.
     */
    @GetMapping("/oficines/{codiAcces}")
    public List<OficinaVisualitzacio> llistarOficines(@PathVariable String codiAcces) {

        log.trace("Petició de llistar productes del codi " + codiAcces);

        String idUsuari = controlAcces.ValidarCodiAcces(codiAcces);
        Usuari usuari = usuariRepository.findByIdUsuari(idUsuari);

        List<Oficina> oficines = new ArrayList<>();

        if (usuari.getRol() == Rol.CLIENT)
        {
            oficines = oficinaRepository.findOficinaByEliminatIsFalse();
        }

        if (usuari.getRol() == Rol.ADMINISTRADOR)
        {
            oficines = oficinaRepository.findAll();
        }

        List<OficinaVisualitzacio> oficinesAMostrar = mapper.OficinesAMostrar(oficines);

        log.trace("Retornada llista d'Oficines");
        return oficinesAMostrar;
    }
    
    /**
     * L'usuari client a través de la seva interficie demana llistat d'oficines
     * disponibles per a poder fer reserves
     * @param peticio
     * @param codiAcces
     * @return 
     */
    @PostMapping("/oficinesdisponibles/{codiAcces}")
    public List<OficinaVisualitzacio> llistarOficinesDisponibles(
        @RequestBody PeticioLlistatOficinesDisponibles peticio,
        @PathVariable String codiAcces
    ) {
        log.trace("Petició de llistar productes del codi " + codiAcces);

        String idUsuari = controlAcces.ValidarCodiAcces(codiAcces);
        Usuari usuari = usuariRepository.findByIdUsuari(idUsuari);

        List<Oficina> oficines = new ArrayList<>();

        if (usuari.getRol() == Rol.CLIENT)
        {
            oficines = oficinaRepository.findOficinesDisponibles(
                peticio.getDataInici(),
                peticio.getDataFi()
            );
        }

        List<OficinaVisualitzacio> oficinesDisponibles = mapper.OficinesAMostrar(oficines);

        log.trace("Retornada llista d'Oficines disponibles");
        return oficinesDisponibles;
    }
    
    /**
     * L'usuari administrador a través de la seva interficie demana eliminar
     * una oficina registrada.
     * @param codiAcces codi d'acces del usuari administrador.
     * @param idOficina identificador d'Oficina.
     * @return missatge de que l'oficina s'ha registrat correctament.
     */
    @DeleteMapping("/baixaoficina/{codiAcces}/{idOficina}")
    public String baixaOficina(@PathVariable String codiAcces, @PathVariable String idOficina)
    {
        log.trace("Petició de baixa de la oficina amb codi "+ codiAcces);

        String idUsuari = controlAcces.ValidarCodiAcces(codiAcces);
        ValidarCampsObligatorisPeticioBaixaOficina(idOficina);
        ValidarUsuariAdministrador(idUsuari);
        Oficina oficina = ValidarOficinaExistent(idOficina);
        BaixaOficina(oficina);

        return "Oficina donada de baixa";
    }
    
    /**
     * L'usuari administrador a través de la seva interficie demana editar una
     * oficina donada.
     * @param codiAcces codi d'acces del usuari administrador.
     * @param oficinaEditada dades de la nova Oficina editada.
     * @return missatge de que l'oficina s'ha actualitzat amb les noves dades.
     */
    @PutMapping("/editaroficina/{codiAcces}")
    public OficinaVisualitzacio editarOficina(@RequestBody OficinaVisualitzacio oficinaEditada, @PathVariable String codiAcces)
    {
        log.trace("Petició d'edició de oficina del codi " + codiAcces);

        String idUsuari = controlAcces.ValidarCodiAcces(codiAcces);
        ValidarUsuariAdministrador(idUsuari);
        ValidarCampsObligatorisEdicioOficina(oficinaEditada);
        Oficina oficinaGuardada = ValidarOficinaExistent(oficinaEditada.getIdOficina());
        OficinaVisualitzacio oficinaActualitzada = ActualitzarOficina(oficinaGuardada, oficinaEditada);

        return oficinaActualitzada;
    }
    
    /**
     * Valida que s'informi de tots els camps obligatoris d'una oficina editada donada.
     * @param novaOficina dades de la nova Oficina editada.
     */
    

    private void ValidarCampsNovaOficina(Oficina novaOficina) {

        if (novaOficina == null)
        {
            throw new BadRequestException("Oficina no informada");
        }

        if (novaOficina.getNom() == null || novaOficina.getNom().isEmpty())
        {
            throw new BadRequestException("El camp nom és obligatori");
        }

        if (novaOficina.getTipus() == null)
        {
            throw new BadRequestException("El camp tipus és obligatori");
        }

        if (novaOficina.getPreu() <= 0)
        {
            throw new BadRequestException("El camp preu és obligatori i no pot ser igual o inferior a 0");
        }

    }
    
    /**
     * Valida que s'informi de tots els camps obligatoris d'una oficina editada donada.
     * @param novaOficina dades de la nova Oficina editada.
     */
    private void InicialitzarCampsNovaOficina(Oficina novaOficina) {
        novaOficina.setIdOficina(UUID.randomUUID().toString());
        novaOficina.setDataCreacio(new Date());

        if (novaOficina.isHabilitada() == null)
        {
            novaOficina.setHabilitada(true);
        }
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

    /**
     * Valida que s'informi de tots els camps obligatoris per eliminar una oficina.
     * @param idOficina identificador de l'oficina que es vol eliminar.
     */
    private void ValidarCampsObligatorisPeticioBaixaOficina(String idOficina) {
        if (idOficina == null || idOficina.isEmpty())
        {
            throw new BadRequestException("El camp id oficina és obligatori");
        }
    }
    
    /**
     * Elimina l'oficina que un usuari administrador vol dornar de baixa.
     * @param Oficina dades de l'oficina que es vol donar de baixa.
     */
    private void BaixaOficina(Oficina oficina) {
        /**
         * Estableix a true el valor del camp booleà eliminat.
         */
        oficina.setEliminat(true);
        /**
         * Actualitza la data d'edició de l'oficina.
         */
        oficina.setDataUltimaEdicio(new Date());
        /**
         * Actualitza la baixa de l'oficina al repositori d'oficines.
         */
        oficinaRepository.save(oficina);
        log.info("Donada de baixa l'oficina amb identificador " + oficina.getIdOficina());
    }

    private void ValidarCampsObligatorisEdicioOficina(OficinaVisualitzacio oficinaEditada) {

        if (oficinaEditada.getIdOficina() == null || oficinaEditada.getIdOficina().isEmpty())
        {
            throw new BadRequestException("El camp id oficina és obligatori");
        }
    }

    private Oficina ValidarOficinaExistent(String idOficina) {

        Oficina oficina = oficinaRepository.findByIdOficina(idOficina);

        if (oficina == null)
        {
            throw new OficinaNotFoundException(idOficina);
        }

        return oficina;
    }

    private OficinaVisualitzacio ActualitzarOficina(Oficina oficina, OficinaVisualitzacio oficinaEditada) {

        if (oficinaEditada.getNom() != null && !oficinaEditada.getNom().isEmpty())
        {
            oficina.setNom(oficinaEditada.getNom());
        }

        if (oficinaEditada.getCapacitat() != null && !oficinaEditada.getCapacitat().toString().isEmpty())
        {
            oficina.setCapacitat(oficinaEditada.getCapacitat());
        }


        if (oficinaEditada.getTipus() != null)
        {
            oficina.setTipus(oficinaEditada.getTipus());
        }

        if (oficinaEditada.getPreu() != null)
        {
            if (oficinaEditada.getPreu() <= 0)
            {
                throw new BadRequestException("El valor del preu no pot ser negatiu");
            }
            oficina.setPreu(oficinaEditada.getPreu());
        }

        if (oficinaEditada.getServeis() != null && !oficinaEditada.getServeis().isEmpty())
        {
            oficina.setServeis(oficinaEditada.getServeis());
        }

        if (oficinaEditada.getHabilitada() != null && oficinaEditada.getHabilitada() != oficina.isHabilitada())
        {
            oficina.setHabilitada(oficinaEditada.getHabilitada());
        }

        if (oficinaEditada.getProvincia() != null && !oficinaEditada.getProvincia().isEmpty())
        {
            oficina.setProvincia(oficinaEditada.getProvincia());
        }
        
        if (oficinaEditada.getPoblacio() != null && !oficinaEditada.getPoblacio().isEmpty())
        {
            oficina.setPoblacio(oficinaEditada.getPoblacio());
        }
        
        if (oficinaEditada.getDireccio() != null && !oficinaEditada.getDireccio().isEmpty())
        {
            oficina.setDireccio(oficinaEditada.getDireccio());
        }
        
        oficina.setDataUltimaEdicio(new Date());

        oficinaRepository.save(oficina);

        return mapper.OficinaAMostrar(oficina);
    }

}
