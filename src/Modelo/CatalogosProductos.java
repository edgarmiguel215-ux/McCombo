
package Modelo;


public class CatalogosProductos {
  
    private String nombre;
    private int cantidadSeleccionada;
    private double precio;

    public CatalogosProductos() {
    }

    public CatalogosProductos(String nombre, int cantidadSeleccionada, double precio) {
        this.nombre = nombre;
        this.cantidadSeleccionada = cantidadSeleccionada;
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidadSeleccionada() {
        return cantidadSeleccionada;
    }

    public void setCantidadSeleccionada(int cantidadSeleccionada) {
        this.cantidadSeleccionada = cantidadSeleccionada;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
    
    
}
