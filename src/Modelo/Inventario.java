
package Modelo;


public class Inventario {
    
    private int idCompra;
    private String nombre;
    private String unidad;
    private int stockActual;
    private double costoUnitario;
    private double valorInventario;
    private String estado;
    private String imagen;
    private int idArticulo;
    private int cantidadComprada;
    private int idInventario;

    public Inventario() {
    }

   
    
    public Inventario(int idCompra, String nombre, String unidad, int stockActual, double costoUnitario, double valorInventario, String estado, String imagen, int idArticulo, int cantidadComprada, int idInventario) {
        this.idCompra = idCompra;
        this.nombre = nombre;
        this.unidad = unidad;
        this.stockActual = stockActual;
        this.costoUnitario = costoUnitario;
        this.valorInventario = valorInventario;
        this.estado = estado;
        this.imagen = imagen;
        this.idArticulo = idArticulo;
        this.cantidadComprada = cantidadComprada;
        this.idInventario = idInventario;
    }

    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
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

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public double getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(double costoUnitario) {
        this.costoUnitario = costoUnitario;
    }

    public double getValorInventario() {
        return valorInventario;
    }

    public void setValorInventario(double valorInventario) {
        this.valorInventario = valorInventario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(int idArticulo) {
        this.idArticulo = idArticulo;
    }

    public int getCantidadComprada() {
        return cantidadComprada;
    }

    public void setCantidadComprada(int cantidadComprada) {
        this.cantidadComprada = cantidadComprada;
    }

    public int getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }
    
    
    
}