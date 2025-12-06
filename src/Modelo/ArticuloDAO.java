
package Modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;


public class ArticuloDAO {


    private Conexion conexion;

    public ArticuloDAO() {
        conexion = new Conexion();
    }

   // Insertar artículo
    public boolean insertar(Articulo articulo) {
        String sql = "INSERT INTO articulo (nombre, unidad) VALUES (?, ?)";
        try (Connection con = conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, articulo.getNombre());
            ps.setString(2, articulo.getUnidad());
            
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar artículo: " + e.getMessage());
            return false;
        }
    }

    // Listar todos los artículos
    public List<Articulo> listar() {
    List<Articulo> lista = new ArrayList<>();
    String sql = "SELECT id_articulo, nombre, unidad FROM articulo";

    try (Connection con = conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Articulo art = new Articulo();
            art.setIdArticulo(rs.getInt("id_articulo"));   // 👈 correcto
            art.setNombre(rs.getString("nombre"));
            art.setUnidad(rs.getString("unidad"));
           
            lista.add(art);
        }

    } catch (SQLException e) {
        System.out.println("Error listar articulos: " + e.getMessage());
    }

    return lista;
}


    // Actualizar artículo
    public boolean actualizar(Articulo articulo) {
        String sql = "UPDATE articulo SET nombre = ?, unidad = ? WHERE id_articulo = ?";
        try (Connection con = conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, articulo.getNombre());
            ps.setString(2, articulo.getUnidad());
            
            ps.setInt(3, articulo.getIdArticulo());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar artículo: " + e.getMessage());
            return false;
        }
    }

    // Eliminar artículo
    // ELIMINAR ARTÍCULO CON VALIDACIÓN
public boolean eliminarArticulo(int idArticulo) {
    // 1️⃣ Verificar si el artículo tiene compras asociadas
    String sqlCheck = "SELECT COUNT(*) FROM compras WHERE id_articulo = ?";
    try (Connection con = conexion.getConnection();
         PreparedStatement psCheck = con.prepareStatement(sqlCheck)) {

        psCheck.setInt(1, idArticulo);
        ResultSet rs = psCheck.executeQuery();
        if (rs.next() && rs.getInt(1) > 0) {
            // Tiene compras → no permitir eliminar
            JOptionPane.showMessageDialog(null,
                "No se puede eliminar el artículo porque tiene compras registradas.",
                "Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    } catch (SQLException e) {
        System.err.println("Error al verificar compras del artículo: " + e.getMessage());
        return false;
    }

    // 2️⃣ Si no tiene compras, eliminar normalmente
    String sqlDelete = "DELETE FROM articulo WHERE id_articulo = ?";
    try (Connection con = conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sqlDelete)) {

        ps.setInt(1, idArticulo);
        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        System.err.println("Error al eliminar artículo: " + e.getMessage());
        return false;
    }
}


    // Buscar por nombre
    public Articulo buscarPorNombre(String nombre) {
        Articulo articulo = null;
        String sql = "SELECT id_articulo, nombre, unidad FROM articulo WHERE nombre = ?";

        try (Connection con = conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                articulo = new Articulo();
                articulo.setIdArticulo(rs.getInt("id_articulo"));
                articulo.setNombre(rs.getString("nombre"));
                articulo.setUnidad(rs.getString("unidad"));
                
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar artículo: " + e.getMessage());
        }

        return articulo;
    }

    // Buscar por ID
    public Articulo buscarPorId(int idArticulo) {
        Articulo articulo = null;
        String sql = "SELECT id_articulo, nombre, unidad FROM articulo WHERE id_articulo = ?";

        try (Connection con = conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idArticulo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                articulo = new Articulo();
                articulo.setIdArticulo(rs.getInt("id_articulo"));
                articulo.setNombre(rs.getString("nombre"));
                articulo.setUnidad(rs.getString("unidad"));
                
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar artículo por ID: " + e.getMessage());
        }

        return articulo;
    }

    // Verificar si existe el nombre
    public boolean existeNombre(String nombre) {
        String sql = "SELECT COUNT(*) FROM articulo WHERE nombre = ?";
        try (Connection con = conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;

        } catch (SQLException e) {
            return false;
        }
    }

    // Verificar si tiene detalles asociados
    public boolean tieneDetallesAsociados(int idArticulo) {
        String sql = "SELECT COUNT(*) FROM detalle_producto WHERE id_articulo = ?";
        try (Connection con = conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idArticulo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar detalles asociados: " + e.getMessage());
        }

        return false;
    }


    // Obtener nombre de artículo por id
    public String obtenerNombre(int idArticulo) {
    String nombre = "";
    String sql = "SELECT nombre FROM articulo WHERE id_articulo = ?";

    try (Connection con = conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idArticulo);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                nombre = rs.getString("nombre");
            }
        }

    } catch (SQLException e) {
        System.err.println("Error en obtenerNombre Articulo: " + e.getMessage());
    }
    return nombre;
}

    
}





