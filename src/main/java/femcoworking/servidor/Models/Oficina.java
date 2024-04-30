package femcoworking.servidor.Models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;



@Entity
@Table(name = "Oficines")
public class Oficina {
    @Id
    private String idOficina;
    @NotNull
    private String nom;
    @NotNull
    private Categoria tipus;
    @NotNull
    private Integer capacitat;
    @NotNull
    private Double preu;
    private String serveis;
    private Boolean habilitada;
    private String provincia;
    private String poblacio;
    private String direccio;
    private Date dataCreacio;
    private Date dataUltimaEdicio;
    private Boolean eliminat;

    public Oficina() {
    }

    public Oficina(String idOficina) {
        this.idOficina = idOficina;
    }

    public String getIdOficina() {
        return idOficina;
    }

    public void setIdOficina(String idOficina) {
        this.idOficina = idOficina;
    }



    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public Categoria getTipus() {
        return tipus;
    }

    public void setTipus(Categoria categoria) {
        this.tipus = categoria;
    }
    
    public Integer getCapacitat() {
        return capacitat;
    }

    public void setCapacitat(Integer capacitat) {
        this.capacitat = capacitat;
    }

    public Double getPreu() {
        return preu;
    }

    public void setPreu(Double preu) {
        this.preu = preu;
    }

    public String getServeis() {
        return serveis;
    }

    public void setServeis(String serveis) {
        this.serveis = serveis;
    }

    public Boolean isHabilitada() {
        return habilitada;
    }

    public void setHabilitada(Boolean habilitada) {
        this.habilitada = habilitada;
    }

    public Date getDataCreacio() {
        return dataCreacio;
    }

    public void setDataCreacio(Date dataCreacio) {
        this.dataCreacio = dataCreacio;
    }

    public Date getDataUltimaEdicio() {
        return dataUltimaEdicio;
    }

    public void setDataUltimaEdicio(Date dataUltimaEdicio) {
        this.dataUltimaEdicio = dataUltimaEdicio;
    }

   public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }
 
    public String getPoblacio() {
        return poblacio;
    }

    public void setPoblacio(String poblacio) {
        this.poblacio = poblacio;
    }
    
     public String getDireccio() {
        return direccio;
    }

    public void setDireccio(String direccio) {
        this.direccio = direccio;
    }
    public Boolean getEliminat() {
        return eliminat;
    }

    public void setEliminat(Boolean eliminat) {
        this.eliminat = eliminat;
    }  
    
    
}
