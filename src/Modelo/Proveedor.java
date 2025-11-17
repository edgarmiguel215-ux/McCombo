
package Modelo;

public class Proveedor {
    
    private int id;
    private String nombre;
    private String tipo;
    private String numeroDocumento;
    private String telefono;
    private String direccion;
    private String razon;
    
    

    public Proveedor() {
    }

    public Proveedor(int id, String nombre, String tipo, String numeroDocumento, String telefono, String direccion, String razon) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.numeroDocumento = numeroDocumento;
        this.telefono = telefono;
        this.direccion = direccion;
        this.razon = razon;
    }

    
    
    @Override
    public String toString() {
    return this.nombre; // igual, el campo que quieras mostrar
}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getRazon() {
        return razon;
    }

    public void setRazon(String razon) {
        this.razon = razon;
    }

}