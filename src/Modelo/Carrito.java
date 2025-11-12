
package Modelo;

import java.util.ArrayList;
import java.util.List;

public class Carrito {

    private List<CarritoItem> items = new ArrayList<>();

    public void agregarItem(CarritoItem item) {
        for (CarritoItem i : items) {
            if (i.getIdProducto() == item.getIdProducto()) {
                i.setCantidad(i.getCantidad() + item.getCantidad());
                return;
            }
        }
        items.add(item);
    }

    public void quitarItem(int idProducto) {
        items.removeIf(i -> i.getIdProducto() == idProducto);
    }

    public void cambiarCantidad(int idProducto, int nuevaCantidad) {
        for (CarritoItem i : items) {
            if (i.getIdProducto() == idProducto) {
                if (nuevaCantidad <= 0) {
                    quitarItem(idProducto);
                } else {
                    i.setCantidad(nuevaCantidad);
                }
                return;
            }
        }
    }

    public List<CarritoItem> getItems() {
        return items;
    }

    public double getTotal() {
        return items.stream().mapToDouble(CarritoItem::getSubtotal).sum();
    }

    public void clear() {
        items.clear();
    }
}
