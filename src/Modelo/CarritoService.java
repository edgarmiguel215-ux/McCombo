
package Modelo;

import java.util.ArrayList;
import java.util.List;


public class CarritoService {
    // El único carrito que existirá en toda la aplicación
    private static CarritoService instancia;
    private List<DetalleVenta> productos;

    // El constructor es privado para que nadie más pueda crear carritos
    private CarritoService() {
        productos = new ArrayList<>();
    }

    // El método público para obtener la única instancia del carrito
    public static CarritoService getInstance() {
        if (instancia == null) {
            instancia = new CarritoService();
        }
        return instancia;
    }

    /**
     * Agrega un producto al carrito. Si el producto ya existe, actualiza su cantidad.
     * Si no existe, lo añade como un nuevo item.
     * @param nuevoDetalle El producto que se quiere agregar.
     */
    public void agregarProducto(DetalleVenta nuevoDetalle) {
        // Recorremos la lista actual de productos en el carrito
        for (DetalleVenta detalleExistente : this.productos) {
            
            // Comparamos el ID del producto existente con el del nuevo producto
            if (detalleExistente.getIdProducto() == nuevoDetalle.getIdProducto()) {
                
                // Si ya existe, calculamos la nueva cantidad
                int nuevaCantidad = detalleExistente.getCantidad() + nuevoDetalle.getCantidad();
                
                // Actualizamos la cantidad en el producto que ya estaba en la lista
                detalleExistente.setCantidad(nuevaCantidad);
                
                // Salimos del método porque ya terminamos nuestra tarea
                return; 
            }
        }
        
        // Si el bucle termina y nunca encontramos el producto, significa que es nuevo.
        // Entonces, lo agregamos a la lista.
        this.productos.add(nuevoDetalle);
    }

    public List<DetalleVenta> getProductos() {
        return this.productos;
    }
    
    public void vaciarCarrito() {
        this.productos.clear();
    }
}
