
package Modelo;

import java.sql.*;
import java.util.*;




public class CompraDAO {


    Conexion cn = new Conexion();

    public int registrarCompra(Compra c) {
    int idGenerado = 0;
    String sql = "INSERT INTO compras(id_articulo, idProveedor, cantidad, unidad, precio_unitario, total, comprobante, metodo_pago, datos_pago, fecha, estado) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        ps.setInt(1, c.getIdArticulo());   // 🔥 ID del artículo
        ps.setInt(2, c.getIdProveedor());  // 🔥 ID del proveedor
        ps.setInt(3, c.getCantidad());
        ps.setString(4, c.getUnidad());
        ps.setDouble(5, c.getPrecioUnitario());
        ps.setDouble(6, c.getTotal());
        ps.setString(7, c.getComprobante());
        ps.setString(8, c.getMetodoPago());
        ps.setString(9, c.getDatosPago());
        ps.setDate(10, new java.sql.Date(c.getFecha().getTime()));
        ps.setString(11, c.getEstado());

        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) idGenerado = rs.getInt(1);

    } catch (SQLException e) {
        System.err.println("Error registrarCompra: " + e.getMessage());
    }
    return idGenerado;
}

    public void actualizarInventarioDespuesDeCompra(Compra c) {
    InventarioDAO daoInventario = new InventarioDAO();

    // 🔹 Primero, insertar si no existe
    daoInventario.insertarInventarioSiNoExiste(c);

    // 🔹 Luego, actualizar el inventario
    String sql = "UPDATE inventario SET stock_actual = stock_actual + ?, costo_unitario = ?, valor_inventario = (stock_actual + ?) * ? , estado = ? WHERE id_articulo = ?";
    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, c.getCantidad());
        ps.setDouble(2, c.getPrecioUnitario());
        ps.setInt(3, c.getCantidad());
        ps.setDouble(4, c.getPrecioUnitario());
        ps.setString(5, c.getEstado());
        ps.setInt(6, c.getIdArticulo());

        ps.executeUpdate();
    } catch (SQLException e) {
        System.err.println("Error al actualizar inventario: " + e.getMessage());
    }
}


    public List<Compra> listarCompras() {
    List<Compra> lista = new ArrayList<>();
    String sql = "SELECT c.idCompras, a.nombre AS articulo, p.nombre AS proveedor, " +
             "c.cantidad, c.unidad, c.precio_unitario, c.total, c.comprobante, " +
             "c.metodo_pago, c.datos_pago, c.fecha, c.estado " +
             "FROM compras c " +
             "JOIN articulo a ON c.id_articulo = a.id_articulo " +   // 🔥 corregido
             "JOIN proveedor p ON c.idProveedor = p.id " +           // 🔥 corregido
             "ORDER BY c.idCompras DESC";

    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Compra c = new Compra();
            c.setId(rs.getInt("idCompras"));
            c.setArticulo(rs.getString("articulo"));   // nombre del artículo
            c.setProveedor(rs.getString("proveedor")); // nombre del proveedor
            c.setCantidad(rs.getInt("cantidad"));
            c.setUnidad(rs.getString("unidad"));
            c.setPrecioUnitario(rs.getDouble("precio_unitario"));
            c.setTotal(rs.getDouble("total"));
            c.setComprobante(rs.getString("comprobante"));
            c.setMetodoPago(rs.getString("metodo_pago"));
            c.setDatosPago(rs.getString("datos_pago"));
            c.setFecha(rs.getDate("fecha"));
            c.setEstado(rs.getString("estado"));
            lista.add(c);
        }

    } catch (SQLException e) {
        System.err.println("Error listarCompras: " + e.getMessage());
    }
    return lista;
}
    
    public boolean editarCompra(Compra c) {
    String sql = "UPDATE compras SET id_articulo=?, idProveedor=?, cantidad=?, unidad=?, precio_unitario=?, total=?, comprobante=?, metodo_pago=?, datos_pago=?, fecha=?, estado=? WHERE idCompras=?";
    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, c.getIdArticulo());
        ps.setInt(2, c.getIdProveedor());
        ps.setInt(3, c.getCantidad());
        ps.setString(4, c.getUnidad());
        ps.setDouble(5, c.getPrecioUnitario());
        ps.setDouble(6, c.getTotal());
        ps.setString(7, c.getComprobante());
        ps.setString(8, c.getMetodoPago());
        ps.setString(9, c.getDatosPago());
        ps.setDate(10, new java.sql.Date(c.getFecha().getTime()));
        ps.setString(11, c.getEstado());
        ps.setInt(12, c.getId());

        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error editarCompra: " + e.getMessage());
        return false;
    }
}

    
    public boolean eliminarCompra(int idCompra) {
    String sql = "DELETE FROM compras WHERE idCompras=?";
    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idCompra);
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error eliminarCompra: " + e.getMessage());
        return false;
    }
}

    public Compra obtenerCompraDesdeBD(int idCompra) {
    String sql = "SELECT * FROM compras WHERE idCompras = ?";
    Compra c = new Compra();

    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idCompra);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            c.setId(rs.getInt("idCompras"));
            c.setIdArticulo(rs.getInt("id_articulo"));
            c.setIdProveedor(rs.getInt("idProveedor"));
            c.setCantidad(rs.getInt("cantidad"));
            c.setUnidad(rs.getString("unidad"));
            c.setPrecioUnitario(rs.getDouble("precio_unitario"));
            c.setTotal(rs.getDouble("total"));
            c.setComprobante(rs.getString("comprobante"));
            c.setMetodoPago(rs.getString("metodo_pago"));
            c.setDatosPago(rs.getString("datos_pago"));
            c.setFecha(rs.getDate("fecha"));
            c.setEstado(rs.getString("estado"));
        }

    } catch (SQLException e) {
        System.err.println("Error obtenerCompraDesdeBD: " + e.getMessage());
    }

    return c;
}

    public Compra obtenerCompraOriginal(int idCompra) {
    String sql = "SELECT * FROM compras WHERE idCompras = ?";
    Compra c = null;
    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, idCompra);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            c = new Compra();
            c.setId(rs.getInt("idCompras"));
            c.setIdArticulo(rs.getInt("id_articulo"));
            c.setIdProveedor(rs.getInt("idProveedor"));
            c.setCantidad(rs.getInt("cantidad"));
            c.setUnidad(rs.getString("unidad"));
            c.setPrecioUnitario(rs.getDouble("precio_unitario"));
            c.setTotal(rs.getDouble("total"));
            c.setComprobante(rs.getString("comprobante"));
            c.setMetodoPago(rs.getString("metodo_pago"));
            c.setDatosPago(rs.getString("datos_pago"));
            c.setFecha(rs.getDate("fecha"));
            c.setEstado(rs.getString("estado"));
        }
    } catch (SQLException e) {
        System.err.println("Error obtenerCompraOriginal: " + e.getMessage());
    }
    return c;
}

    public void actualizarInventarioDespuesDeEditar(Compra original, Compra editada) {
    String sql = "UPDATE inventario SET stock_actual = ?, costo_unitario = ?, valor_inventario = ?, unidad = ? WHERE id_articulo = ?";

    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        // 1️⃣ Obtener el stock actual de la base de datos
        String consultaStock = "SELECT stock_actual FROM inventario WHERE id_articulo = ?";
        int stockActual = 0;
        try (PreparedStatement psStock = con.prepareStatement(consultaStock)) {
            psStock.setInt(1, editada.getIdArticulo());
            try (ResultSet rs = psStock.executeQuery()) {
                if (rs.next()) {
                    stockActual = rs.getInt("stock_actual");
                }
            }
        }

        // 2️⃣ Calcular nuevo stock y valor
        int nuevoStock = stockActual - original.getCantidad() + editada.getCantidad();
        double nuevoValor = nuevoStock * editada.getPrecioUnitario();

        // 3️⃣ Actualizar inventario con los valores exactos
        ps.setInt(1, nuevoStock);
        ps.setDouble(2, editada.getPrecioUnitario());
        ps.setDouble(3, nuevoValor);
        ps.setString(4, editada.getUnidad());
        ps.setInt(5, editada.getIdArticulo());

        ps.executeUpdate();

    } catch (SQLException e) {
        System.err.println("Error actualizarInventarioDespuesDeEditar: " + e.getMessage());
        e.printStackTrace();
    }
}


}
