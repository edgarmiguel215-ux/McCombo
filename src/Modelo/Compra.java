
package Modelo;


import java.util.Date;


public class Compra {
    
    private int id;
    private int idProveedor;
    private String comprobante;
    private String numero;
    private String metodoPago;
    private String proveedor;
    private Date fechaCompra;
    private String estado;
    private double total;

    public Compra() {
    }

    public Compra(int id, int idProveedor, String comprobante, String numero, String metodoPago, String proveedor, Date fechaCompra, String estado, double total) {
        this.id = id;
        this.idProveedor = idProveedor;
        this.comprobante = comprobante;
        this.numero = numero;
        this.metodoPago = metodoPago;
        this.proveedor = proveedor;
        this.fechaCompra = fechaCompra;
        this.estado = estado;
        this.total = total;
    }

    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public Date getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(Date fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    
}
