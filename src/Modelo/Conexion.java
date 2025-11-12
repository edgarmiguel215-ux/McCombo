
package Modelo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL = "jdbc:mysql://localhost:3306/sistemaVenta?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "29092005";

    public Connection getConnection() {
        try {
            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexion exitosa a la base de datos.");
            return con;
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
            return null;
        }
    }
}

