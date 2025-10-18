/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author salga
 */
public class Venta {
    
    private int id;
    private int id_cliente;
    private String vendedor;
    private double total;
    private String fecha;

    // Getters y Setters
    public int getId() { 
        return id; 
    }
    public void setId(int id) { 
        this.id = id; 
    }
    public int getId_cliente() { 
        return id_cliente; 
    }
    public void setId_cliente(int id_cliente) { 
        this.id_cliente = id_cliente; 
    }
    public String getVendedor() { 
        return vendedor; 
    }
    public void setVendedor(String vendedor) { 
        this.vendedor = vendedor; 
    }
    public double getTotal() { 
        return total; 
    }
    public void setTotal(double total) { 
        this.total = total; 
    }
    public String getFecha() { 
        return fecha; 
    }
    public void setFecha(String fecha) { 
        this.fecha = fecha; 
    }
}
