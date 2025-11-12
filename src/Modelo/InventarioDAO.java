
package Modelo;

import java.sql.*;
import java.util.*;


public class InventarioDAO {
    
    
    Connection con;
    PreparedStatement ps;
    ResultSet rs;
    Conexion cn = new Conexion();


    // Actualiza o inserta un registro de inventario desde una compra
    public void actualizarInventarioDesdeCompra(CompraDetalle detalle) {
        String sqlBuscar = "SELECT stock_actual FROM inventario WHERE idProducto = ?";
        String sqlInsertar = "INSERT INTO inventario (idProducto, nombre, unidad, stock_actual, costo_unitario, valor_inventario, idCompra, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlActualizar = "UPDATE inventario SET stock_actual = ?, unidad = ?, costo_unitario = ?, valor_inventario = ?, idCompra = ?, estado = ? WHERE idProducto = ?";

        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sqlBuscar);
            ps.setInt(1, detalle.getIdProducto());
            rs = ps.executeQuery();

            if (rs.next()) {
                int stockActual = rs.getInt("stock_actual");
                int nuevoStock = stockActual + detalle.getCantidad();
                double nuevoValor = nuevoStock * detalle.getPrecio();

                ps = con.prepareStatement(sqlActualizar);
                ps.setInt(1, nuevoStock);
                ps.setString(2, detalle.getUnidad());
                ps.setDouble(3, detalle.getPrecio());
                ps.setDouble(4, nuevoValor);
                ps.setInt(5, detalle.getIdCompra());
                ps.setString(6, detalle.getEstado());
                ps.setInt(7, detalle.getIdProducto());
                ps.executeUpdate();
            } else {
                ps = con.prepareStatement(sqlInsertar);
                ps.setInt(1, detalle.getIdProducto());
                ps.setString(2, detalle.getArticulo());
                ps.setString(3, detalle.getUnidad());
                ps.setInt(4, 0); // stock mínimo
                ps.setInt(5, detalle.getCantidad());
                ps.setDouble(6, detalle.getPrecio());
                ps.setDouble(7, detalle.getCantidad() * detalle.getPrecio());
                ps.setInt(8, detalle.getIdCompra());
                ps.setString(9, detalle.getEstado());
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            System.err.println("Error al actualizar inventario: " + e.getMessage());
        } finally {
            try { if (ps != null) ps.close(); if (con != null) con.close(); } catch (SQLException ex) {}
        }
    }

    // Devuelve todo el inventario para mostrarlo en la tabla
    public List<InventarioItem> obtenerTodos() {
        List<InventarioItem> lista = new ArrayList<>();
        String sql = "SELECT idCompra, nombre, unidad, stock_actual, costo_unitario, valor_inventario, estado FROM inventario";

        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                InventarioItem item = new InventarioItem();
                item.setIdCompra(rs.getInt("idCompra"));
                item.setNombre(rs.getString("nombre"));
                item.setUnidad(rs.getString("unidad"));
                
                item.setStockActual(rs.getInt("stock_actual"));
                item.setCostoUnitario(rs.getDouble("costo_unitario"));
                item.setValorInventario(rs.getDouble("valor_inventario"));
                item.setEstado(rs.getString("estado"));
                lista.add(item);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener inventario: " + e.getMessage());
        }

        return lista;
    }

    // 🆕 === MÉTODOS NUEVOS PARA SINCRONIZACIÓN COMPLETA ===

    // 1️⃣ Buscar un artículo por nombre
     // En obtenerPorNombre, asegúrate de cargar idCompra
    public InventarioItem obtenerPorNombre(String nombre) {
        String sql = "SELECT * FROM inventario WHERE nombre = ?";
        InventarioItem item = null;
        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    item = new InventarioItem();
                    item.setIdCompra(rs.getInt("idCompra")); // ✅ Cargar idCompra
                    item.setNombre(rs.getString("nombre"));
                    item.setUnidad(rs.getString("unidad"));
                    
                    item.setStockActual(rs.getInt("stock_actual"));
                    item.setCostoUnitario(rs.getDouble("costo_unitario"));
                    item.setValorInventario(rs.getDouble("valor_inventario"));
                    item.setEstado(rs.getString("estado"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en obtenerPorNombre: " + e.getMessage());
        }
        return item;
    }

    // Insertar nuevo artículo en inventario
    public void insertar(InventarioItem item) {
        String sql = "INSERT INTO inventario (idCompra, nombre, unidad, stock_min, stock_actual, costo_unitario, valor_inventario, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, item.getIdCompra());
            ps.setString(2, item.getNombre());
            ps.setString(3, item.getUnidad());
            ps.setInt(4, 0); // stock mínimo
            ps.setInt(5, item.getStockActual());
            ps.setDouble(6, item.getCostoUnitario());
            ps.setDouble(7, item.getValorInventario());
            ps.setString(8, item.getEstado());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al insertar inventario: " + e.getMessage());
        }
    }

    // Actualizar artículo existente
     public void actualizar(InventarioItem item) {
        String sql = "UPDATE inventario SET stock_actual=?, costo_unitario=?, valor_inventario=?, estado=?, idCompra=? WHERE nombre=?";
        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, item.getStockActual());
            ps.setDouble(2, item.getCostoUnitario());
            ps.setDouble(3, item.getValorInventario());
            ps.setString(4, item.getEstado());
            ps.setInt(5, item.getIdCompra()); // ✅ Agregar idCompra
            ps.setString(6, item.getNombre());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al actualizar inventario: " + e.getMessage());
        }
    }
     
}


