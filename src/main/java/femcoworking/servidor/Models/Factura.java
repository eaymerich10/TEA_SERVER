package femcoworking.servidor.Models;

import femcoworking.servidor.Models.Usuari;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;



@Entity
@Table(name = "Factures")
public class Factura {
    @Id
    private String idFactura;
    
    @ManyToOne
    @JoinColumn(name = "idReserva", referencedColumnName = "idReserva")
    private Reserva idReserva;
    
    @ManyToOne
    @JoinColumn(name = "idUsuari", referencedColumnName = "idUsuari")
    private Usuari idUsuari;
    
    private String nomUsuariReserva;
    
    private String nomOficina;
    
    private Categoria tipusOficina;
    
    private Double preuOficina;
    
    private String serveisOficina;
    
    private Date data_inici_reserva;
    
    private Date data_fi_reserva;
    
    private Double subTotal;
    
    private Double impostos;
    
    private Double total;
    
    private Date dataCreacio;
    
    public Factura() {
    }
    

    public String getNomUsuariReserva() {
        return nomUsuariReserva;
    }

    public void setNomUsuariReserva(String nomUsuariReserva) {
        this.nomUsuariReserva = nomUsuariReserva;
    }

    public String getNomOficina() {
        return nomOficina;
    }

    public void setNomOficina(String nomOficina) {
        this.nomOficina = nomOficina;
    }

    public Categoria getTipusOficina() {
        return tipusOficina;
    }

    public void setTipusOficina(Categoria tipusOficina) {
        this.tipusOficina = tipusOficina;
    }

    public Double getPreuOficina() {
        return preuOficina;
    }

    public void setPreuOficina(Double preuOficina) {
        this.preuOficina = preuOficina;
    }

    public String getServeisOficina() {
        return serveisOficina;
    }

    public void setServeisOficina(String serveisOficina) {
        this.serveisOficina = serveisOficina;
    }

    public Date getData_inici_reserva() {
        return data_inici_reserva;
    }

    public void setData_inici_reserva(Date data_inici_reserva) {
        this.data_inici_reserva = data_inici_reserva;
    }

    public Date getData_fi_reserva() {
        return data_fi_reserva;
    }

    public void setData_fi_reserva(Date data_fi_reserva) {
        this.data_fi_reserva = data_fi_reserva;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }

    public Double getImpostos() {
        return impostos;
    }

    public void setImpostos(Double impostos) {
        this.impostos = impostos;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
    

    public Usuari getIdUsuari() {
        return idUsuari;
    }

    public void setIdUsuari(Usuari idUsuari) {
        this.idUsuari = idUsuari;
    }
    
  
    public Factura(String idFactura) {
        this.idFactura = idFactura;
    }
    
    public Date getDataCreacio() {
        return dataCreacio;
    }

    public void setDataCreacio(Date dataCreacio) {
        this.dataCreacio = dataCreacio;
    }
     
    public String getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
    }
    
    public Reserva getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Reserva idReserva) {
        this.idReserva = idReserva;
    }
    
}
    
    
