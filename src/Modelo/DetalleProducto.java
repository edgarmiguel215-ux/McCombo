
package Modelo;


public class DetalleProducto {

    private int idDetalle;
    private String codigoProducto;
    private String nombreProducto;
    private String nombreArticulo;
    private int cantidad;
    private int idProducto;     // id del producto
    private int idArticulo;     // id del artículo
    
    public DetalleProducto(int idDetalle, int cantidad, int idProducto, int idArticulo) {
        this.idDetalle = idDetalle;
        this.cantidad = cantidad;
        this.idProducto = idProducto;
        this.idArticulo = idArticulo;
    }

    public DetalleProducto(int idDetalle, String codigoProducto, String nombreProducto, String nombreArticulo, int cantidad) {
        this.idDetalle = idDetalle;
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto;
        this.nombreArticulo = nombreArticulo;
        this.cantidad = cantidad;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(int idArticulo) {
        this.idArticulo = idArticulo;
    }
    

    

    public DetalleProducto() {
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getNombreArticulo() {
        return nombreArticulo;
    }

    public void setNombreArticulo(String nombreArticulo) {
        this.nombreArticulo = nombreArticulo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    
    
}

