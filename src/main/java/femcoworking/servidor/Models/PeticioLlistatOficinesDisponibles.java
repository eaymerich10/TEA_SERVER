/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package femcoworking.servidor.Models;


public class PeticioLlistatOficinesDisponibles {
    private String dataInici;
    private String dataFi;

    public String getDataInici() {
        return dataInici;
    }

    public void setDataInici(String dataInici) {
        this.dataInici = dataInici + " 00:00:00";
    }

    public String getDataFi() {
        return dataFi;
    }

    public void setDataFi(String dataFi) {
        this.dataFi = dataFi + " 23:59:59";
    }
    
    
}
