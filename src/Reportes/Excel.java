package Reportes;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import Modelo.Conexion;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Excel {
    public static void reporte() {

        Workbook book = new XSSFWorkbook();
        Sheet sheet = book.createSheet("Proveedores");

        try {
            // Logo (opcional)
            try {
                InputStream is = new FileInputStream("src/img/logo.png");
                byte[] bytes = IOUtils.toByteArray(is);
                int imgIndex = book.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
                is.close();

                CreationHelper help = book.getCreationHelper();
                Drawing draw = sheet.createDrawingPatriarch();

                ClientAnchor anchor = help.createClientAnchor();
                anchor.setCol1(0);
                anchor.setRow1(1);
                Picture pict = draw.createPicture(anchor, imgIndex);
                pict.resize(1, 3);
            } catch (FileNotFoundException e) {
                System.out.println("Logo no encontrado, continuando sin logo...");
            }

            // Estilo para el título
            CellStyle tituloEstilo = book.createCellStyle();
            tituloEstilo.setAlignment(HorizontalAlignment.CENTER);
            tituloEstilo.setVerticalAlignment(VerticalAlignment.CENTER);
            Font fuenteTitulo = book.createFont();
            fuenteTitulo.setFontName("Arial");
            fuenteTitulo.setBold(true);
            fuenteTitulo.setFontHeightInPoints((short) 14);
            tituloEstilo.setFont(fuenteTitulo);

            Row filaTitulo = sheet.createRow(1);
            Cell celdaTitulo = filaTitulo.createCell(1);
            celdaTitulo.setCellStyle(tituloEstilo);
            celdaTitulo.setCellValue("Reporte de Proveedores");

            sheet.addMergedRegion(new CellRangeAddress(1, 2, 1, 6));

            // Cabecera para Proveedores
            String[] cabecera = new String[]{
                "No. Documento", 
                "Tipo Documento", 
                "Nombre/Razón Social", 
                "Teléfono", 
                "Dirección", 
                "Razón"
            };

            // Estilo para los encabezados
            CellStyle headerStyle = book.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            Font font = book.createFont();
            font.setFontName("Arial");
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            font.setFontHeightInPoints((short) 12);
            headerStyle.setFont(font);

            Row filaEncabezados = sheet.createRow(4);

            for (int i = 0; i < cabecera.length; i++) {
                Cell celdaEncabezado = filaEncabezados.createCell(i);
                celdaEncabezado.setCellStyle(headerStyle);
                celdaEncabezado.setCellValue(cabecera[i]);
            }

            // Conexión a la base de datos
            Conexion con = new Conexion();
            PreparedStatement ps;
            ResultSet rs;
            Connection conn = con.getConnection();

            int numFilaDatos = 5;

            // Estilo para los datos
            CellStyle datosEstilo = book.createCellStyle();
            datosEstilo.setBorderBottom(BorderStyle.THIN);
            datosEstilo.setBorderLeft(BorderStyle.THIN);
            datosEstilo.setBorderRight(BorderStyle.THIN);
            datosEstilo.setBorderTop(BorderStyle.THIN);
            datosEstilo.setVerticalAlignment(VerticalAlignment.TOP);
            datosEstilo.setWrapText(true);

            // CONSULTA CORREGIDA - tabla se llama "proveedor" (singular)
            ps = conn.prepareStatement("SELECT numero_documento, tipo, nombre, telefono, direccion, razon FROM proveedor ORDER BY nombre");
            rs = ps.executeQuery();

            while (rs.next()) {
                Row filaDatos = sheet.createRow(numFilaDatos);

                // No. Documento
                Cell celdaNumeroDoc = filaDatos.createCell(0);
                celdaNumeroDoc.setCellStyle(datosEstilo);
                celdaNumeroDoc.setCellValue(rs.getString("numero_documento"));

                // Tipo Documento
                Cell celdaTipo = filaDatos.createCell(1);
                celdaTipo.setCellStyle(datosEstilo);
                celdaTipo.setCellValue(rs.getString("tipo"));

                // Nombre/Razón Social
                Cell celdaNombre = filaDatos.createCell(2);
                celdaNombre.setCellStyle(datosEstilo);
                celdaNombre.setCellValue(rs.getString("nombre"));

                // Teléfono
                Cell celdaTelefono = filaDatos.createCell(3);
                celdaTelefono.setCellStyle(datosEstilo);
                celdaTelefono.setCellValue(rs.getString("telefono"));

                // Dirección
                Cell celdaDireccion = filaDatos.createCell(4);
                celdaDireccion.setCellStyle(datosEstilo);
                celdaDireccion.setCellValue(rs.getString("direccion"));

                // Razón
                Cell celdaRazon = filaDatos.createCell(5);
                celdaRazon.setCellStyle(datosEstilo);
                celdaRazon.setCellValue(rs.getString("razon"));

                numFilaDatos++;
            }

            for (int i = 0; i < cabecera.length; i++) {
                sheet.autoSizeColumn(i);
            }
            sheet.setZoom(120);


            // Guardar archivo
            String fileName = "proveedores";
            String home = System.getProperty("user.home");
            File file = new File(home + "/Downloads/" + fileName + ".xlsx");
            
            // Si el archivo existe, agregar número
            int counter = 1;
            while (file.exists()) {
                file = new File(home + "/Downloads/" + fileName + "_" + counter + ".xlsx");
                counter++;
            }
            
            FileOutputStream fileOut = new FileOutputStream(file);
            book.write(fileOut);
            fileOut.close();
            
            // Abrir el archivo
            Desktop.getDesktop().open(file);
            JOptionPane.showMessageDialog(null, "Reporte de Proveedores Generado Exitosamente");

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Excel.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Error: Archivo no encontrado - " + ex.getMessage());
        } catch (IOException | SQLException ex) {
            Logger.getLogger(Excel.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Error al generar reporte: " + ex.getMessage());
        } finally {
            try {
                book.close();
            } catch (IOException ex) {
                Logger.getLogger(Excel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}