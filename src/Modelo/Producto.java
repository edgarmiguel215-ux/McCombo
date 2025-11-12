
package Modelo;


public class Producto {

    private int id;
    private String codigo;
    private String nombre;
    private double precio;
    private String categoria;

    public Producto() {
    }

    public Producto(int id, String codigo, String nombre, double precio, String categoria) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
    }
    
    @Override
public String toString() {
    return nombre; // o getNombre()
}


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

   

    
}

