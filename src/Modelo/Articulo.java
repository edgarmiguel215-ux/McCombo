
package Modelo;


public class Articulo {

    private int idArticulo;
    private String nombre;
    private String unidad;
    private int precio;
    private int id;

    public Articulo() {
    }

    public Articulo(int idArticulo, String nombre, String unidad) {
        this.idArticulo = idArticulo;
        this.nombre = nombre;
        this.unidad = unidad;
    }
    

    public Articulo(int precio) {
        this.precio = precio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }
    
    

    public int getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(int idArticulo) {
        this.idArticulo = idArticulo;
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

    
    

    @Override
    public String toString() {
        return nombre;
    }
}

