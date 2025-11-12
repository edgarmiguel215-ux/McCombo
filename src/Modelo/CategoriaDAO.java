
package Modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;



public class CategoriaDAO {
    Conexion conexion = new Conexion();

    //  Insertar nueva categoría
    public boolean insertar(String nombre) {
        String sql = "INSERT INTO categorias(nombre) VALUES(?)";
        try (Connection con = conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al insertar categoria: " + e.getMessage());
            return false;
        }
    }

    // Eliminar categoría por ID
    public boolean eliminar(int id) {
        String sql = "DELETE FROM categorias WHERE id=?";
        try (Connection con = conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar categoria: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(int id, String nuevoNombre) {
    String sql = "UPDATE categorias SET nombre=? WHERE id=?";
    try (Connection con = conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, nuevoNombre);
        ps.setInt(2, id);
        ps.executeUpdate();
        return true;
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al actualizar categoría: " + e.getMessage());
        return false;
    }
}

    public int obtenerIdPorNombre(String nombre) {
    String sql = "SELECT id FROM categorias WHERE nombre=?";
    try (Connection con = conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, nombre);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("id");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al obtener ID de categoría: " + e.getMessage());
    }
    return -1;
}
    

        
    
    //  Listar todas las categorías
    public List<Categoria> listar() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM categorias ORDER BY id ASC";
        try (Connection con = conexion.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                lista.add(new Categoria(id, nombre));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al listar categorias: " + e.getMessage());
        }
        return lista;
    }
    
    public boolean existeNombre(String nombre) {
    String sql = "SELECT COUNT(*) FROM categorias WHERE TRIM(nombre) = ?";
    try (Connection con = conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, nombre.trim());
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al verificar duplicado: " + e.getMessage());
    }
    return false;
}

}


