
package Modelo;

import java.sql.*;
import java.util.*;


public class InventarioDAO {
    
    Conexion cn = new Conexion();


    // -------------------------- LISTAR INVENTARIO --------------------------
    public List<Inventario> listar() {
        List<Inventario> lista = new ArrayList<>();
        String sql = "SELECT id_articulo, nombre, unidad, cantidad_comprada, stock_actual, costo_unitario, valor_inventario, estado, imagen " +
                     "FROM inventario ORDER BY id_articulo DESC";

        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Inventario i = new Inventario();
                i.setIdArticulo(rs.getInt("id_articulo"));
                i.setNombre(rs.getString("nombre"));
                i.setUnidad(rs.getString("unidad"));
                i.setCantidadComprada(rs.getInt("cantidad_comprada"));
                i.setStockActual(rs.getInt("stock_actual"));
                i.setCostoUnitario(rs.getDouble("costo_unitario"));
                i.setValorInventario(rs.getDouble("valor_inventario"));
                i.setEstado(rs.getString("estado"));
                i.setImagen(rs.getString("imagen"));
                lista.add(i);
            }

        } catch (SQLException e) {
            System.err.println("Error listar inventario: " + e.getMessage());
        }

        return lista;
    }

    // -------------------------- EDITAR COMPRA --------------------------
    public void actualizarInventarioDespuesDeEditar(Compra original, Compra editada) {
        String sqlUpdate = "UPDATE inventario SET cantidad_comprada = ?, stock_actual = ?, costo_unitario = ?, valor_inventario = ?, unidad = ?, estado = ? WHERE id_articulo = ?";

        try (Connection con = cn.getConnection()) {
            String consulta = "SELECT stock_actual, cantidad_comprada FROM inventario WHERE id_articulo = ?";
            int stockActual = 0;
            int cantidadCompradaActual = 0;

            try (PreparedStatement psCheck = con.prepareStatement(consulta)) {
                psCheck.setInt(1, editada.getIdArticulo());
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        stockActual = rs.getInt("stock_actual");
                        cantidadCompradaActual = rs.getInt("cantidad_comprada");
                    }
                }
            }

            int nuevoStock = stockActual;

            // Ajuste según estados
            if ("Recibido".equalsIgnoreCase(original.getEstado())) {
                nuevoStock -= original.getCantidad();
            }
            if ("Recibido".equalsIgnoreCase(editada.getEstado())) {
                nuevoStock += editada.getCantidad();
            }
            nuevoStock = Math.max(nuevoStock, 0);

            int nuevaCantidadComprada = cantidadCompradaActual - original.getCantidad() + editada.getCantidad();
            double nuevoValor = nuevoStock * editada.getPrecioUnitario();

            try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
                ps.setInt(1, nuevaCantidadComprada);
                ps.setInt(2, nuevoStock);
                ps.setDouble(3, editada.getPrecioUnitario());
                ps.setDouble(4, nuevoValor);
                ps.setString(5, editada.getUnidad());
                ps.setString(6, editada.getEstado());
                ps.setInt(7, editada.getIdArticulo());
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            System.err.println("Error actualizarInventarioDespuesDeEditar: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // -------------------------- ELIMINAR COMPRA (soft delete) --------------------------
    public boolean actualizarInventarioDespuesDeEliminar(Compra c) {
        try (Connection con = cn.getConnection()) {
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

            // Solo restar si la compra estaba recibida
            int nuevoStock = "Recibido".equalsIgnoreCase(c.getEstado()) ? stockActual - c.getCantidad() : stockActual;
            nuevoStock = Math.max(nuevoStock, 0);
            double nuevoValor = nuevoStock * costoUnitario;

            String sqlUpdate = "UPDATE inventario SET stock_actual = ?, valor_inventario = ? WHERE id_articulo = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
                ps.setInt(1, nuevoStock);
                ps.setDouble(2, nuevoValor);
                ps.setInt(3, c.getIdArticulo());
                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error actualizarInventarioDespuesDeEliminar: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // -------------------------- REGISTRAR COMPRA --------------------------
     public void actualizarInventarioDespuesDeCompra(Compra compra) {
        try (Connection con = cn.getConnection()) {
            String consulta = "SELECT stock_actual, cantidad_comprada, costo_unitario FROM inventario WHERE id_articulo = ?";
            int stockActual = 0;
            int cantidadCompradaActual = 0;
            double costoActual = 0;
            boolean existe = false;

            try (PreparedStatement psCheck = con.prepareStatement(consulta)) {
                psCheck.setInt(1, compra.getIdArticulo());
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        stockActual = rs.getInt("stock_actual");
                        cantidadCompradaActual = rs.getInt("cantidad_comprada");
                        costoActual = rs.getDouble("costo_unitario");
                        existe = true;
                    }
                }
            }

            int nuevoStock = stockActual;
            int nuevaCantidadComprada = cantidadCompradaActual + compra.getCantidad();
            double nuevoCosto = compra.getPrecioUnitario();

            // Solo sumamos al stock si la compra está marcada como Recibido
            if ("Recibido".equalsIgnoreCase(compra.getEstado())) {
                nuevoStock += compra.getCantidad();
            }

            double nuevoValor = nuevoStock * nuevoCosto;

            if (existe) {
                String sqlUpdate = "UPDATE inventario SET cantidad_comprada = ?, stock_actual = ?, costo_unitario = ?, valor_inventario = ?, unidad = ?, estado = ? WHERE id_articulo = ?";
                try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
                    ps.setInt(1, nuevaCantidadComprada);
                    ps.setInt(2, nuevoStock);
                    ps.setDouble(3, nuevoCosto);
                    ps.setDouble(4, nuevoValor);
                    ps.setString(5, compra.getUnidad());
                    ps.setString(6, compra.getEstado());
                    ps.setInt(7, compra.getIdArticulo());
                    ps.executeUpdate();
                }
            } else {
                String sqlInsert = "INSERT INTO inventario (id_articulo, nombre, cantidad_comprada, stock_actual, unidad, costo_unitario, valor_inventario, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = con.prepareStatement(sqlInsert)) {
                    ps.setInt(1, compra.getIdArticulo());
                    ps.setString(2, compra.getArticulo());
                    ps.setInt(3, compra.getCantidad());
                    ps.setInt(4, "Recibido".equalsIgnoreCase(compra.getEstado()) ? compra.getCantidad() : 0);
                    ps.setString(5, compra.getUnidad());
                    ps.setDouble(6, nuevoCosto);
                    ps.setDouble(7, nuevoValor);
                    ps.setString(8, compra.getEstado());
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
    // -------------------------- CALCULAR TOTAL INVENTARIO --------------------------
    public double calcularTotalInventario() {
        double total = 0;
        String sql = "SELECT SUM(valor_inventario) AS total FROM inventario WHERE estado <> 'Cancelado'";

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
    // -------------------------- ALERTA DE STOCK BAJO --------------------------
    public boolean necesitaReabastecer(int stockActual) {
    int UMBRAL = 10;
    return stockActual < UMBRAL;
}
    
    // -------------------------- DESCONTAR ARTÍCULO --------------------------
    public boolean inventarioDescontarArticulo(int idArticulo, int cantidad) {
        String sql = "UPDATE inventario SET stock_actual = stock_actual - ? WHERE id_articulo = ?";

        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, cantidad);
            ps.setInt(2, idArticulo);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al descontar inventario: " + e.getMessage());
            return false;
        }
    }


    public void registrarInventarioPorCompra(Compra c) {
    String sql = "INSERT INTO inventario(id_articulo, nombre, unidad, cantidad_comprada, stock_actual, costo_unitario, valor_inventario, idCompra, estado) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, c.getIdArticulo());
        ps.setString(2, c.getNombreArticulo()); // nombre histórico
        ps.setString(3, c.getUnidad());
        ps.setInt(4, c.getCantidad()); // cantidad_comprada
        ps.setInt(5, c.getCantidad()); // stock_actual inicial
        ps.setDouble(6, c.getPrecioUnitario());
        ps.setDouble(7, c.getCantidad() * c.getPrecioUnitario());
        ps.setInt(8, c.getId()); // ID de la compra
        ps.setString(9, c.getEstado());

        ps.executeUpdate();

    } catch (SQLException e) {
        System.err.println("Error registrarInventarioPorCompra: " + e.getMessage());
    }
}



}

