/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package femcoworking.servidor.Models;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;



@Entity
@Table(name = "Reserva_oficina")
public class Reserva {
    @Id
    private String idReserva;
    @NotNull
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date dataIniciReserva;
    @NotNull
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date dataFiReserva;
    @ManyToOne
    @JoinColumn(name = "idOficina", referencedColumnName = "idOficina")
    private Oficina idOficina;
    @ManyToOne
    @JoinColumn(name = "idUsuari", referencedColumnName = "idUsuari")
    private Usuari idUsuari;
     
    public String getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(String idReserva) {
        this.idReserva = idReserva;
    }
    
    public Oficina getIdOficina() {
        return idOficina;
    }

    public void setIdOficina(Oficina idOficina) {
        this.idOficina = idOficina;
    }

    public Usuari getIdUsuari() {
        return idUsuari;
    }

    public void setIdUsuari(Usuari idUsuari) {
        this.idUsuari = idUsuari;
    }
      
    public Date getDataIniciReserva() {
        return dataIniciReserva;
    }

    public void setDataIniciReserva(Date data_inici_reserva) {
        this.dataIniciReserva = data_inici_reserva;
    }
    
    public Date getDataFiReserva() {
        return dataFiReserva;
    }

    public void setDataFiReserva(Date data_fi_reserva) {
        this.dataFiReserva = data_fi_reserva;
    }
    
 
    
}