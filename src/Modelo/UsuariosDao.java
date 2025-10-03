
package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UsuariosDao {
    
    Conexion cn = new Conexion();
    Connection con;
    PreparedStatement ps;

    public boolean registrarUsuario(Usuarios u) {
        String sql = "INSERT INTO usuarios (nombre, correo, pass, rol) VALUES (?, ?, ?, ?)";
       
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getCorreo());
            ps.setString(3, u.getPass());
            ps.setString(4, u.getRol());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al registrar usuario: " + e.toString());
            return false;
        }
    }
    
}
