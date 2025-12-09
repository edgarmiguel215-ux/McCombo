
package Modelo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

public class LoginDAO {
    Connection con;
    PreparedStatement ps;
    ResultSet rs;
    Conexion cn = new Conexion();
    
    // Método para login que devuelve un objeto de tipo login
    public login log(String correo, String passPlano){
        login l = null;
        String sql = "SELECT * FROM usuarios WHERE correo = ? AND estado = 'Activo'";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, correo);
            rs = ps.executeQuery();
            if (rs.next()){
                String hash = rs.getString("pass");
                boolean ok = BCrypt.checkpw(passPlano, hash); // Validar contraseña con BCrypt

                if (ok) {
                    l = new login();
                    l.setId(rs.getInt("id"));
                    l.setNombre(rs.getString("nombre"));
                    l.setCorreo(rs.getString("correo"));
                    l.setPass(hash); // guardas el hash, no el plano
                    l.setRol(rs.getString("rol").trim());
                    l.setEstado(rs.getString("estado"));
                    l.setOtpSecret(rs.getString("otp_secret"));
                    l.setOtpEnabled(rs.getInt("otp_enabled") == 1);
                }
            }
        } catch (Exception e){
            System.out.println("Error en login: " + e.toString());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar recursos: " + e.toString());
            }
        }
        return l;
    }

    // Método para login
    // Método para validar inicio de sesión
    public Usuarios login(String correo, String passPlano) {
        String sql = "SELECT * FROM usuarios WHERE correo = ? AND estado = 'Activo'";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, correo);
            rs = ps.executeQuery();

            if (rs.next()) {
                String hash = rs.getString("pass");
                boolean ok = BCrypt.checkpw(passPlano, hash); // Validar contraseña

                if (ok) {
                    Usuarios u = new Usuarios();
                    u.setId(rs.getInt("id"));
                    u.setNombre(rs.getString("nombre"));
                    u.setCorreo(rs.getString("correo"));
                    u.setRol(rs.getString("rol"));
                    u.setEstado(rs.getString("estado"));
                    u.setOtpSecret(rs.getString("otp_secret"));
                    u.setOtpEnabled(rs.getInt("otp_enabled") == 1);
                    return u;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en login: " + e.toString());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar recursos: " + e.toString());
            }
        }
        return null;
    }

    public login obtenerUsuarioPorCorreo(String correo) {
    login usuario = null;

    String sql = "SELECT * FROM usuarios WHERE correo = ?";
    
    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, correo);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            usuario = new login();
            usuario.setId(rs.getInt("id"));
            usuario.setNombre(rs.getString("nombre"));
            usuario.setCorreo(rs.getString("correo"));
            usuario.setPass(rs.getString("pass"));
            usuario.setRol(rs.getString("rol"));
            usuario.setEstado(rs.getString("estado"));
            usuario.setOtpSecret(rs.getString("otp_secret"));
            usuario.setOtpEnabled(rs.getBoolean("otp_enabled"));
        }

    } catch (SQLException e) {
        System.err.println("Error en obtenerUsuarioPorCorreo: " + e.getMessage());
    }

    return usuario;
}

}
