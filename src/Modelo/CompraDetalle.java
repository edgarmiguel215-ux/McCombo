package Modelo;

import java.util.Date;


public class CompraDetalle {
  
    
    private int idCompra;
    private int idArticulo;
    private String articulo;
    private int cantidad;
    private double precio;
    private double subtotal;
    private String proveedor;
    private String comprobante;
    private String metodoPago;
    private Date fecha;
    private String estado;
    private String numero;
    private String unidad;    // gramos, kilos, litros, etc.
    private int idInventario;
    


    
    public CompraDetalle() {
    }

    public CompraDetalle(int idCompra, int idArticulo, String articulo, int cantidad, double precio, double subtotal, String proveedor, String comprobante, String metodoPago, Date fecha, String estado, String numero, String unidad, int idInventario) {
        this.idCompra = idCompra;
        this.idArticulo = idArticulo;
        this.articulo = articulo;
        this.cantidad = cantidad;
        this.precio = precio;
        this.subtotal = subtotal;
        this.proveedor = proveedor;
        this.comprobante = comprobante;
        this.metodoPago = metodoPago;
        this.fecha = fecha;
        this.estado = estado;
        this.numero = numero;
        this.unidad = unidad;
        this.idInventario = idInventario;
    }

    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public int getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(int idArticulo) {
        this.idArticulo = idArticulo;
    }

    public String getArticulo() {
        return articulo;
    }

    public void setArticulo(String articulo) {
        this.articulo = articulo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public int getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }
    
    
    

    
    
}
