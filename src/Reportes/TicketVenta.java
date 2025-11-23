
package Reportes;


import Modelo.CatalogosProductos;
import com.itextpdf.text.BaseColor;
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

    private double totalPagar = 0;
    
    private javax.swing.JTextField txtAutorizacion;
    private javax.swing.JTextField txtTerminal;

    public TicketVenta() {
    initComponents();
    
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
            jLabel13.setVisible(false); // "MONTO RECIBIDO"
            jLabel14.setVisible(false); // "CAMBIO"

            // Forzar valores
            txtMontoRecibido.setText("Pago con tarjeta");
            txtCambio.setText("0.00");
        } else {
            // Mostrar campos de efectivo
            txtMontoRecibido.setVisible(true);
            txtCambio.setVisible(true);
            jLabel13.setVisible(true);
            jLabel14.setVisible(true);

            // Resetear valores
            txtMontoRecibido.setText("0.00");
            txtCambio.setText("0.00");
        }

        // Refrescar el panel
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
    

    private void generarTicketPDF() {
    try {
        String rutaDescargas = System.getProperty("user.home") + "/Downloads/";
        String archivo = rutaDescargas + "Ticket_" + txtNoTicket.getText() + ".pdf";

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

        // Datos relevantes
        document.add(new Paragraph("TICKET NO.: " + txtNoTicket.getText(), fuentePequena));
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

        Paragraph cliente = new Paragraph("Cliente: " + txtNombreCliente.getText(), fuentePequena);
        cliente.setAlignment(Element.ALIGN_CENTER);
        document.add(cliente);

        Paragraph tel = new Paragraph("Tel: 744-555-1234", fuentePequena);
        tel.setAlignment(Element.ALIGN_CENTER);
        document.add(tel);

        Paragraph aviso = new Paragraph("*No se aceptan cambios ni devoluciones en alimentos.*", fuentePequena);
        aviso.setAlignment(Element.ALIGN_CENTER);
        document.add(aviso);

        document.close();

        javax.swing.JOptionPane.showMessageDialog(this,
            "Ticket generado en PDF en Descargas:\n" + archivo);

    } catch (Exception e) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "Error al generar ticket: " + e.getMessage());
    }
}

    

    public class FormularioTarjeta extends JDialog {
    private JTextField txtAutorizacionForm;
    private JTextField txtTerminalForm;
    private JButton btnConfirmar;
    private TicketVenta ticketVenta; // referencia al ticket

    public FormularioTarjeta(JFrame parent, TicketVenta ticketVenta) {
        super(parent, "Pago con Tarjeta", true);
        this.ticketVenta = ticketVenta; // guardar referencia

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
            String autorizacion = txtAutorizacionForm.getText();
            String terminal = txtTerminalForm.getText();

            if (autorizacion.isEmpty() || terminal.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Completa todos los campos.");
            } else {
                // Guardar en la instancia de TicketVenta
                ticketVenta.txtAutorizacion.setText(autorizacion);
                ticketVenta.txtTerminal.setText(terminal);

                // Generar ticket PDF
                ticketVenta.generarTicketPDF();

                dispose();
            }
        });
    }
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

        btnFinalizarTicket.setText("[ Finalizar / Generar Ticket ] ");
        btnFinalizarTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinalizarTicketActionPerformed(evt);
            }
        });

        comboFormaPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "EFECTIVO", "TARJETA" }));

        javax.swing.GroupLayout panelTicketLayout = new javax.swing.GroupLayout(panelTicket);
        panelTicket.setLayout(panelTicketLayout);
        panelTicketLayout.setHorizontalGroup(
            panelTicketLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTicketLayout.createSequentialGroup()
                .addGap(91, 91, 91)
                .addGroup(panelTicketLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelTicketLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(87, 87, 87)
                        .addComponent(btnFinalizarTicket)
                        .addGap(20, 20, 20))
                    .addGroup(panelTicketLayout.createSequentialGroup()
                        .addGroup(panelTicketLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelTicketLayout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCambio, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelTicketLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(panelTicketLayout.createSequentialGroup()
                                    .addComponent(jLabel12)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(comboFormaPago, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(3, 3, 3))
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
                                .addComponent(txtNombreCliente, javax.swing.GroupLayout.Alignment.LEADING))
                            .addGroup(panelTicketLayout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtMontoRecibido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(110, Short.MAX_VALUE))))
        );
        panelTicketLayout.setVerticalGroup(
            panelTicketLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTicketLayout.createSequentialGroup()
                .addGroup(panelTicketLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelTicketLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1))
                    .addGroup(panelTicketLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(btnFinalizarTicket)))
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
                    .addComponent(txtCambio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(52, 52, 52)
                .addComponent(jLabel15)
                .addGap(11, 11, 11)
                .addComponent(txtNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel17)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        add(panelTicket, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 660, 1020));
    }// </editor-fold>//GEN-END:initComponents

    private void btnFinalizarTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinalizarTicketActionPerformed
        // TODO add your handling code here:
        // Ruta fija a Descargas
        String formaPago = comboFormaPago.getSelectedItem().toString();

    if (formaPago.equalsIgnoreCase("TARJETA")) {
        // Abrir formulario de tarjeta
        FormularioTarjeta form = new FormularioTarjeta(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            this
        );
        form.setVisible(true);
    } else {
        // Pago en efectivo: generar ticket directamente
        generarTicketPDF();
    }
        
    }//GEN-LAST:event_btnFinalizarTicketActionPerformed


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
