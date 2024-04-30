package femcoworking.servidor.Models;

/**
 * Representa les dades d'acces d'un usuari per inicar sessió
 * 
 */
public class DadesAcces {
    /**
     * Email de l'usuari que vol iniciar sessió.
     */
    private String email;
    /**
     * Contrasenya de l'usuari que vol iniciar sessió.
     */
    private String contrasenya;

    public String getEmail() {
        return email;
    }
    public String getContrasenya() {
        return contrasenya;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setContrasenya(String contrasenya) {
        this.contrasenya = contrasenya;
    }
}
