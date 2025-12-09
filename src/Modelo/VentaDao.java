
package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author salga
 */
public class VentaDao {
    
    
    // ✅ Método para registrar venta con conexión interna
    public int registrarVenta(Venta venta) {

        String sql = "INSERT INTO ventas (cliente, Vendedor, total, fecha) VALUES (?,?,?,?)";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, venta.getId_cliente());   // ID del cliente (INT)
            ps.setString(2, venta.getVendedor());
            ps.setDouble(3, venta.getTotal());
            ps.setString(4, venta.getFecha());

            int filas = ps.executeUpdate();

            if (filas == 0) {
                System.out.println("❌ No se pudo registrar la venta.");
                return -1;
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error SQL al registrar la venta: " + e.getMessage());
        }

        return -1;
    }


    // ✅ Método para registrar detalle con conexión interna
    // ✅ Método para registrar detalle sin duplicados
    public void registrarDetalle(DetalleVenta detalle) {
    String sql = "INSERT INTO detalle_ticket (id_ticket, id_producto, cantidad, precio_unitario, subtotal) " +
                 "VALUES (?,?,?,?,?) " +
                 "ON DUPLICATE KEY UPDATE " +
                 "cantidad = cantidad + VALUES(cantidad), " +
                 "subtotal = subtotal + VALUES(subtotal)";

    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, detalle.getId_venta());          // ID del ticket
        ps.setInt(2, detalle.getIdProducto());        // ID del producto
        ps.setInt(3, detalle.getCantidad());          // Cantidad vendida
        ps.setDouble(4, detalle.getPrecio());         // Precio unitario
        ps.setDouble(5, detalle.getCantidad() * detalle.getPrecio()); // Subtotal

        int filas = ps.executeUpdate();
        if (filas > 0) {
            System.out.println("✅ Detalle registrado/actualizado correctamente para venta ID: " + detalle.getId_venta());
        } else {
            System.out.println("❌ Error: No se pudo registrar el detalle de la venta.");
        }
    } catch (SQLException e) {
        System.out.println("❌ Error SQL al registrar detalle: " + e.getMessage());
    }
}

//    public void registrarDetalle(DetalleVenta detalle) {
//        String sql = "INSERT INTO detalle_ticket (id_ticket, id_producto, cantidad, precio_unitario, subtotal) VALUES (?,?,?,?,?)";
//        try (Connection con = Conexion.getConnection();
//             PreparedStatement ps = con.prepareStatement(sql)) {
//
//            ps.setInt(1, detalle.getId_venta());
//            ps.setInt(2, detalle.getIdProducto());
//            ps.setInt(3, detalle.getCantidad());
//            ps.setDouble(4, detalle.getPrecio());
//            ps.setDouble(5, detalle.getCantidad() * detalle.getPrecio());
//
//            int filas = ps.executeUpdate();
//            if (filas > 0) {
//                System.out.println("✅ Detalle registrado correctamente para venta ID: " + detalle.getId_venta());
//            } else {
//                System.out.println("❌ Error: No se pudo registrar el detalle de la venta.");
//            }
//        } catch (SQLException e) {
//            System.out.println("❌ Error SQL al registrar detalle: " + e.getMessage());
//        }
//    }




}
