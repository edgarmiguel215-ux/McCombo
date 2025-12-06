
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

public class ExcelCompras {
    
    public static void reporte() {

        Workbook book = new XSSFWorkbook();
        Sheet sheet = book.createSheet("Compras");

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
            celdaTitulo.setCellValue("Reporte de Compras");

            sheet.addMergedRegion(new CellRangeAddress(1, 2, 1, 13));

            // Cabecera
            String[] cabecera = new String[]{
                "ID", "Artículo", "Cantidad", "Unidad", "Precio Unitario", "Total",
                "Datos Pago", "ID Proveedor", "Nombre Proveedor", "Comprobante",
                "Método Pago", "Fecha", "Estatus"
            };

            // Estilo para encabezados
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

            // Consulta con JOIN para traer nombre del proveedor
            ps = conn.prepareStatement(
            "SELECT c.idCompras, c.nombre_articulo, c.cantidad, c.unidad, c.precio_unitario, c.total, " +
            "c.datos_pago, c.idProveedor, p.nombre AS nombre_proveedor, c.comprobante, c.metodo_pago, c.fecha, c.estado " +
            "FROM compras c " +
            "JOIN proveedor p ON c.idProveedor = p.id " +   // alias p definido
            "ORDER BY c.idCompras"
        );

            rs = ps.executeQuery();

            while (rs.next()) {
                Row filaDatos = sheet.createRow(numFilaDatos);

                filaDatos.createCell(0).setCellValue(rs.getInt("idCompras"));
                filaDatos.createCell(1).setCellValue(rs.getString("nombre_articulo"));
                filaDatos.createCell(2).setCellValue(rs.getInt("cantidad"));
                filaDatos.createCell(3).setCellValue(rs.getString("unidad"));
                filaDatos.createCell(4).setCellValue(rs.getDouble("precio_unitario"));
                filaDatos.createCell(5).setCellValue(rs.getDouble("total"));
                filaDatos.createCell(6).setCellValue(rs.getString("datos_pago"));
                filaDatos.createCell(7).setCellValue(rs.getString("idProveedor"));
                filaDatos.createCell(8).setCellValue(rs.getString("nombre_proveedor"));
                filaDatos.createCell(9).setCellValue(rs.getString("comprobante"));
                filaDatos.createCell(10).setCellValue(rs.getString("metodo_pago"));
                filaDatos.createCell(11).setCellValue(rs.getString("fecha"));
                filaDatos.createCell(12).setCellValue(rs.getString("estado"));

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
            String fileName = "Compras";
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
            JOptionPane.showMessageDialog(null, "Reporte de Compras Generado Exitosamente");

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExcelCompras.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Error: Archivo no encontrado - " + ex.getMessage());
        } catch (IOException | SQLException ex) {
            Logger.getLogger(ExcelCompras.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Error al generar reporte: " + ex.getMessage());
        } finally {
            try {
                book.close();
            } catch (IOException ex) {
                Logger.getLogger(ExcelCompras.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}