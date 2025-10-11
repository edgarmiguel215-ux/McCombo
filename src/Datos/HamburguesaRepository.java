
package Datos;

import Modelo.Hamburguesa;
import java.util.List;


public class HamburguesaRepository {
    public static Hamburguesa getHamburguesa(String id) {
        if (id == null) return null;
        switch (id) {
            case "BigMac":
                return new Hamburguesa("Big Mac", 75.00,
                    List.of("pan con semillas de sésamo", "Dos hamburguesas de carne 100% vacuno", 
                            "salsa Big Mac", "lechuga picada", "cebolla picada", 
                            "pepinillos encurtidos", "queso cheddar"),
                    "Img/Big Mac.PNG");
            case "McTosino":
                return new Hamburguesa("Mc Tocino", 85.00,
                    List.of("Carne", "Queso", "Tocino", "Catsup"),
                    "Img/Mc Tosino.PNG");
            case "ClubHause":
                return new Hamburguesa("Signature Club Hause", 69.00,
                    List.of("La mejor combinación de tocino", "cebolla grillada", "queso blanco", 
                            "lechuga", "jitomate", "pan brioche", "salsa especial", 
                            "1 carne 100% de res", "pan brioche."),
                    "Img/Club Hause.PNG");
            case "GrandBaconDeluxe":
                return new Hamburguesa("Grand Bacon Deluxe", 80,
                    List.of("Pan de sésamo", "carne 100% res", "tocino crujiente", "queso cheddar",
                            "cebolla", "lechuga", "tomate y mayonesa"),
                        "img/Grand Bacon Deluxe.PNG");
            case "HDobleconQueso":
                return new Hamburguesa("Hamburguesa Doble con Queso", 100.00,
                List.of("Hamburguesa con doble carne de 50gr", "queso cheddar", "cebolla", 
                        "pepinillos", "salsa de tomate y mostaza"),
                        "img/H Doble con Queso.PNG");
            case "HamtripleconQueso":
                return new Hamburguesa("Hamburguesa Triple Con Queso", 90,
                List.of("Hamburguesa con tres carne de 50gr", "Dos queso cheddar", "cebolla", 
                        "pepinillos", "salsa de tomate y mostaza."),
                        "img/Ham triple con Queso.PNG");
            case "HamDobleGourmet":
                return new Hamburguesa("Hamburguesa Doble Gourmet", 120,
                List.of("Dos carnes 100% de res", "cebolla grillada", "queso cheddar blanco",
                        "Aderezo."),
                        "img/Ham Doble Gourmet.PNG");
            case "HamGourmet":
                return new Hamburguesa("Hamburguesa Gourmet", 100,
                List.of("carne 100% de res (1)", "cebolla grillada", "queso cheddar blanco",
                        "Aderezo"),
                "img/Ham Gourmet.PNG");
            case "HamLechugaytomate":
                return new Hamburguesa("Hamburguesa Lechuga y Tomate", 70,
                List.of("carne 100% fresa","lechuga", "tomate", "queso y mayonesa"),
                "img/Ham Lechuga y tomate.PNG");
            case "HamTripleBBQ":
                return new Hamburguesa("Hamburguesa Triple BBQ", 85,
                List.of("tres carnes 100% de res", "tocino", "cebolla grillada", "cebolla crujiente", 
                        "queso blanco", "salsa sweet BBQ"),
                "img/Ham Triple BBQ.PNG");
            case "HamTripleGourmet":
                return new Hamburguesa("Hamburguesa Triple Gourmet", 87,
                List.of("Triple y jugosa carne 100% de res", "cebolla grillada", "queso cheddar blanco",
                        "aderezo"),
                "img/Ham Triple Gourmet.PNG");
            case "HamconQueso":
                return new Hamburguesa("Hamburguesa con Queso Sencilla", 65,
                List.of("Carne 100% de res", "Queso amarillo", "Acompañada de catsup,", "mostaza,", 
                "pepinillos y cebolla"),
                "img/Ham con Queso.PNG");
                
             // Agrega más hamburguesas aquí
            default:
                System.out.println("ID no encontrado: " + id);
                return null; // Esto lo validaremos antes de mostrar
        }
    }
}
