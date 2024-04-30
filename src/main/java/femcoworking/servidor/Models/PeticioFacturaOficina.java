package femcoworking.servidor.Models;

import femcoworking.servidor.Models.Factura;

/**
 * Representa les dades necesaries per fer una petició d'alta d'una factura
 * per part d'un usuari client.
 * 
 */
public class PeticioFacturaOficina {
    private String codiAcces;
    private String idFactura;
    private String idReserva;

    public String getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
    }     

    public String getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(String idReserva) {
        this.idReserva = idReserva;
    }

    public String getCodiAcces() {
        return codiAcces;
    }

    public void setCodiAcces(String codiAcces) {
        this.codiAcces = codiAcces;
    }

  
 
}