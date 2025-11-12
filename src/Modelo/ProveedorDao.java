
package Modelo;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;
import javax.swing.JOptionPane;


public class ProveedorDao {
    private Conexion cn = new Conexion();

    // INSERTAR PROVEEDOR
    public boolean insertar(Proveedor p) {
        String sql = "INSERT INTO proveedor (numero_documento, tipo, nombre, telefono, direccion, razon) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNumeroDocumento());
            ps.setString(2, p.getTipo());
            ps.setString(3, p.getNombre());
            ps.setString(4, p.getTelefono());
            ps.setString(5, p.getDireccion());
            ps.setString(6, p.getRazon());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al insertar proveedor: " + e.getMessage());
            return false;
        }
    }

    // LISTAR PROVEEDORES
    public List<Proveedor> listar() {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM proveedor";

        try (Connection con = cn.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Proveedor p = new Proveedor();
                p.setId(rs.getInt("id"));
                p.setNumeroDocumento(rs.getString("numero_documento"));
                p.setTipo(rs.getString("tipo"));
                p.setNombre(rs.getString("nombre"));
                p.setTelefono(rs.getString("telefono"));
                p.setDireccion(rs.getString("direccion"));
                p.setRazon(rs.getString("razon"));
                lista.add(p);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar proveedores: " + e.getMessage());
        }
        return lista;
    }

    // ACTUALIZAR PROVEEDOR
    public boolean actualizar(Proveedor p) {
        String sql = "UPDATE proveedor SET numero_documento=?, Tipo=?, nombre=?, telefono=?, direccion=?, razon=? WHERE id=?";
        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNumeroDocumento());
            ps.setString(2, p.getTipo());
            ps.setString(3, p.getNombre());
            ps.setString(4, p.getTelefono());
            ps.setString(5, p.getDireccion());
            ps.setString(6, p.getRazon());
            ps.setInt(7, p.getId());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al actualizar proveedor: " + e.getMessage());
            return false;
        }
    }

    // ELIMINAR PROVEEDOR
    public boolean eliminar(int id) {
        String sql = "DELETE FROM proveedor WHERE id = ?";
        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al eliminar proveedor: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "No se pudo eliminar el proveedor:\n" + e.getMessage());
            return false;
        }
    }

   public Proveedor buscarPorId(int id) throws SQLException {
    Proveedor proveedor = null;
    String sql = "SELECT * FROM proveedor WHERE id = ?";
    
    Conexion conexion = new Conexion();
    try (Connection conn = conexion.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, id);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                proveedor = new Proveedor();
                proveedor.setId(rs.getInt("id"));
                proveedor.setNombre(rs.getString("nombre"));
                proveedor.setTipo(rs.getString("tipo"));
                proveedor.setNumeroDocumento(rs.getString("numero_documento"));
                proveedor.setTelefono(rs.getString("telefono"));
                proveedor.setDireccion(rs.getString("direccion"));
                proveedor.setRazon(rs.getString("razon"));
            }
        }
    }
    return proveedor;
}

}
