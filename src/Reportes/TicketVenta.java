
package Reportes;


import Modelo.CatalogosProductos;
import Modelo.Cliente;
import Modelo.ClienteDao;
import Modelo.DetalleProducto;
import Modelo.DetalleProductoDAO;
import Modelo.DetalleTicketDAO;
import Modelo.InventarioDAO;
import Modelo.ProductoDAO;
import Modelo.TicketDAO;
import Modelo.Venta;
import Modelo.VentaDao;
import Vista.Carrito;
import java.awt.Color;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;



// Librerías iTextPDF 5.5.1

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Image;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import java.io.FileOutputStream;



public class TicketVenta extends javax.swing.JPanel {

    private Carrito carrito; // referencia al carrito
    private int idUsuario;
    private String autorizacionTarjeta;
    private String terminalTarjeta;
    private boolean datosTarjetaCapturados = false;


    public void setCarrito(Carrito carrito) {
    this.carrito = carrito;
}

    private double totalPagar = 0;
    
    private javax.swing.JTextField txtAutorizacion;
    private javax.swing.JTextField txtTerminal;

    public TicketVenta(int idUsuario) {
        initComponents();
        this.idUsuario = idUsuario; // aquí sí se guarda el ID correcto
        System.out.println("TicketVenta inicializado con idUsuario: " + this.idUsuario);
    
    
    txtAutorizacion = new javax.swing.JTextField();
    txtTerminal = new javax.swing.JTextField();
    configurarListenerCambio();
    configurarCamposTicket();

    TablaProductosComprados.setBackground(Color.WHITE);
    TablaProductosComprados.setForeground(Color.BLACK);
    TablaProductosComprados.setGridColor(Color.LIGHT_GRAY);
    TablaProductosComprados.setFillsViewportHeight(true);

    jScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    txtNombreCliente.setText(""); 
    txtMontoRecibido.setText("0.00");
    txtCambio.setText("0.00");
    txtDescuento.setEditable(false);

    // 👉 Listener para ocultar/mostrar campos según forma de pago
    comboFormaPago.addActionListener(e -> {
    String formaPago = comboFormaPago.getSelectedItem().toString();

    if (formaPago.equalsIgnoreCase("TARJETA")) {
        // Ocultar campos de efectivo
        txtMontoRecibido.setVisible(false);
        txtCambio.setVisible(false);
        jLabel13.setVisible(false);
        jLabel14.setVisible(false);

        txtMontoRecibido.setText("Pago con tarjeta");
        txtCambio.setText("0.00");

        // 👉 Abrir formulario de tarjeta inmediatamente
        FormularioTarjeta form = new FormularioTarjeta(
            (javax.swing.JFrame) javax.swing.SwingUtilities.getWindowAncestor(this),
            this,
            -1,   // idTicket aún no existe
            -1    // idCliente aún no existe
        );
        form.setVisible(true);

    } else {
        // Mostrar campos de efectivo
        txtMontoRecibido.setVisible(true);
        txtCambio.setVisible(true);
        jLabel13.setVisible(true);
        jLabel14.setVisible(true);

        txtMontoRecibido.setText("0.00");
        txtCambio.setText("0.00");
    }

    panelTicket.revalidate();
    panelTicket.repaint();
});


}


