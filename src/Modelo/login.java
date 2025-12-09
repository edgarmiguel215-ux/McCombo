
package Modelo;


public class login {
    private int id;
    private String nombre;
    private String correo;
    private String pass;
    private String Rol;
    private String estado;
    private String otpSecret;
    private boolean otpEnabled;

    public login() {
    }

//    public login(int id, String nombre, String correo, String pass, String Rol) {
//        this.id = id;
//        this.nombre = nombre;
//        this.correo = correo;
//        this.pass = pass;
//        this.Rol = Rol;
//    }

    public login(int id, String nombre, String correo, String pass, String Rol, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.pass = pass;
        this.Rol = Rol;
        this.estado = estado;
    }

    public boolean isOtpEnabled() {
        return otpEnabled;
    }

    public void setOtpEnabled(boolean otpEnabled) {
        this.otpEnabled = otpEnabled;
    }

    
    public String getOtpSecret() {
        return otpSecret;
    }

    public void setOtpSecret(String otpSecret) {
        this.otpSecret = otpSecret;
    }
    
    

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getRol() {
        return Rol;
    }

    public void setRol(String Rol) {
        this.Rol = Rol;
    }

    
    
}
