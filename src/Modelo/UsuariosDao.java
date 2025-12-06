
package Modelo;


import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuariosDao {
    
    Conexion cn = new Conexion();
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    public boolean registrarUsuario(Usuarios u) {
    String sql = "INSERT INTO usuarios (nombre, correo, pass, rol, estado) VALUES (?, ?, ?, ?, 'Activo')";
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

   
    
    // Listar usuarios
    public List<Usuarios> listarUsuarios() {
        List<Usuarios> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Usuarios u = new Usuarios();
                u.setId(rs.getInt("id"));
                u.setNombre(rs.getString("nombre"));
                u.setCorreo(rs.getString("correo"));
                u.setPass(rs.getString("pass"));
                u.setRol(rs.getString("rol"));
                u.setEstado(rs.getString("estado")); // ✅ nuevo
                lista.add(u);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e.toString());
        } finally {
            // Cerrar recursos
            cerrarRecursos(rs, ps, con);
        }
        return lista;
    }

    //  Editar usuario
    public boolean actualizarUsuario(Usuarios u) {
        String sql = "UPDATE usuarios SET nombre = ?, correo = ?, pass = ?, rol = ? WHERE id = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getCorreo());
            ps.setString(3, u.getPass());
            ps.setString(4, u.getRol());
            ps.setInt(5, u.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar usuario: " + e.toString());
            return false;
        } finally {
            // ✅ Cerrar recursos
            cerrarRecursos(null, ps, con);
        }
    }

    
    // ELIMINAR USUARIO (SOFT DELETE)
    public boolean eliminarUsuario(int id) {
    String sql = "UPDATE usuarios SET estado = 'Inactivo' WHERE id = ?";
    try {
        con = cn.getConnection();
        ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        System.out.println("Error al eliminar usuario: " + e.toString());
        return false;
    } finally {
        cerrarRecursos(null, ps, con);
    }
}

    
    // Metodo para cerrar recursos y evitar memory leaks
    private void cerrarRecursos(ResultSet rs, PreparedStatement ps, Connection con) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            System.out.println("Error al cerrar recursos: " + e.toString());
        }
    }

    public boolean existeUsuarioConNombreYPass(String nombre, String pass) {
    String sql = "SELECT COUNT(*) FROM usuarios WHERE nombre = ? AND pass = ?";
    try {
        con = cn.getConnection();
        ps = con.prepareStatement(sql);
        ps.setString(1, nombre);
        ps.setString(2, pass);
        rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
    } catch (SQLException e) {
        System.out.println("Error al verificar duplicado: " + e.toString());
    }
    return false;
}

    

    
    public boolean existeUsuarioConNombreYCorreoExcluyendoId(String nombre, String correo, int idExcluir) {
    String sql = "SELECT COUNT(*) FROM usuarios WHERE nombre = ? AND correo = ? AND id != ?";
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        con = cn.getConnection(); // se obtiene nueva conexión siempre
        ps = con.prepareStatement(sql);
        ps.setString(1, nombre);
        ps.setString(2, correo);
        ps.setInt(3, idExcluir);
        rs = ps.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        // Cerrar solo ResultSet y PreparedStatement
        try { if (rs != null) rs.close(); } catch (Exception ignored) {}
        try { if (ps != null) ps.close(); } catch (Exception ignored) {}
        // No cerrar la conexión aquí
    }
    return false;
}

    
    public List<Usuarios> buscarUsuarios(String criterio) {
    List<Usuarios> lista = new ArrayList<>();
    String sql = "SELECT * FROM usuarios WHERE id LIKE ? OR nombre LIKE ? OR correo LIKE ?";
    try {
        con = cn.getConnection();
        ps = con.prepareStatement(sql);
        ps.setString(1, "%" + criterio + "%");
        ps.setString(2, "%" + criterio + "%");
        ps.setString(3, "%" + criterio + "%");
        rs = ps.executeQuery();
        while (rs.next()) {
            Usuarios u = new Usuarios();
            u.setId(rs.getInt("id"));
            u.setNombre(rs.getString("nombre"));
            u.setCorreo(rs.getString("correo"));
            u.setPass(rs.getString("pass"));
            u.setRol(rs.getString("rol"));
            u.setEstado(rs.getString("estado"));
            lista.add(u);
        }
    } catch (SQLException e) {
        System.out.println("Error al buscar usuarios: " + e.toString());
    } finally {
        cerrarRecursos(rs, ps, con);
    }
    return lista;
}


    
}