    private void configurarListenerCambio() {
        txtMontoRecibido.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calcularCambio(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calcularCambio(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calcularCambio(); }
        });
    }

    private void calcularCambio() {
        try {
            if (comboFormaPago.getSelectedItem().toString().equalsIgnoreCase("EFECTIVO")) {
                double montoRecibido = Double.parseDouble(txtMontoRecibido.getText());
                double totalPagar = Double.parseDouble(txtTotalPagar.getText());
                double cambio = montoRecibido - totalPagar;
                txtCambio.setText(String.format("%.2f", cambio >= 0 ? cambio : 0));
            } else {
                txtCambio.setText("0.00");
            }
        } catch (NumberFormatException ex) {
            txtCambio.setText("0.00");
        }
    }

    
    public void llenarTicket(
        String cajero,
        String fecha,
        String numeroTicket,
        List<CatalogosProductos> productos,
        double subtotal,
        double iva,
        double descuento,
        double totalPagar
    ) {
        txtNombreCajero.setText(cajero);
        txtFechaActual.setText(fecha);
        txtNoTicket.setText(numeroTicket);
        txtTotalPagar.setText(String.format("%.2f", totalPagar));

        DefaultTableModel modelo = (DefaultTableModel) TablaProductosComprados.getModel();
        modelo.setRowCount(0);

        for (CatalogosProductos p : productos) {
            int cantidad = p.getCantidadSeleccionada();
            double precio  = p.getPrecio();
            double total   = cantidad * precio;

            modelo.addRow(new Object[]{ cantidad, p.getNombre(), precio, total });
        }

        txtSubtotal.setText(String.format("%.2f", subtotal));
        txtIVA.setText(String.format("%.2f", iva));
        txtDescuento.setText(String.format("%.2f", descuento));
    }

    private void configurarCamposTicket() {
        txtMontoRecibido.setEditable(true);
        txtMontoRecibido.setBorder(javax.swing.BorderFactory.createLineBorder(Color.ORANGE, 2));

        txtNombreCliente.setEditable(true);
        txtNombreCliente.setBorder(javax.swing.BorderFactory.createLineBorder(Color.ORANGE, 2));

        // Campos solo lectura
        txtNoTicket.setEditable(false); txtNoTicket.setBorder(null);
        txtFechaActual.setEditable(false); txtFechaActual.setBorder(null);
        txtNombreCajero.setEditable(false); txtNombreCajero.setBorder(null);
        txtSubtotal.setEditable(false); txtSubtotal.setBorder(null);
        txtIVA.setEditable(false); txtIVA.setBorder(null);
        txtDescuento.setEditable(false); txtDescuento.setBorder(null);
        txtCambio.setEditable(false); txtCambio.setBorder(null);
        txtTotalPagar.setEditable(false); txtTotalPagar.setBorder(null);

        comboFormaPago.setEditable(false);
        comboFormaPago.setBorder(null);
    }
    
    public String generarNoTicket() {
    String fecha = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
    int aleatorio = (int)(Math.random() * 9000) + 1000; // 4 dígitos
    return "TCK-" + fecha + "-" + aleatorio;
}



    public String generarTicketPDF(int idTicket) {
    String rutaPDF = "";
    try {
        // Generar número único de ticket SOLO UNA VEZ
        String noTicket = generarNoTicket();
        txtNoTicket.setText(noTicket);

        String rutaDescargas = System.getProperty("user.home") + "/Downloads/";
        String archivo = rutaDescargas + "Ticket_" + noTicket + ".pdf";

        // Documento con ancho de ticket térmico (~80 mm)
        Document document = new Document(new com.itextpdf.text.Rectangle(226, 600));
        PdfWriter.getInstance(document, new FileOutputStream(archivo));
        document.open();

        // Fuentes
        Font fuentePequena = FontFactory.getFont(FontFactory.HELVETICA, 6, Font.NORMAL);
        Font fuenteTitulo  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 6, Font.BOLD);

        // Separador punteado reutilizable
        DottedLineSeparator separador = new DottedLineSeparator();
        separador.setGap(2f);
        separador.setLineWidth(0.5f);

        // Logo
        try {
            Image logo = Image.getInstance(getClass().getResource("/Img/IcoMc.png"));
            logo.scaleToFit(80, 80);
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);
        } catch (Exception ex) {
            Paragraph titulo = new Paragraph("Restaurantes McDonald's", fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
        }

        // Encabezado
        Paragraph direccion = new Paragraph("Costera Miguel Alemán #500, Acapulco de Juárez, Gro.", fuentePequena);
        direccion.setAlignment(Element.ALIGN_CENTER);
        document.add(direccion);

        Paragraph rfc = new Paragraph("RFC: ABC123456XYZ", fuentePequena);
        rfc.setAlignment(Element.ALIGN_CENTER);
        document.add(rfc);

        document.add(new Chunk(separador));

        // 👉 Datos relevantes
        document.add(new Paragraph("TICKET NO.: " + noTicket, fuentePequena));
        document.add(new Paragraph("FECHA: " + txtFechaActual.getText(), fuentePequena));
        document.add(new Paragraph("CAJERO: " + txtNombreCajero.getText(), fuentePequena));

        document.add(new Chunk(separador));

        // Tabla de productos SIN bordes
        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidths(new float[]{1.2f, 2.5f, 1.5f, 1.5f});
        tabla.setWidthPercentage(100);

        String[] headers = {"CANT.", "DESCRIPCIÓN", "P. UNIT.", "TOTAL"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Paragraph(h, fuentePequena));
            cell.setBorder(PdfPCell.NO_BORDER);
            tabla.addCell(cell);
        }

        DefaultTableModel modelo = (DefaultTableModel) TablaProductosComprados.getModel();
        for (int i = 0; i < modelo.getRowCount(); i++) {
            for (int j = 0; j < modelo.getColumnCount(); j++) {
                PdfPCell cell = new PdfPCell(new Paragraph(modelo.getValueAt(i, j).toString(), fuentePequena));
                cell.setBorder(PdfPCell.NO_BORDER);
                tabla.addCell(cell);
            }
        }

        document.add(tabla);
        document.add(new Chunk(separador));

        // Totales
        document.add(new Paragraph("SUBTOTAL: " + txtSubtotal.getText(), fuentePequena));
        document.add(new Paragraph("IVA: " + txtIVA.getText(), fuentePequena));
        document.add(new Paragraph("DESCUENTO: " + txtDescuento.getText(), fuentePequena));
        document.add(new Paragraph("TOTAL: " + txtTotalPagar.getText(), fuentePequena));

        // Forma de pago desde el combo
        String formaPago = comboFormaPago.getSelectedItem().toString();
        document.add(new Paragraph("FORMA DE PAGO: " + formaPago, fuentePequena));

        if (formaPago.equalsIgnoreCase("TARJETA")) {
            // Pago con tarjeta: solo imprime datos de tarjeta
            document.add(new Paragraph("AUTORIZACIÓN: " + txtAutorizacion.getText(), fuentePequena));
            document.add(new Paragraph("TERMINAL: " + txtTerminal.getText(), fuentePequena));
            document.add(new Paragraph("RECIBIDO: TARJETA", fuentePequena));
            document.add(new Paragraph("CAMBIO: 0.00", fuentePequena));
        } else {
            // Pago en efectivo: imprime monto recibido y cambio
            document.add(new Paragraph("MONTO RECIBIDO: " + txtMontoRecibido.getText(), fuentePequena));
            document.add(new Paragraph("CAMBIO: " + txtCambio.getText(), fuentePequena));
        }

        document.add(new Chunk(separador));

        // Mensajes finales centrados
        Paragraph gracias = new Paragraph("¡GRACIAS POR SU PREFERENCIA!", fuenteTitulo);
        gracias.setAlignment(Element.ALIGN_CENTER);
        document.add(gracias);

        Paragraph cliente = new Paragraph("Cliente  Sr/Sra: " + txtNombreCliente.getText(), fuentePequena);
        cliente.setAlignment(Element.ALIGN_CENTER);
        document.add(cliente);

        Paragraph tel = new Paragraph("Para Información Comunicarse al Tel: 744-555-1234", fuentePequena);
        tel.setAlignment(Element.ALIGN_CENTER);
        document.add(tel);

        Paragraph aviso = new Paragraph("*No se aceptan cambios ni devoluciones en alimentos y Bebidas.*", fuentePequena);
        aviso.setAlignment(Element.ALIGN_CENTER);
        document.add(aviso);

        document.close();

        // Limpiar carrito y campos
        limpiarCarrito();
        rutaPDF = archivo; // guarda la ruta del archivo generado
        JOptionPane.showMessageDialog(this,
            "Ticket generado en PDF en Descargas:\n" + archivo);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
            "Error al generar ticket: " + e.getMessage());
    }
    return rutaPDF; // ⬅️ ahora sí devuelve la ruta para guardarla en la BD
}

    private void limpiarCarrito() {
    // Vaciar tabla de productos
    DefaultTableModel modelo = (DefaultTableModel) TablaProductosComprados.getModel();
    modelo.setRowCount(0);

    // Resetear campos de totales
    txtSubtotal.setText("");
    txtIVA.setText("");
    txtDescuento.setText("");
    txtTotalPagar.setText("");
    txtMontoRecibido.setText("");
    txtCambio.setText("");

    // Opcional: limpiar cliente y ticket
    txtNombreCliente.setText("");
    txtNoTicket.setText("");
}



    // Ajusta la clase interna:
    public class FormularioTarjeta extends JDialog {
    private JTextField txtAutorizacionForm;
    private JTextField txtTerminalForm;
    private JButton btnConfirmar;
    private TicketVenta ticketVenta;
    private int idTicket;
    private int idCliente; // ⬅️ nuevo

    public FormularioTarjeta(JFrame parent, TicketVenta ticketVenta, int idTicket, int idCliente) {
        super(parent, "Pago con Tarjeta", true);
        this.ticketVenta = ticketVenta;
        this.idTicket = idTicket;
        this.idCliente = idCliente;

        setSize(300, 200);
        setLocationRelativeTo(parent);

        txtAutorizacionForm = new JTextField(15);
        txtTerminalForm = new JTextField(15);
        btnConfirmar = new JButton("Confirmar Pago");

        JPanel panel = new JPanel();
        panel.add(new JLabel("Autorización:"));
        panel.add(txtAutorizacionForm);
        panel.add(new JLabel("Terminal:"));
        panel.add(txtTerminalForm);
        panel.add(btnConfirmar);

        add(panel);

        btnConfirmar.addActionListener(e -> {
            String autorizacion = txtAutorizacionForm.getText().trim();
            String terminal = txtTerminalForm.getText().trim();

            if (autorizacion.isEmpty() || terminal.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Completa todos los campos.");
                return;
            }

            // Guardar en TicketVenta y continuar flujo
            ticketVenta.txtAutorizacion.setText(autorizacion);
            ticketVenta.txtTerminal.setText(terminal);

            // Cerrar flujo: generar PDF y actualizar rutas
            ticketVenta.cerrarFlujoGeneracionYActualizacion(idTicket, idCliente);

            dispose();
        });
    }
}

    private void cerrarFlujoGeneracionYActualizacion(int idTicket, int idCliente) {
    try {
        TicketDAO ticketDAO = new TicketDAO();
        ClienteDao clienteDAO = new ClienteDao();

        // ⚡ Aquí puedes volver a recorrer los productos y descontar inventario
        DefaultTableModel modelo = (DefaultTableModel) TablaProductosComprados.getModel();
        ProductoDAO productoDAO = new ProductoDAO();
        DetalleProductoDAO detalleProductoDAO = new DetalleProductoDAO();
        InventarioDAO inventarioDAO = new InventarioDAO();

        for (int i = 0; i < modelo.getRowCount(); i++) {
            int cantidad = Integer.parseInt(modelo.getValueAt(i, 0).toString());
            String nombreProducto = modelo.getValueAt(i, 1).toString();
            int idProducto = productoDAO.obtenerIdProductoPorNombre(nombreProducto);

            List<DetalleProducto> articulos = detalleProductoDAO.obtenerArticulosPorProducto(idProducto);
            for (DetalleProducto dp : articulos) {
                int cantidadNecesaria = dp.getCantidad() * cantidad;
                inventarioDAO.descontarStock(dp.getIdArticulo(), cantidadNecesaria);
            }
        }

        String rutaPDF = generarTicketPDF(idTicket);
        ticketDAO.actualizarRutaPDF(idTicket, rutaPDF);
        clienteDAO.actualizarRutaPDFCliente(idCliente, rutaPDF);

        limpiarCarrito();
        if (carrito != null) carrito.limpiarCarrito();

        JOptionPane.showMessageDialog(this, "Ticket registrado correctamente.");

        java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);
        if (parentWindow instanceof javax.swing.JFrame) {
            ((javax.swing.JFrame) parentWindow).dispose();
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al cerrar flujo: " + e.getMessage());
    }
}

    // Método que valida los campos antes de generar el ticket
    private boolean validarCampos() {
    // Validar cliente
    if (txtNombreCliente.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Debes ingresar el nombre del cliente.");
        return false;
    }

    // Validar forma de pago
    if (comboFormaPago.getSelectedItem() == null) {
        JOptionPane.showMessageDialog(this, "Debes seleccionar una forma de pago.");
        return false;
    }

    // Validar productos
    DefaultTableModel modelo = (DefaultTableModel) TablaProductosComprados.getModel();
    if (modelo.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "Debes agregar al menos un producto al ticket.");
        return false;
    }

    // Validar usuario
    if (this.idUsuario <= 0) {
        JOptionPane.showMessageDialog(this, "Error: el usuario no está inicializado correctamente.");
        return false;
    }

    // Validar monto recibido si es efectivo
    if (comboFormaPago.getSelectedItem().toString().equalsIgnoreCase("EFECTIVO")) {
        try {
            double montoRecibido = Double.parseDouble(txtMontoRecibido.getText());
            double total = Double.parseDouble(txtTotalPagar.getText());
            if (montoRecibido < total) {
                JOptionPane.showMessageDialog(this, "El monto recibido no cubre el total a pagar.");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Monto recibido inválido.");
            return false;
        }
    }

    // ⚠️ Para TARJETA: NO validar aquí autorización/terminal,
    // porque se capturan en el formulario. Solo asegúrate de que
    // nombre del cliente y productos estén llenos (ya validado arriba).

    return true;
}


    private void procesarTicket() {
    try {
        // Validar campos antes de procesar
        if (!validarCampos()) return;

        // 1. Registrar cliente
        ClienteDao clienteDAO = new ClienteDao();
        Cliente cliente = new Cliente(txtNombreCliente.getText());
        int idCliente = clienteDAO.registrarCliente(cliente);

        // 2. Registrar ticket
        TicketDAO ticketDAO = new TicketDAO();
        int idUsuario = this.idUsuario;
        double total = Double.parseDouble(txtTotalPagar.getText());
        String metodoPago = comboFormaPago.getSelectedItem().toString();

        int idTicket = ticketDAO.registrarTicket(idCliente, idUsuario, total, metodoPago);
        
        // 2b. Registrar venta en tabla ventas
        VentaDao ventaDAO = new VentaDao();
        Venta venta = new Venta();
        venta.setId_cliente(idCliente);
        venta.setVendedor(txtNombreCajero.getText());
        venta.setTotal(total);
        venta.setFecha(new java.sql.Timestamp(System.currentTimeMillis()).toString());

        int idVenta = ventaDAO.registrarVenta(venta);

        if (idVenta > 0) {
            System.out.println("✅ Venta registrada en SQL con ID: " + idVenta);
        } else {
            System.out.println("❌ Error al registrar venta en SQL.");
        }


        // 3. Registrar detalle (siempre)
        DetalleTicketDAO detalleDAO = new DetalleTicketDAO();
        ProductoDAO productoDAO = new ProductoDAO();
        DefaultTableModel modelo = (DefaultTableModel) TablaProductosComprados.getModel();

        for (int i = 0; i < modelo.getRowCount(); i++) {
            int cantidad = Integer.parseInt(modelo.getValueAt(i, 0).toString());
            String nombreProducto = modelo.getValueAt(i, 1).toString();
            double precioUnitario = Double.parseDouble(modelo.getValueAt(i, 2).toString());
            double subtotal = Double.parseDouble(modelo.getValueAt(i, 3).toString());

            int idProducto = productoDAO.obtenerIdProductoPorNombre(nombreProducto);
            detalleDAO.registrarDetalle(idTicket, idProducto, cantidad, precioUnitario, subtotal);
            
            // ==============================
            // NUEVO: Descontar inventario
            // ==============================
            DetalleProductoDAO detalleProductoDAO = new DetalleProductoDAO();
            InventarioDAO inventarioDAO = new InventarioDAO();
            DetalleTicketDAO detalleTicketDAO = new DetalleTicketDAO();

            List<DetalleProducto> articulos = detalleProductoDAO.obtenerArticulosPorProducto(idProducto);

            boolean hayStock = true;
            for (DetalleProducto dp : articulos) {
                int cantidadNecesaria = dp.getCantidad() * cantidad;
                if (!inventarioDAO.verificarStock(dp.getIdArticulo(), cantidadNecesaria)) {
                    hayStock = false;
                    System.out.println("Stock insuficiente para artículo id=" + dp.getIdArticulo());
                    break;
                }
            }

            if (hayStock) {
                // Registrar detalle del ticket
                detalleTicketDAO.registrarDetalle(idTicket, idProducto, cantidad, precioUnitario, subtotal);

                // Descontar inventario
                for (DetalleProducto dp : articulos) {
                    int cantidadNecesaria = dp.getCantidad() * cantidad;
                    inventarioDAO.descontarStock(dp.getIdArticulo(), cantidadNecesaria);
                }
            } else {
                System.out.println("Venta cancelada por falta de stock.");
            }

        } 

       // 4. Ramificar por forma de pago
        if (metodoPago.equalsIgnoreCase("TARJETA")) {
            // Abrir formulario de tarjeta con el idTicket real
            FormularioTarjeta form = new FormularioTarjeta(
                (javax.swing.JFrame) javax.swing.SwingUtilities.getWindowAncestor(this),
                this,
                idTicket,
                idCliente // ⬅️ pasa también el cliente para cerrar flujo
            );
            form.setVisible(true);
            return; // el resto se hace al confirmar en el formulario
        }

        // EFECTIVO: cerrar flujo aquí
        
        String rutaPDF = generarTicketPDF(idTicket);
        ticketDAO.actualizarRutaPDF(idTicket, rutaPDF);
        clienteDAO.actualizarRutaPDFCliente(idCliente, rutaPDF);

        limpiarCarrito();
        if (carrito != null) carrito.limpiarCarrito();

        JOptionPane.showMessageDialog(this, "Ticket registrado correctamente.");

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al registrar ticket: " + e.getMessage());
    }
}

    
    private void actualizarUIFormaPago() {
    Object sel = comboFormaPago.getSelectedItem();
    String formaPago = (sel == null) ? "" : sel.toString().trim();

    boolean esTarjeta = formaPago.equalsIgnoreCase("TARJETA");

    // Mostrar/ocultar campos relacionados con EFECTIVO
    txtMontoRecibido.setVisible(!esTarjeta);
    txtCambio.setVisible(!esTarjeta);
    jLabel13.setVisible(!esTarjeta); // MONTO RECIBIDO
    jLabel14.setVisible(!esTarjeta); // CAMBIO

    if (esTarjeta) {
        txtMontoRecibido.setText("0.00");
        txtCambio.setText("0.00");
    } else {
        txtMontoRecibido.setText("0.00");
        txtCambio.setText("0.00");
    }

    panelTicket.revalidate();
    panelTicket.repaint();
}

    
    
    

    /*
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelTicket = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TablaProductosComprados = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtSubtotal = new javax.swing.JTextField();
        txtIVA = new javax.swing.JTextField();
        txtDescuento = new javax.swing.JTextField();
        txtTotalPagar = new javax.swing.JTextField();
        txtMontoRecibido = new javax.swing.JTextField();
        txtCambio = new javax.swing.JTextField();
        txtNoTicket = new javax.swing.JTextField();
        txtNombreCajero = new javax.swing.JTextField();
        txtFechaActual = new javax.swing.JTextField();
        txtNombreCliente = new javax.swing.JTextField();
        btnFinalizarTicket = new javax.swing.JButton();
        comboFormaPago = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelTicket.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/IcoMc.png"))); // NOI18N

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Restaurantes McDonald´S");

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Costera Miguel Alemán #500, Acapulco de Juárez, Gro.");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("RFC: ABC123456XYZ");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("TICKET NO.:");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("FECHA:");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("CAJERO:");

        TablaProductosComprados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CANT.", "DESCRIPCIÓN", "P. UNIT.", "TOTAL"
            }
        ));
        jScrollPane1.setViewportView(TablaProductosComprados);
        if (TablaProductosComprados.getColumnModel().getColumnCount() > 0) {
            TablaProductosComprados.getColumnModel().getColumn(0).setPreferredWidth(60);
            TablaProductosComprados.getColumnModel().getColumn(2).setPreferredWidth(60);
            TablaProductosComprados.getColumnModel().getColumn(3).setPreferredWidth(60);
        }

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("SUBTOTAL:");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("IVA:");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setText("DESCUENTO:");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("TOTAL A PAGAR:");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 0, 0));
        jLabel12.setText("FORMA DE PAGO:");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(0, 0, 0));
        jLabel13.setText("MONTO RECIBIDO:");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 0, 0));
        jLabel14.setText("CAMBIO:");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 0, 0));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("¡GRACIAS POR SU PREFERENCIA!");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 0, 0));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("Para aclaraciones o pedidos: 744-555-1234");

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 0, 0));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("*No se aceptan cambios ni devoluciones en alimentos.*");

        txtNombreCliente.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtNombreCliente.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        btnFinalizarTicket.setBackground(new java.awt.Color(0, 0, 0));
        btnFinalizarTicket.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnFinalizarTicket.setForeground(new java.awt.Color(255, 255, 255));
        btnFinalizarTicket.setText("[ Finalizar / Generar Ticket ] ");
        btnFinalizarTicket.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFinalizarTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinalizarTicketActionPerformed(evt);
            }
        });

        comboFormaPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "EFECTIVO", "TARJETA" }));
        comboFormaPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboFormaPagoActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(0, 0, 0));
        jLabel18.setText("Sr/Sra:");

        javax.swing.GroupLayout panelTicketLayout = new javax.swing.GroupLayout(panelTicket);
        panelTicket.setLayout(panelTicketLayout);
        panelTicketLayout.setHorizontalGroup(
            panelTicketLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTicketLayout.createSequentialGroup()
                .addGap(91, 91, 91)
                .addGroup(panelTicketLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelTicketLayout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMontoRecibido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelTicketLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelTicketLayout.createSequentialGroup()
                            .addComponent(jLabel11)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtTotalPagar))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelTicketLayout.createSequentialGroup()
                            .addComponent(jLabel10)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtDescuento))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelTicketLayout.createSequentialGroup()
                            .addComponent(jLabel9)
                            .addGap(18, 18, 18)
                            .addComponent(txtIVA))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelTicketLayout.createSequentialGroup()
                            .addComponent(jLabel8)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtSubtotal))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelTicketLayout.createSequentialGroup()
                            .addComponent(jLabel6)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtFechaActual))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelTicketLayout.createSequentialGroup()
                            .addComponent(jLabel7)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtNombreCajero))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelTicketLayout.createSequentialGroup()
                            .addComponent(jLabel5)
                            .addGap(12, 12, 12)
                            .addComponent(txtNoTicket))
                        .addGroup(panelTicketLayout.createSequentialGroup()
                            .addGroup(panelTicketLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(panelTicketLayout.createSequentialGroup()
                                    .addComponent(jLabel14)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtCambio, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnFinalizarTicket))
                                .addGroup(panelTicketLayout.createSequentialGroup()
                                    .addComponent(jLabel12)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(comboFormaPago, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGap(3, 3, 3))
                        .addGroup(panelTicketLayout.createSequentialGroup()
                            .addComponent(jLabel18)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(110, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTicketLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelTicketLayout.setVerticalGroup(
            panelTicketLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTicketLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addGap(30, 30, 30)
                .addGroup(panelTicketLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtNoTicket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelTicketLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtFechaActual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelTicketLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtNombreCajero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addGroup(panelTicketLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelTicketLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtIVA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelTicketLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelTicketLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtTotalPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelTicketLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(comboFormaPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelTicketLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtMontoRecibido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(panelTicketLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txtCambio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFinalizarTicket))
                .addGap(51, 51, 51)
                .addComponent(jLabel15)
                .addGap(11, 11, 11)
                .addGroup(panelTicketLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel17)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        add(panelTicket, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 660, 1020));
    }// </editor-fold>//GEN-END:initComponents

    private void btnFinalizarTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinalizarTicketActionPerformed
        // TODO add your handling code here:
                                                       
    System.out.println("ID de usuario usado: " + this.idUsuario);

    if (!validarCampos()) {
        return;
    }

    // Si es EFECTIVO, procesar directamente
    procesarTicket();

    java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);
    if (parentWindow instanceof javax.swing.JFrame) {
        ((javax.swing.JFrame) parentWindow).dispose();
    }

        
    }//GEN-LAST:event_btnFinalizarTicketActionPerformed

    private void comboFormaPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboFormaPagoActionPerformed
        // TODO add your handling code here:
    actualizarUIFormaPago();

    }//GEN-LAST:event_comboFormaPagoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable TablaProductosComprados;
    private javax.swing.JButton btnFinalizarTicket;
    private javax.swing.JComboBox<String> comboFormaPago;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panelTicket;
    private javax.swing.JTextField txtCambio;
    private javax.swing.JTextField txtDescuento;
    private javax.swing.JTextField txtFechaActual;
    private javax.swing.JTextField txtIVA;
    private javax.swing.JTextField txtMontoRecibido;
    private javax.swing.JTextField txtNoTicket;
    private javax.swing.JTextField txtNombreCajero;
    private javax.swing.JTextField txtNombreCliente;
    private javax.swing.JTextField txtSubtotal;
    private javax.swing.JTextField txtTotalPagar;
    // End of variables declaration//GEN-END:variables
//    private javax.swing.JTextField txtAutorizacion;
//    private javax.swing.JTextField txtTerminal;


}
