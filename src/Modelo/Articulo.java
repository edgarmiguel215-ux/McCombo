
package Modelo;


public class Articulo {

   
    private String nombre;
    private String unidad;
    private double precio;
    private int idArticulo;
    
    
    public Articulo() {
    }

    public Articulo(String nombre, String unidad, double precio, int idArticulo) {
        this.nombre = nombre;
        this.unidad = unidad;
        this.precio = precio;
        this.idArticulo = idArticulo;
    }
    
    @Override
    public String toString() {
    return this.nombre; // o como se llame tu campo de nombre
}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(int idArticulo) {
        this.idArticulo = idArticulo;
    }

}

