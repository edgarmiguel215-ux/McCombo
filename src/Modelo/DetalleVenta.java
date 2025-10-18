
package Modelo;


public class DetalleVenta {
     private int id;
    private int id_venta;
    private int idProducto;
    private int cantidad;
    private double precio;

    // --- GETTERS (Métodos para LEER los valores) ---

    public int getId() {
        return id;
    }

    public int getId_venta() {
        return id_venta;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public int getCantidad() { // <-- El método que te faltaba
        return cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    // --- SETTERS (Métodos para MODIFICAR los valores) ---

    public void setId(int id) {
        this.id = id;
    }

    public void setIdVenta(int id_venta) {
        this.id_venta = id_venta;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public void setCantidad(int cantidad) { // <-- Su pareja, el "setter"
        this.cantidad = cantidad;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}
