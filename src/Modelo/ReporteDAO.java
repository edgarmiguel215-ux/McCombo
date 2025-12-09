
package Modelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.*;


public class ReporteDAO {
   
    private Conexion conexion;

    public ReporteDAO() {
        conexion = new Conexion();
    }
    
    public List<Object[]> ventasPorRango(Date desde, Date hasta) {
    List<Object[]> lista = new ArrayList<>();
    String sql = "SELECT DATE(fecha), SUM(total), COUNT(*) " +
                 "FROM tickets WHERE fecha BETWEEN ? AND ? " +
                 "GROUP BY DATE(fecha) ORDER BY fecha DESC";

    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setDate(1, new java.sql.Date(desde.getTime()));
        ps.setDate(2, new java.sql.Date(hasta.getTime()));

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            lista.add(new Object[]{
                rs.getDate(1),
                rs.getDouble(2),
                rs.getInt(3)
            });
        }

    } catch (SQLException e) {
        System.err.println("Error en ventasPorRango: " + e.getMessage());
    }
    return lista;
}
    
    public List<Object[]> ventasPorUsuario() {
    List<Object[]> lista = new ArrayList<>();
    String sql = "SELECT u.nombre, SUM(t.total), COUNT(t.id_ticket) " +
                 "FROM tickets t JOIN usuarios u ON t.id_usuario = u.id " +
                 "GROUP BY u.nombre ORDER BY SUM(t.total) DESC";

    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            lista.add(new Object[]{
                rs.getString(1),  // nombre del usuario
                rs.getDouble(2),  // total ventas
                rs.getInt(3)      // cantidad de tickets
            });
        }

    } catch (SQLException e) {
        System.err.println("Error en ventasPorUsuario: " + e.getMessage());
    }
    return lista;
}

    
    // ✅ Productos más vendidos sin duplicados
    public List<Object[]> productosMasVendidos() {
    List<Object[]> lista = new ArrayList<>();
    String sql = "SELECT p.nombre, SUM(dt.cantidad) AS cantidadVendida, SUM(dt.subtotal) AS ingresos " +
                 "FROM detalle_ticket dt " +
                 "INNER JOIN productos p ON dt.id_producto = p.id " +
                 "GROUP BY p.id, p.nombre " +
                 "ORDER BY cantidadVendida DESC";

    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            lista.add(new Object[]{
                rs.getString("nombre"),       // Producto
                rs.getInt("cantidadVendida"), // Cantidad Vendida
                rs.getDouble("ingresos")      // Ingresos Generados
            });
        }

    } catch (SQLException e) {
        System.err.println("❌ Error en productosMasVendidos: " + e.getMessage());
    }
    return lista;
}
    // 🔹 Ventas por Usuario (todos)
    public List<Object[]> ventasPorUsuarioBusqueda() {
    List<Object[]> lista = new ArrayList<>();
    String sql = "SELECT Vendedor, SUM(total) AS totalVentas, COUNT(id) AS cantidadTickets " +
                 "FROM ventas " +
                 "GROUP BY Vendedor";
    try {
        Connection con = conexion.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Object[] fila = {
                rs.getString("Vendedor"),
                rs.getDouble("totalVentas"),
                rs.getInt("cantidadTickets")
            };
            lista.add(fila);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return lista;
}

    // 🔹 Ventas por Usuario con filtro
    public List<Object[]> ventasPorUsuarioFiltro(String nombre) {
    List<Object[]> lista = new ArrayList<>();
    String sql = "SELECT Vendedor, SUM(total) AS totalVentas, COUNT(id) AS cantidadTickets " +
                 "FROM ventas " +
                 "WHERE Vendedor LIKE ? " +
                 "GROUP BY Vendedor";
    try {
        Connection con = conexion.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, "%" + nombre + "%"); // solo busca por nombre
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Object[] fila = {
                rs.getString("Vendedor"),
                rs.getDouble("totalVentas"),
                rs.getInt("cantidadTickets")
            };
            lista.add(fila);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return lista;
}

    // 🔹 Productos más vendidos (todos)
    public List<Object[]> productosMasVendidosBusqueda() {
    List<Object[]> lista = new ArrayList<>();
    String sql = "SELECT p.nombre, SUM(dt.cantidad) AS cantidadVendida, SUM(dt.subtotal) AS ingresos " +
                 "FROM detalle_ticket dt INNER JOIN productos p ON dt.id_producto = p.id " +
                 "GROUP BY p.id, p.nombre ORDER BY cantidadVendida DESC";
    try (Connection con = conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Object[] fila = {
                rs.getString("nombre"),
                rs.getInt("cantidadVendida"),
                rs.getDouble("ingresos")
            };
            lista.add(fila);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return lista;
}

    // 🔹 Productos más vendidos con filtro
    public List<Object[]> productosMasVendidosFiltro(String criterio) {
    List<Object[]> lista = new ArrayList<>();
    String sql = "SELECT p.nombre, SUM(dt.cantidad) AS cantidadVendida, SUM(dt.subtotal) AS ingresos " +
                 "FROM detalle_ticket dt INNER JOIN productos p ON dt.id_producto = p.id " +
                 "WHERE p.nombre LIKE ? " +
                 "GROUP BY p.id, p.nombre ORDER BY cantidadVendida DESC";
    try (Connection con = conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, "%" + criterio + "%");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Object[] fila = {
                rs.getString("nombre"),
                rs.getInt("cantidadVendida"),
                rs.getDouble("ingresos")
            };
            lista.add(fila);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return lista;
}
}
