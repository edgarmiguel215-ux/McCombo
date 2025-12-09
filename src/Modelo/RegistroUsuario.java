
package Modelo;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.awt.image.BufferedImage;

public class RegistroUsuario {
  
    public void registrar(String correo) {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        GoogleAuthenticatorKey key = gAuth.createCredentials();

        String secret = key.getKey(); // Guardar en BD
        String qrUrl = GoogleAuthenticatorQRGenerator.getOtpAuthURL(
                "SistemaVenta", correo, key);

        System.out.println("Secret: " + secret);
        System.out.println("Escanea este QR en Google Authenticator: " + qrUrl);

        // Aquí guardas 'secret' en la columna otp_secret del usuario
    }
    
    public BufferedImage generarQR(String url) throws WriterException {
    QRCodeWriter qrWriter = new QRCodeWriter();
    BitMatrix bitMatrix = qrWriter.encode(url, BarcodeFormat.QR_CODE, 200, 200);
    return MatrixToImageWriter.toBufferedImage(bitMatrix);
}
}
