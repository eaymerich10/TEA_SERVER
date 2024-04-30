/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package femcoworking.servidor.Models;


public class PeticioReservaOficina {
    private String codiAcces;
    private String idOficina;
    private String dataIniciReserva;
    private String dataFiReserva;

    public String getCodiAcces() {
        return codiAcces;
    }

    public void setCodiAcces(String codiAcces) {
        this.codiAcces = codiAcces;
    }  

    public String getIdOficina() {
        return idOficina;
    }

    public void setIdOficina(String idOficina) {
        this.idOficina = idOficina;
    }

    public String getDataIniciReserva() {
        return dataIniciReserva;
    }

    public void setDataIniciReserva(String dataIniciReserva) {
        this.dataIniciReserva = dataIniciReserva;
    }

    public String getDataFiReserva() {
        return dataFiReserva;
    }

    public void setDataFiReserva(String dataFiReserva) {
        this.dataFiReserva = dataFiReserva;
    }
}
    
