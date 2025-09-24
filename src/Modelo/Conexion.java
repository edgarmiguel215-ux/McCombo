
package Modelo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    Connection con;
    public Connection getConnection(){
        try{
            String myBD = "jdbc:mysql://localhost:3306/sistemaVenta?serverTimezone=UTC";
            con = DriverManager.getConnection(myBD, "root", "29092005");
            return con;
        } catch(SQLException e){
            System.out.println(e.toString());
        }
        return null;
    }
}
