package com.jhorje18.quicktrade.model;

/**
 * Created by JHORJE on 15/12/17.
 */

public class Usuario {

    //Variables
    String USUARIO;
    String NOMBRE;
    String APEDILLOS;
    String CORREO;
    String DIRECCION;

    public Usuario(String USUARIO, String NOMBRE, String APEDILLOS, String CORREO, String DIRECCION) {
        this.USUARIO = USUARIO;
        this.NOMBRE = NOMBRE;
        this.APEDILLOS = APEDILLOS;
        this.CORREO = CORREO;
        this.DIRECCION = DIRECCION;
    }

    public String getUSUARIO() {
        return USUARIO;
    }

    public void setUSUARIO(String USUARIO) {
        this.USUARIO = USUARIO;
    }

    public String getNOMBRE() {
        return NOMBRE;
    }

    public void setNOMBRE(String NOMBRE) {
        this.NOMBRE = NOMBRE;
    }

    public String getAPEDILLOS() {
        return APEDILLOS;
    }

    public void setAPEDILLOS(String APEDILLOS) {
        this.APEDILLOS = APEDILLOS;
    }

    public String getCORREO() {
        return CORREO;
    }

    public void setCORREO(String CORREO) {
        this.CORREO = CORREO;
    }

    public String getDIRECCION() {
        return DIRECCION;
    }

    public void setDIRECCION(String DIRECCION) {
        this.DIRECCION = DIRECCION;
    }

    public String toString(){
        return USUARIO + "{" +
                "CORREO='" + CORREO + "\'," +
                "NOMBRE='" + NOMBRE + "\'," +
                "APEDILLOS='" + APEDILLOS + "\'" +
                "DIRECCION='" + DIRECCION + "\'" +
                "}";
    }
}
