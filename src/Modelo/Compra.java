
package Modelo;


import java.util.Date;


public class Compra {
       
    private int id;
    private int idArticulo;
    private int idProveedor;
    private String articulo;
    private int cantidad;
    private String unidad;
    private double precioUnitario;
    private double total;
    private String comprobante;
    private String metodoPago;
    private String datosPago;
    private Date fecha;
    private String estado;
    private String proveedor;
    
    
    
    
    public Compra() {
    }

    public Compra(int id, int idArticulo, int idProveedor, String articulo, int cantidad, String unidad, double precioUnitario, double total, String comprobante, String metodoPago, String datosPago, Date fecha, String estado, String proveedor) {
        this.id = id;
        this.idArticulo = idArticulo;
        this.idProveedor = idProveedor;
        this.articulo = articulo;
        this.cantidad = cantidad;
        this.unidad = unidad;
        this.precioUnitario = precioUnitario;
        this.total = total;
        this.comprobante = comprobante;
        this.metodoPago = metodoPago;
        this.datosPago = datosPago;
        this.fecha = fecha;
        this.estado = estado;
        this.proveedor = proveedor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(int idArticulo) {
        this.idArticulo = idArticulo;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
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

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
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

    public String getDatosPago() {
        return datosPago;
    }

    public void setDatosPago(String datosPago) {
        this.datosPago = datosPago;
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

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    
    
    
    
}
