
package Reportes;

import Modelo.Conexion;
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

public class ExcelVentasPorUsuario {
   
    public static void reporte() {
        Workbook book = new XSSFWorkbook();
        Sheet sheet = book.createSheet("Ventas por Usuario");

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
            } catch (IOException e) {
                System.out.println("Logo no encontrado, continuando sin logo...");
            }

            // Estilo título
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
            celdaTitulo.setCellValue("Historial de Ventas por Usuario");
            sheet.addMergedRegion(new CellRangeAddress(1, 2, 1, 4));

            // Cabecera
            String[] cabecera = new String[]{
                "Usuario", "Fecha", "Cantidad Tickets", "Total Ventas"
            };


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

            // Conexión y consulta
            Conexion con = new Conexion();
            Connection conn = con.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT u.nombre AS usuario, DATE(t.fecha) AS fecha, " +
                "COUNT(t.id_ticket) AS cantidad_tickets, " +
                "SUM(t.total) AS total_ventas " +
                "FROM tickets t " +
                "JOIN usuarios u ON t.id_usuario = u.id " +
                "GROUP BY u.nombre, DATE(t.fecha) " +
                "ORDER BY u.nombre, fecha"
            );

            ResultSet rs = ps.executeQuery();

            int numFilaDatos = 5;
            CellStyle datosEstilo = book.createCellStyle();
            datosEstilo.setBorderBottom(BorderStyle.THIN);
            datosEstilo.setBorderLeft(BorderStyle.THIN);
            datosEstilo.setBorderRight(BorderStyle.THIN);
            datosEstilo.setBorderTop(BorderStyle.THIN);
            datosEstilo.setVerticalAlignment(VerticalAlignment.TOP);
            datosEstilo.setWrapText(true);

            while (rs.next()) {
                Row filaDatos = sheet.createRow(numFilaDatos);
                filaDatos.createCell(0).setCellValue(rs.getString("usuario"));
                filaDatos.createCell(1).setCellValue(rs.getString("fecha")); // fecha como texto
                filaDatos.createCell(2).setCellValue(rs.getInt("cantidad_tickets"));
                filaDatos.createCell(3).setCellValue(rs.getDouble("total_ventas"));

                for (int i = 0; i < cabecera.length; i++) {
                    filaDatos.getCell(i).setCellStyle(datosEstilo);
                }

                numFilaDatos++;
}


            for (int i = 0; i < cabecera.length; i++) {
                sheet.autoSizeColumn(i);
            }
            sheet.setZoom(120);

            // Guardar archivo
            String fileName = "VentasPorUsuario";
            String home = System.getProperty("user.home");
            File file = new File(home + "/Downloads/" + fileName + ".xlsx");

            int counter = 1;
            while (file.exists()) {
                file = new File(home + "/Downloads/" + fileName + "_" + counter + ".xlsx");
                counter++;
            }

            FileOutputStream fileOut = new FileOutputStream(file);
            book.write(fileOut);
            fileOut.close();

            Desktop.getDesktop().open(file);
            JOptionPane.showMessageDialog(null, "Reporte de Ventas por Usuario Generado Exitosamente");

        } catch (IOException | SQLException ex) {
            Logger.getLogger(ExcelVentasPorUsuario.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Error al generar reporte: " + ex.getMessage());
        } finally {
            try {
                book.close();
            } catch (IOException ex) {
                Logger.getLogger(ExcelVentasPorUsuario.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
