
package Modelo;

import java.sql.*;
public class TicketDAO {
 
  
    private Conexion conexion;

    public TicketDAO() {
        conexion = new Conexion();
    }

    // REGISTRAR TICKET Y DEVOLVER EL ID GENERADO
    public int registrarTicket(int idCliente, int idUsuario, double total, String metodoPago) {
        int idTicket = 0;
        String sql = "INSERT INTO tickets (id_cliente, id_usuario, total, metodo_pago, fecha) VALUES (?, ?, ?, ?, NOW())";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, idCliente);
            ps.setInt(2, idUsuario);
            ps.setDouble(3, total);
            ps.setString(4, metodoPago);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idTicket = rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error al registrar ticket: " + e.getMessage());
        }

        return idTicket;
    }
    
    // ACTUALIZAR RUTA DEL PDF
    public boolean actualizarRutaPDF(int idTicket, String ruta) {
        String sql = "UPDATE tickets SET ruta_pdf = ? WHERE id_ticket = ?";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, ruta);
            ps.setInt(2, idTicket);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar ruta PDF: " + e.getMessage());
            return false;
        }
    }
    
    // OBTENER LA RUTA PDF (si necesitas abrir el ticket después)
    public String obtenerRutaPDF(int idTicket) {
        String ruta = "";
        String sql = "SELECT ruta_pdf FROM tickets WHERE id_ticket = ?";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idTicket);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) ruta = rs.getString(1);

        } catch (SQLException e) {
            System.err.println(" Error al obtener ruta PDF: " + e.getMessage());
        }

        return ruta;
    }

}

