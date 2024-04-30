package femcoworking.servidor.Models;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;

/**
 * Representa un usuari de l'aplicació.
 * 
 */
@Entity
@Table(name = "Usuaris")
public class Usuari {
    /**
     * Identificador únic de l'id d'usuari
     */
    @Id
    private String idUsuari;
    /**
     * Email de l'ususari. Camp obligatori.
     */
    @NotNull
    @Email
    private String email;
    /**
     * Contrasenya de l'ususari. Camp obligatori.
     */
    @NotNull
    private String contrasenya;
    /**
     * Rol de l'ususari. Camp obligatori.
     */
    @NotNull
    private Rol rol;
    /**
     * Nom i cognoms de l'ususari.
     */
    private String nom;
    /**
     * Cif de l'empresa a la que pertany l'usuari que es registra.
     */
    private String cifEmpresa;
    /**
     * Direcció de l'empresa a la que pertany l'usuari que es registra.
     */
    private String direccio;
    /**
     * Direcció de l'empresa a la que pertany l'usuari que es registra.
     */
    private String poblacio;
    /**
     * Provincia de l'empresa a la que pertany l'usuari que es registra.
     */
    private String provincia;
    /**
     * Data de creació del registre d'un usuari.
     */
    private Date dataCreacio;
    /**
     * Data de l'ultim acces d'un de l'usuari.
     */
    private Date ultimAcces;
    /**
     * Està deshabilitat l'usuari?
     */
    private boolean deshabilitat;

    public Usuari() { }

    public Usuari(String idUsuari) {
        this.idUsuari = idUsuari;
    }

    public String getIdUsuari() {
        return idUsuari;
    }

    public void setIdUsuari(String idUsuari) {
        this.idUsuari = idUsuari;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasenya() {
        return contrasenya;
    }

    public void setContrasenya(String contrasenya) {
        this.contrasenya = contrasenya;
    }

    public String getCifEmpresa() {
        return cifEmpresa;
    }

    public void setCifEmpresa(String cifEmpresa) {
        this.cifEmpresa = cifEmpresa;
    }

    public String getDireccio() {
        return direccio;
    }

    public void setDireccio(String direccio) {
        this.direccio = direccio;
    }

    public String getPoblacio() {
        return poblacio;
    }

    public void setPoblacio(String poblacio) {
        this.poblacio = poblacio;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public Date getDataCreacio() {
        return dataCreacio;
    }

    public void setDataCreacio(Date dataCreacio) {
        this.dataCreacio = dataCreacio;
    }

    public Date getUltimAcces() {
        return ultimAcces;
    }

    public void setUltimAcces(Date ultimAcces) {
        this.ultimAcces = ultimAcces;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public boolean isDeshabilitat() {
        return deshabilitat;
    }

    public void setDeshabilitat(boolean usuariDeshabilitat) {
        this.deshabilitat = usuariDeshabilitat;
    }
}
