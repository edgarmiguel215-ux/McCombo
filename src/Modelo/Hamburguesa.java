
package Modelo;

import java.util.List;


public class Hamburguesa {
    
    private String nombre;
    private double precio;
    private List<String> ingredientes;
    private String rutaImagen;
    private String descripcion;
    private int id;
    private String imagen;
    
    public Hamburguesa(String nombre, double precio, List<String> ingredientes, String rutaImagen) {
        this.nombre = nombre;
        this.precio = precio;
        this.ingredientes = ingredientes;
        this.rutaImagen = rutaImagen;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public List<String> getIngredientes() {
        return ingredientes;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public Hamburguesa(String descripcion, int id, String imagen) {
        this.descripcion = descripcion;
        this.id = id;
        this.imagen = imagen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
    
    
        
}
