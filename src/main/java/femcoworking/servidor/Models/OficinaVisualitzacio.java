/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package femcoworking.servidor.Models;


public class OficinaVisualitzacio {
    private String idOficina;
    private String nom;
    private Categoria tipus;
    private Integer capacitat;
    private Double preu;
    private String serveis;
    private Boolean habilitada;
    private String provincia;
    private String poblacio;
    private String direccio;
    private Boolean eliminat;

    public OficinaVisualitzacio(
        String idOficina,
        String nom, 
        Categoria tipus, 
        Integer capacitat, 
        Double preu, 
        String serveis, 
        Boolean habilitada, 
        String provincia, 
        String poblacio, 
        String direccio,
        Boolean eliminat
    ) {
        this.idOficina = idOficina;
        this.nom = nom;
        this.tipus = tipus;
        this.capacitat = capacitat;
        this.preu = preu;
        this.serveis = serveis;
        this.habilitada = habilitada;
        this.provincia = provincia;
        this.poblacio = poblacio;
        this.direccio = direccio;
        this.eliminat = eliminat;
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

    public void setTipus(Categoria tipus) {
        this.tipus = tipus;
    }

    public Integer getCapacitat() {
        return capacitat;
    }

    public void setCapaditat(Integer capacitat) {
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

    public Boolean getHabilitada() {
        return habilitada;
    }

    public void setHabilitada(Boolean habilitada) {
        this.habilitada = habilitada;
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
