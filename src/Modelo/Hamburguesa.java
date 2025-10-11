
package Modelo;

import java.util.List;


public class Hamburguesa {
    
    private String nombre;
    private double precio;
    private List<String> ingredientes;
    private String rutaImagen;

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
    
    
        
}
