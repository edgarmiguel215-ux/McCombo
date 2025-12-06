
package Modelo;

import Vista.DetalleTicket;
import java.sql.*;
import java.util.*;
import javax.swing.JOptionPane;


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
    String sqlSelect = "SELECT idInventario FROM inventario WHERE idCompra = ?";
    String sqlInsert = "INSERT INTO inventario (idCompra, id_articulo, nombre, unidad, cantidad_comprada, stock_actual, costo_unitario, valor_inventario, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    String sqlUpdate = "UPDATE inventario SET id_articulo=?, nombre=?, unidad=?, cantidad_comprada=?, stock_actual=?, costo_unitario=?, valor_inventario=?, estado=? WHERE idCompra=?";
    
    try (Connection con = cn.getConnection();
         PreparedStatement psSelect = con.prepareStatement(sqlSelect)) {

        psSelect.setInt(1, editada.getId());
        ResultSet rs = psSelect.executeQuery();

        double valor = editada.getCantidad() * editada.getPrecioUnitario();

        if (rs.next()) {
            // 🔹 REGISTRO EXISTE —> UPDATE
            try (PreparedStatement psUpdate = con.prepareStatement(sqlUpdate)) {
                psUpdate.setInt(1, editada.getIdArticulo());
                psUpdate.setString(2, editada.getNombreArticulo());
                psUpdate.setString(3, editada.getUnidad());
                psUpdate.setInt(4, editada.getCantidad());
                psUpdate.setInt(5, editada.getCantidad());
                psUpdate.setDouble(6, editada.getPrecioUnitario());
                psUpdate.setDouble(7, valor);
                psUpdate.setString(8, editada.getEstado());
                psUpdate.setInt(9, editada.getId()); // idCompra
                psUpdate.executeUpdate();
            }
        } else {
            // 🔹 NO EXISTE —> INSERT
            try (PreparedStatement psInsert = con.prepareStatement(sqlInsert)) {
                psInsert.setInt(1, editada.getId());
                psInsert.setInt(2, editada.getIdArticulo());
                psInsert.setString(3, editada.getNombreArticulo());
                psInsert.setString(4, editada.getUnidad());
                psInsert.setInt(5, editada.getCantidad());
                psInsert.setInt(6, editada.getCantidad());
                psInsert.setDouble(7, editada.getPrecioUnitario());
                psInsert.setDouble(8, valor);
                psInsert.setString(9, editada.getEstado());
                psInsert.executeUpdate();
            }
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al actualizar inventario: " + e.getMessage());
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

            String sqlUpdate = "UPDATE inventario SET nombre = ?, stock_actual = ?, valor_inventario = ? WHERE id_articulo = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
                ps.setString(1, c.getNombreArticulo()); // nombre correcto
                ps.setInt(2, nuevoStock);
                ps.setDouble(3, nuevoValor);
                ps.setInt(4, c.getIdArticulo());
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
                String sqlUpdate = "UPDATE inventario SET nombre = ?, cantidad_comprada = ?, stock_actual = ?, costo_unitario = ?, valor_inventario = ?, unidad = ?, estado = ? WHERE id_articulo = ?";
                try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
                    ps.setString(1, compra.getNombreArticulo()); // nombre correcto
                    ps.setInt(2, nuevaCantidadComprada);
                    ps.setInt(3, nuevoStock);
                    ps.setDouble(4, nuevoCosto);
                    ps.setDouble(5, nuevoValor);
                    ps.setString(6, compra.getUnidad());
                    ps.setString(7, compra.getEstado());
                    ps.setInt(8, compra.getIdArticulo());
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
    String sql = "INSERT INTO inventario (id_articulo, nombre, stock_actual, costo_unitario, valor_inventario, estado) " +
                 "SELECT ?, ?, ?, ?, ?, ? FROM DUAL WHERE NOT EXISTS " +
                 "(SELECT 1 FROM inventario WHERE id_articulo = ?)";

    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, c.getIdArticulo());
        ps.setString(2, c.getNombreArticulo()); // nombre del artículo
        ps.setInt(3, c.getCantidad());
        ps.setDouble(4, c.getPrecioUnitario());
        ps.setDouble(5, c.getCantidad() * c.getPrecioUnitario());
        ps.setString(6, c.getEstado());
        ps.setInt(7, c.getIdArticulo());

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


    public boolean existePorIdCompra(int idCompra) {
    String sql = "SELECT 1 FROM inventario WHERE idCompra = ?";
    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idCompra);
        ResultSet rs = ps.executeQuery();
        return rs.next();

    } catch (SQLException e) {
        System.err.println("Error existePorIdCompra: " + e.getMessage());
        return false;
    }
}


    public boolean actualizarInventarioPorCompra(Compra c) {
    String sql = "UPDATE inventario SET nombre=?, unidad=?, cantidad_comprada=?, stock_actual=?, costo_unitario=?, valor_inventario=?, estado=? WHERE idCompra=?";

    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, c.getNombreArticulo());
        ps.setString(2, c.getUnidad());
        ps.setInt(3, c.getCantidad());
        ps.setInt(4, c.getCantidad()); // stock actual reemplazado, si no tienes lógica diferente
        ps.setDouble(5, c.getPrecioUnitario());
        ps.setDouble(6, c.getCantidad() * c.getPrecioUnitario());
        ps.setString(7, c.getEstado());
        ps.setInt(8, c.getId()); // idCompra

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        System.err.println("Error actualizarInventarioPorCompra: " + e.getMessage());
        return false;
    }
}

    //////DESCONTAR AL INVENTARIO///////////
    
    public boolean descontarStock(int idArticulo, int cantidad) {
        String sqlSelect = "SELECT stock_actual FROM inventario WHERE id_articulo = ?";
        String sqlUpdate = "UPDATE inventario SET stock_actual = stock_actual - ? WHERE id_articulo = ?";

        try (Connection con = cn.getConnection()) {
            con.setAutoCommit(false);

            // 1) comprobar stock actual
            try (PreparedStatement psSel = con.prepareStatement(sqlSelect)) {
                psSel.setInt(1, idArticulo);
                ResultSet rs = psSel.executeQuery();
                if (!rs.next()) {
                    con.rollback();
                    System.out.println("Inventario: artículo no encontrado id=" + idArticulo);
                    return false;
                }
                int stockActual = rs.getInt("stock_actual");
                if (stockActual < cantidad) {
                    con.rollback();
                    System.out.println("Inventario: stock insuficiente para id=" + idArticulo +
                                       " (actual=" + stockActual + ", pedido=" + cantidad + ")");
                    return false;
                }
            }

            // 2) hacer update
            try (PreparedStatement psUpd = con.prepareStatement(sqlUpdate)) {
                psUpd.setInt(1, cantidad);
                psUpd.setInt(2, idArticulo);
                int r = psUpd.executeUpdate();
                if (r == 0) {
                    con.rollback();
                    return false;
                }
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public boolean verificarStock(int idArticulo, int cantidad) {
    String sql = "SELECT stock_actual FROM inventario WHERE id_articulo = ?";

    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idArticulo);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int stockActual = rs.getInt("stock_actual");
            return stockActual >= cantidad; // true si hay suficiente
        } else {
            System.out.println("Inventario: artículo no encontrado id=" + idArticulo);
            return false;
        }

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
    
    
    public int obtenerStock(int idArticulo) {

    String sql = "SELECT stock_actual FROM inventario WHERE id_articulo = ?";

    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idArticulo);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt("stock_actual");
        }

    } catch (SQLException e) {
        System.out.println("Error obtenerStock: " + e.getMessage());
    }

    return -1;
}

    
    private void descontarInventario(int idTicket) {
    try {
        DetalleTicketDAO dtDAO = new DetalleTicketDAO();
        DetalleProductoDAO dpDAO = new DetalleProductoDAO();
        InventarioDAO invDAO = new InventarioDAO();

        List<Object[]> detalles = dtDAO.obtenerDetallesPorTicket(idTicket);

        for (Object[] fila : detalles) {
            int idProducto = (int) fila[0];
            int cantidadVendida = (int) fila[1];

            List<DetalleProducto> articulos = dpDAO.obtenerArticulosPorProducto(idProducto);

            for (DetalleProducto art : articulos) {
                int cantidadNecesaria = art.getCantidad() * cantidadVendida;
                invDAO.descontarStock(art.getIdArticulo(), cantidadNecesaria);
            }
        }

    } catch (Exception e) {
        System.out.println("Error descontando inventario: " + e);
    }
}

}

