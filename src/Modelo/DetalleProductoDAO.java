
package Modelo;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;



public class DetalleProductoDAO {
  
    private Conexion conexion = new Conexion();

    // Insertar detalle de producto
   // Inserta un artículo en un producto
    public boolean insertar(int idProducto, int idArticulo, int cantidad) {
    String sql = "INSERT INTO detalle_producto (id_producto, id_articulo, cantidad) VALUES (?, ?, ?)";
    try (Connection con = new Conexion().getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idProducto);
        ps.setInt(2, idArticulo);
        ps.setInt(3, cantidad);

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        System.err.println("Error al insertar detalle de producto: " + e.getMessage());
        return false;
    }
}

    // Listar detalles de un producto
    public List<DetalleProducto> listarPorProducto(int idProducto) {
        List<DetalleProducto> lista = new ArrayList<>();
        String sql = "SELECT dp.id AS id_detalle, p.codigo AS codigo_producto, p.nombre AS nombre_producto, " +
                     "a.nombre AS nombre_articulo, dp.cantidad " +
                     "FROM detalle_producto dp " +
                     "JOIN productos p ON dp.id_producto = p.id " +
                     "JOIN articulo a ON dp.id_articulo = a.id_articulo " +
                     "WHERE dp.id_producto = ?";

        try (Connection con = conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                DetalleProducto dp = new DetalleProducto();
                dp.setIdDetalle(rs.getInt("id_detalle"));
                dp.setCodigoProducto(rs.getString("codigo_producto"));
                dp.setNombreProducto(rs.getString("nombre_producto"));
                dp.setNombreArticulo(rs.getString("nombre_articulo"));
                dp.setCantidad(rs.getInt("cantidad"));
                lista.add(dp);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar detalle producto: " + e.getMessage());
        }

        return lista;
    }

    // Eliminar detalle de producto
    public boolean eliminar(int idDetalle) {
        String sql = "DELETE FROM detalle_producto WHERE id=?";
        try (Connection con = conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idDetalle);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar detalle: " + e.getMessage());
            return false;
        }
    }
    
    public boolean actualizar(int idDetalle, int nuevaCantidad) {
    String sql = "UPDATE detalle_producto SET cantidad = ? WHERE id = ?";
    try (Connection con = conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, nuevaCantidad);
        ps.setInt(2, idDetalle);
        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        System.err.println("Error al actualizar detalle producto: " + e.getMessage());
        return false;
    }
}

     // Obtener detalles por id de producto
    public List<DetalleProducto> obtenerPorProducto(int idProducto) {
    List<DetalleProducto> lista = new ArrayList<>();
    String sql = "SELECT * FROM detalle_producto WHERE id_producto = ?";

    try (Connection con = conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idProducto);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                DetalleProducto dp = new DetalleProducto();
                dp.setIdDetalle(rs.getInt("id"));
                dp.setIdProducto(rs.getInt("id_producto"));
                dp.setIdArticulo(rs.getInt("id_articulo"));
                dp.setCantidad(rs.getInt("cantidad"));
                lista.add(dp);
            }
        }

    } catch (SQLException e) {
        System.err.println("Error en obtenerPorProducto: " + e.getMessage());
    }
    return lista;
}

    
    public List<DetalleProducto> obtenerArticulosPorProducto(int idProducto) {
    List<DetalleProducto> lista = new ArrayList<>();

    String sql = "SELECT * FROM detalle_producto WHERE id_producto = ?";

    try (Connection con = conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idProducto);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            DetalleProducto dp = new DetalleProducto();
            dp.setIdDetalle(rs.getInt("id")); 
            dp.setIdProducto(rs.getInt("id_producto"));
            dp.setIdArticulo(rs.getInt("id_articulo"));
            dp.setCantidad(rs.getInt("cantidad"));
            lista.add(dp);
        }

    } catch (SQLException e) {
        System.out.println("Error en obtenerArticulosPorProducto: " + e.getMessage());
    }

    return lista;
}

    public boolean verificarStock(int idArticulo, int cantidad) {
    String sql = "SELECT stock_actual FROM inventario WHERE id_articulo = ?";

    try (Connection con = conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idArticulo);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int stockActual = rs.getInt("stock_actual");
            return stockActual >= cantidad; // true si hay suficiente
        } else {
            System.out.println("Inventario: artículo no encontrado id=" + idArticulo);
            return false;
        }

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

}

