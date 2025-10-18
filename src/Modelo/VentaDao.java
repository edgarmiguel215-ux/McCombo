/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author salga
 */
public class VentaDao {
    public int registrarVenta(Venta venta, Connection con) throws SQLException {
        String sql = "INSERT INTO ventas (id_cliente, vendedor, total) VALUES (?,?,?)";
        
        // Usamos 'Statement.RETURN_GENERATED_KEYS' para poder obtener el ID que MySQL genera.
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, venta.getId_cliente());
            ps.setString(2, venta.getVendedor());
            ps.setDouble(3, venta.getTotal());
            
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("No se pudo guardar la venta, no se crearon filas.");
            }

            // Obtenemos el ID generado
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Devuelve el ID de la venta
                } else {
                    throw new SQLException("No se pudo guardar la venta, no se obtuvo el ID.");
                }
            }
        }
    }

      public void registrarDetalle(DetalleVenta detalle, Connection con) throws SQLException {
        String sql = "INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario) VALUES (?,?,?,?)";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, detalle.getId_venta());
            ps.setInt(2, detalle.getIdProducto());
            ps.setInt(3, detalle.getCantidad());
            ps.setDouble(4, detalle.getPrecio());
            ps.executeUpdate();
        }
    }

}
