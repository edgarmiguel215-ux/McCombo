
package Modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


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
    public boolean eliminarArticulo(int idArticulo) {
        String sql = "DELETE FROM articulo WHERE id_articulo = ?";
        try (Connection con = conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

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


}





