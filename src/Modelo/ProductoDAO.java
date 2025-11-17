
package Modelo;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;



public class ProductoDAO {

    private Conexion conexion = new Conexion();

    // Insertar producto
    public boolean insertar(String codigo, String nombre, double precio, int idCategoria) {
        String sql = "INSERT INTO productos(codigo, nombre, precio, id_categoria) VALUES(?, ?, ?, ?)";
        try (Connection con = conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);
            ps.setString(2, nombre);
            ps.setDouble(3, precio);
            ps.setInt(4, idCategoria);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al insertar producto: " + e.getMessage());
            return false;
        }
    }

    // Listar productos
    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.codigo, p.nombre, p.precio, c.id AS id_categoria, c.nombre AS nombre_categoria " +
                     "FROM productos p " +
                     "JOIN categorias c ON p.id_categoria = c.id";

        try (Connection con = conexion.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
            Producto p = new Producto();
            p.setId(rs.getInt("id"));
            p.setCodigo(rs.getString("codigo"));
            p.setNombre(rs.getString("nombre"));
            p.setPrecio(rs.getDouble("precio"));
            p.setCategoria(rs.getString("nombre_categoria")); // directamente el nombre de la categoría
            lista.add(p);
        }


        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al listar productos: " + e.getMessage());
        }

        return lista;
    }

    // Actualizar producto
    public boolean actualizar(int id, String codigo, String nombre, double precio, int idCategoria) {
        String sql = "UPDATE productos SET codigo=?, nombre=?, precio=?, id_categoria=? WHERE id=?";
        try (Connection con = conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);
            ps.setString(2, nombre);
            ps.setDouble(3, precio);
            ps.setInt(4, idCategoria);
            ps.setInt(5, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    // Eliminar producto
    public boolean eliminar(int id) {
        String sql = "DELETE FROM productos WHERE id=?";
        try (Connection con = conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }

    // Verificar si existen productos con una categoría
    public boolean existenProductosConCategoria(int idCategoria) {
        String sql = "SELECT COUNT(*) FROM productos WHERE id_categoria = ?";
        try (Connection con = conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCategoria);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;

        } catch (SQLException e) {
            System.err.println("Error al verificar productos por categoría: " + e.getMessage());
        }
        return false;
    }

    // Buscar producto por código
    public Producto buscarPorCodigo(String codigo) {
        Producto producto = null;
        String sql = "SELECT p.id, p.codigo, p.nombre, p.precio, p.id_categoria, c.nombre AS nombre_categoria " +
                     "FROM productos p " +
                     "JOIN categorias c ON p.id_categoria = c.id " +
                     "WHERE TRIM(p.codigo) = ?";

        try (Connection con = conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo.trim());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
            String nombreCategoria = rs.getString("nombre_categoria");

            producto = new Producto(
                rs.getInt("id"),
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getDouble("precio"),
                nombreCategoria
            );
}


        } catch (SQLException e) {
            System.err.println("Error al buscar producto por codigo: " + e.getMessage());
        }

        return producto;
    }

    // Buscar artículo por nombre
    public Articulo buscarPorNombre(String nombre) {
    Articulo articulo = null;
    String sql = "SELECT * FROM articulo WHERE nombre = ?";

    try (Connection con = conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, nombre);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            articulo = new Articulo(
                rs.getString("nombre"),   // nombre
                rs.getString("unidad"),   // unidad
                rs.getInt("precio"),      // precio
                rs.getInt("id")           // id
            );
        }

    } catch (SQLException e) {
        System.err.println("Error al buscar artículo por nombre: " + e.getMessage());
    }

    return articulo;
}


    // Insertar detalle de producto
    public boolean insertarDetalle(int idProducto, int idArticulo, int cantidad) {
        String sql = "INSERT INTO detalle_producto (id_producto, id_articulo, cantidad) VALUES (?, ?, ?)";
        try (Connection con = conexion.getConnection();
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

    // Eliminar detalle por ID
    public boolean eliminarDetalle(int idDetalle) {
        String sql = "DELETE FROM detalle_producto WHERE id = ?";
        try (Connection con = conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idDetalle);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar detalle: " + e.getMessage());
            return false;
        }
    }

    // Listar detalles por ID de producto
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

    // Listar detalles por código de producto
    public List<DetalleProducto> listarPorCodigoProducto(String codigo) {
        List<DetalleProducto> lista = new ArrayList<>();
        String sql = "SELECT dp.id AS id_detalle, p.codigo AS codigo_producto, p.nombre AS nombre_producto, " +
                     "a.nombre AS nombre_articulo, dp.cantidad " +
                     "FROM detalle_producto dp " +
                     "JOIN productos p ON dp.id_producto = p.id " +
                     "JOIN articulo a ON dp.id_articulo = a.id_articulo " +
                     "WHERE p.codigo = ?";

        try (Connection con = conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);
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
            System.err.println("Error al listar por código: " + e.getMessage());
        }

        return lista;
    }
    public boolean existeCodigo(String codigo) {
    String sql = "SELECT COUNT(*) FROM productos WHERE codigo = ?";
    try (Connection con = conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, codigo);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
    } catch (SQLException e) {
        System.err.println("Error al verificar código existente: " + e.getMessage());
    }
    return false;
}

public boolean existeNombre(String nombre) {
    String sql = "SELECT COUNT(*) FROM productos WHERE nombre = ?";
    try (Connection con = conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, nombre);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
    } catch (SQLException e) {
        System.err.println("Error al verificar nombre existente: " + e.getMessage());
    }
    return false;
}



}









