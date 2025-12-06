
package Modelo;

import Modelo.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetalleTicketDAO {
    

    private Conexion conexion;

    public DetalleTicketDAO() {
        conexion = new Conexion();
    }

    public void registrarDetalle(int idTicket, int idProducto, int cantidad, double precioUnitario, double subtotal) {
        try (Connection con = conexion.getConnection()) {
            PreparedStatement stmt = con.prepareStatement(
                "INSERT INTO detalle_Ticket (id_ticket, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)"
            );
            stmt.setInt(1, idTicket);
            stmt.setInt(2, idProducto);
            stmt.setInt(3, cantidad);
            stmt.setDouble(4, precioUnitario);
            stmt.setDouble(5, subtotal);

            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error al registrar detalle: " + e.getMessage());
        }
    }
    
    
    public List<Object[]> obtenerDetallesPorTicket(int idTicket) {
    List<Object[]> lista = new ArrayList<>();

    String sql = "SELECT id_producto, cantidad, precio_unitario, subtotal "
               + "FROM detalle_ticket WHERE id_ticket = ?";

    try (Connection con = conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idTicket);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            lista.add(new Object[]{
                rs.getInt("id_producto"),
                rs.getInt("cantidad"),
                rs.getDouble("precio_unitario"),
                rs.getDouble("subtotal")
            });
        }

    } catch (SQLException e) {
        System.err.println("Error obtenerDetallesPorTicket: " + e.getMessage());
    }

    return lista;
}

}

