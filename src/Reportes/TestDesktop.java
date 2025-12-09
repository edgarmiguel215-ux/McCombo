/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Reportes;

import java.awt.Desktop;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author salga
 */
public class TestDesktop {
        
        public static void main(String[] args) {
        String hash = "$2a$12$7iauTk4kbGjOfaE.Coe8q.C1r1oLWdS5nWcUC9aK.2vgw1jBYsMwy";

        System.out.println("1234: " + BCrypt.checkpw("1234", hash));
        System.out.println("12345: " + BCrypt.checkpw("12345", hash));
        System.out.println("123456: " + BCrypt.checkpw("123456", hash));
        System.out.println("hola123: " + BCrypt.checkpw("hola123", hash));
    }
}


