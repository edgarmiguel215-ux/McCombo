
package Modelo;

import java.sql.*;
import java.util.*;


public class InventarioDAO {
    
    Conexion cn = new Conexion();


    // -------------------------- LISTAR INVENTARIO --------------------------
    public List<Inventario> listar() {
    List<Inventario> lista = new ArrayList<>();
    String sql = "SELECT id_articulo, nombre, unidad, stock_actual, costo_unitario, valor_inventario, estado FROM inventario ORDER BY id_articulo DESC";

    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Inventario i = new Inventario();
            i.setIdCompra(rs.getInt("id_articulo")); // o usa setIdArticulo si tienes ese campo
            i.setNombre(rs.getString("nombre"));
            i.setUnidad(rs.getString("unidad"));
            i.setStockActual(rs.getInt("stock_actual"));
            i.setCostoUnitario(rs.getDouble("costo_unitario"));
            i.setValorInventario(rs.getDouble("valor_inventario"));
            i.setEstado(rs.getString("estado"));
            lista.add(i);
        }

    } catch (SQLException e) {
        System.err.println("Error listar inventario: " + e.getMessage());
    }

    return lista;
}

    
    // -------------------------- EDITAR COMPRA --------------------------
   public void actualizarInventarioDespuesDeEditar(Compra original, Compra editada) {
    try (Connection con = cn.getConnection()) {
        String consulta = "SELECT stock_actual FROM inventario WHERE id_articulo = ?";
        int stockActual = 0;

        try (PreparedStatement psCheck = con.prepareStatement(consulta)) {
            psCheck.setInt(1, editada.getIdArticulo());
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next()) {
                    stockActual = rs.getInt("stock_actual");
                }
            }
        }

        int nuevoStock = stockActual - original.getCantidad() + editada.getCantidad();
        double nuevoValor = nuevoStock * editada.getPrecioUnitario();

        String sqlUpdate = "UPDATE inventario SET stock_actual = ?, costo_unitario = ?, valor_inventario = ?, unidad = ?, estado = ? WHERE id_articulo = ?";
        
        try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
            ps.setInt(1, nuevoStock);
            ps.setDouble(2, editada.getPrecioUnitario());
            ps.setDouble(3, nuevoValor);
            ps.setString(4, editada.getUnidad());
            ps.setString(5, editada.getEstado()); // ✅ ahora sí actualiza el estado
            ps.setInt(6, editada.getIdArticulo());
            ps.executeUpdate();
        }

    } catch (SQLException e) {
        System.err.println("Error actualizarInventarioDespuesDeEditar: " + e.getMessage());
        e.printStackTrace();
    }
}

    
    // -------------------------- ELIMINAR COMPRA --------------------------
    public void actualizarInventarioDespuesDeEliminar(Compra c) {
        try (Connection con = cn.getConnection()) {
            // Obtener stock actual
            String consulta = "SELECT stock_actual, costo_unitario FROM inventario WHERE id_articulo = ?";
            int stockActual = 0;
            double costoUnitario = 0;

            try (PreparedStatement psCheck = con.prepareStatement(consulta)) {
                psCheck.setInt(1, c.getIdArticulo());
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        stockActual = rs.getInt("stock_actual");
                        costoUnitario = rs.getDouble("costo_unitario");
                    }
                }
            }

            int nuevoStock = stockActual - c.getCantidad();
            double nuevoValor = nuevoStock * costoUnitario;

            String sqlUpdate = "UPDATE inventario SET stock_actual = ?, valor_inventario = ? WHERE id_articulo = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
                ps.setInt(1, nuevoStock);
                ps.setDouble(2, nuevoValor);
                ps.setInt(3, c.getIdArticulo());
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            System.err.println("Error actualizarInventarioDespuesDeEliminar: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // -------------------------- REGISTRAR COMPRA --------------------------
   public void actualizarInventarioDespuesDeCompra(Compra compra) {
    try (Connection con = cn.getConnection()) {
        String consulta = "SELECT stock_actual, costo_unitario FROM inventario WHERE id_articulo = ?";
        int stockActual = 0;
        double costoActual = 0;
        boolean existe = false;

        try (PreparedStatement psCheck = con.prepareStatement(consulta)) {
            psCheck.setInt(1, compra.getIdArticulo());
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next()) {
                    stockActual = rs.getInt("stock_actual");
                    costoActual = rs.getDouble("costo_unitario");
                    existe = true;
                }
            }
        }

        int nuevoStock = stockActual + compra.getCantidad();
        double nuevoCosto = compra.getPrecioUnitario();
        double nuevoValor = nuevoStock * nuevoCosto;

        if (existe) {
            String sqlUpdate = "UPDATE inventario SET stock_actual = ?, costo_unitario = ?, valor_inventario = ?, unidad = ?, nombre = ?, estado = ? WHERE id_articulo = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
                ps.setInt(1, nuevoStock);
                ps.setDouble(2, nuevoCosto);
                ps.setDouble(3, nuevoValor);
                ps.setString(4, compra.getUnidad());
                ps.setString(5, compra.getArticulo());
                ps.setString(6, compra.getEstado());
                ps.setInt(7, compra.getIdArticulo());
                ps.executeUpdate();
            }
        } else {
            String sqlInsert = "INSERT INTO inventario (id_articulo, nombre, stock_actual, unidad, costo_unitario, valor_inventario, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sqlInsert)) {
                ps.setInt(1, compra.getIdArticulo());
                ps.setString(2, compra.getArticulo());
                ps.setInt(3, compra.getCantidad());
                ps.setString(4, compra.getUnidad());
                ps.setDouble(5, nuevoCosto);
                ps.setDouble(6, nuevoValor);
                ps.setString(7, compra.getEstado());
                ps.executeUpdate();
            }
        }

    } catch (SQLException e) {
        System.err.println("Error actualizarInventarioDespuesDeCompra: " + e.getMessage());
        e.printStackTrace();
    }
}

   
    public void insertarInventarioSiNoExiste(Compra c) {
    String sql = "INSERT INTO inventario (id_articulo, stock_actual, costo_unitario, valor_inventario, estado) " +
                 "SELECT ?, ?, ?, ?, ? FROM DUAL WHERE NOT EXISTS " +
                 "(SELECT 1 FROM inventario WHERE id_articulo = ?)";

    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, c.getIdArticulo());
        ps.setInt(2, c.getCantidad());
        ps.setDouble(3, c.getPrecioUnitario());
        ps.setDouble(4, c.getCantidad() * c.getPrecioUnitario());
        ps.setString(5, c.getEstado());
        ps.setInt(6, c.getIdArticulo());

        ps.executeUpdate();
    } catch (SQLException e) {
        System.err.println("Error insertarInventarioSiNoExiste: " + e.getMessage());
    }
}

    
    // -------------------------- CALCULAR TOTAL INVENTARIO --------------------------
    public double calcularTotalInventario() {
    double total = 0;
    String sql = "SELECT SUM(valor_inventario) AS total " +
                 "FROM inventario WHERE estado <> 'Cancelado'";

    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        if (rs.next()) {
            total = rs.getDouble("total");
        }

    } catch (SQLException e) {
        System.err.println("Error calcularTotalInventario: " + e.getMessage());
    }

    return total;
}

}

