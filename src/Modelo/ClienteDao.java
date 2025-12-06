
package Modelo;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ClienteDao {
   
    
    private Conexion conexion;

    public ClienteDao() {
        conexion = new Conexion();
    }


    public int registrarCliente(Cliente cliente) {
        int idCliente = -1;
        try (Connection con = conexion.getConnection()) {
            // Verificar si ya existe por nombre
            PreparedStatement buscar = con.prepareStatement(
                "SELECT id_cliente FROM Clientes WHERE nombre = ?");
            buscar.setString(1, cliente.getNombre());
            ResultSet rs = buscar.executeQuery();

            if (rs.next()) {
                idCliente = rs.getInt("id_cliente"); // ya existe
            } else {
                // Insertar nuevo cliente
                PreparedStatement insertar = con.prepareStatement(
                    "INSERT INTO Clientes (nombre) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS
                );
                insertar.setString(1, cliente.getNombre());
                insertar.executeUpdate();

                ResultSet claves = insertar.getGeneratedKeys();
                if (claves.next()) {
                    idCliente = claves.getInt(1); // nuevo ID generado
                }
            }
            rs.close();
            buscar.close();
        } catch (SQLException e) {
            System.err.println("Error al registrar cliente: " + e.getMessage());
        }
        return idCliente;
    }


    // Buscar cliente por ID
    public Cliente obtenerClientePorId(int idCliente) {
        Cliente cliente = null;
        try (Connection con = conexion.getConnection()) {
            PreparedStatement stmt = con.prepareStatement(
                "SELECT * FROM Clientes WHERE id_cliente = ?");
            stmt.setInt(1, idCliente);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                cliente = new Cliente(
                    rs.getInt("id_cliente"),
                    rs.getString("nombre")
                    
                );
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error al obtener cliente: " + e.getMessage());
        }
        return cliente;
    }

    
    public List<Cliente> listarClientes() {
    List<Cliente> lista = new ArrayList<>();
    String sql = "SELECT c.id_cliente, c.nombre, t.id_ticket, t.ruta_pdf " +
                 "FROM clientes c " +
                 "LEFT JOIN tickets t ON c.id_cliente = t.id_cliente " +
                 "ORDER BY t.fecha DESC";

    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Cliente c = new Cliente();
            c.setIdCliente(rs.getInt("id_cliente"));
            c.setNombre(rs.getString("nombre"));
            c.setIdTicket(rs.getInt("id_ticket"));   // puede ser null → 0
            c.setRutaPDF(rs.getString("ruta_pdf"));  // puede ser null
            lista.add(c);
        }

    } catch (SQLException e) {
        System.err.println("Error al listar clientes: " + e.getMessage());
    }
    return lista;
}

//    public boolean actualizarRutaPDF(int idTicket, String rutaPDF) {
//    String sql = "UPDATE tickets SET ruta_pdf = ? WHERE id_ticket = ?";
//
//    try (Connection con = new Conexion().getConnection();
//         PreparedStatement stmt = con.prepareStatement(sql)) {
//
//        stmt.setString(1, rutaPDF);
//        stmt.setInt(2, idTicket);
//
//        return stmt.executeUpdate() > 0;
//
//    } catch (SQLException e) {
//        JOptionPane.showMessageDialog(null, "Error al actualizar ruta PDF: " + e.getMessage());
//        return false;
//    }
//}


public boolean actualizarRutaPDFCliente(int idCliente, String rutaPDF) {
    String sql = "UPDATE clientes SET ruta_pdf = ? WHERE id_cliente = ?";
    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, rutaPDF);
        ps.setInt(2, idCliente);
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error al actualizar ruta PDF en cliente: " + e.getMessage());
        return false;
    }
}


    public List<Cliente> buscarClientesConTicket(String criterio) {
    List<Cliente> lista = new ArrayList<>();
    String sql = "SELECT c.id_cliente, c.nombre, " +
                 "IFNULL(t.id_ticket, 0) AS id_ticket, " +
                 "IFNULL(t.ruta_pdf, '') AS ruta_pdf " +
                 "FROM clientes c " +
                 "LEFT JOIN tickets t ON c.id_cliente = t.id_cliente " +
                 "WHERE c.id_cliente LIKE ? OR c.nombre LIKE ?";

    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, "%" + criterio + "%");
        ps.setString(2, "%" + criterio + "%");

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Cliente c = new Cliente();
            c.setIdCliente(rs.getInt("id_cliente"));
            c.setNombre(rs.getString("nombre"));
            c.setIdTicket(rs.getInt("id_ticket"));
            c.setRutaPDF(rs.getString("ruta_pdf"));
            lista.add(c);
        }
    } catch (SQLException e) {
        System.out.println("Error al buscar clientes con ticket: " + e.getMessage());
    }
    return lista;
}

    public List<Cliente> listarClientesConTicket() {
    List<Cliente> lista = new ArrayList<>();
    String sql = "SELECT c.id_cliente, c.nombre, " +
                 "IFNULL(t.id_ticket, 0) AS id_ticket, " +
                 "IFNULL(t.ruta_pdf, '') AS ruta_pdf " +
                 "FROM clientes c " +
                 "LEFT JOIN tickets t ON c.id_cliente = t.id_cliente";

    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Cliente c = new Cliente();
            c.setIdCliente(rs.getInt("id_cliente"));
            c.setNombre(rs.getString("nombre"));
            c.setIdTicket(rs.getInt("id_ticket"));   // ahora sí existe en el resultado
            c.setRutaPDF(rs.getString("ruta_pdf"));
            lista.add(c);
        }
    } catch (SQLException e) {
        System.out.println("Error al listar clientes con ticket: " + e.getMessage());
    }
    return lista;
}

    

}
