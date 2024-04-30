/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package femcoworking.servidor.Models;

import java.util.Date;


public class ReservaVisualitzacio {
    private String idReserva;
    private Date data_inici_reserva;
    private Date data_fi_reserva;
    private String idOficina;
    private String idUsuari;
   

    public ReservaVisualitzacio(String idReserva, Date data_inici_reserva, Date data_fi_reserva, String idOficina, String idUsuari) {
        this.idReserva = idReserva;
        this.idUsuari = idUsuari;
        this.data_inici_reserva = data_inici_reserva;
        this.data_fi_reserva = data_fi_reserva;
        this.idOficina = idOficina;
        this.idUsuari = idUsuari;
        
        
    }

    public String getIdOficina() {
        return idOficina;
    }

    public void setIdOficina(String idOficina) {
        this.idOficina = idOficina;
    }
    
     public String getIdUsuari() {
        return idUsuari;
    }

    public void setIdUsuari(String idUsuari) {
        this.idUsuari = idUsuari;
    }


    public String getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(String idReserva) {
        this.idReserva = idReserva;
    }

    public Date getDataIniciReserva() {
        return data_inici_reserva;
    }

    public void setDataIniciReserva(Date data_inici_reserva) {
        this.data_inici_reserva = data_inici_reserva;
    }
    
    public Date getDataFiReserva() {
        return data_fi_reserva;
    }

    public void setDataFiReserva(Date data_fi_reserva) {
        this.data_fi_reserva = data_fi_reserva;
    }
    
    
}
