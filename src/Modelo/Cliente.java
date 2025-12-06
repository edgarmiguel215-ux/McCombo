
package Modelo;


public class Cliente {
    private int idCliente;
    private String nombre;
    private int idTicket;
    private String rutaPDF;
    

    public Cliente() {
    }

    public Cliente(String nombre) {
        this.nombre = nombre;
    }

    
    public Cliente(int idCliente, String nombre) {
        this.idCliente = idCliente;
        this.nombre = nombre;
    }

    public Cliente(int idCliente, String nombre, int idTicket, String rutaPDF) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.idTicket = idTicket;
        this.rutaPDF = rutaPDF;
    }

    public int getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(int idTicket) {
        this.idTicket = idTicket;
    }

    public String getRutaPDF() {
        return rutaPDF;
    }

    public void setRutaPDF(String rutaPDF) {
        this.rutaPDF = rutaPDF;
    }
    
    

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    
    
}
