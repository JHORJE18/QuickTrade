package com.jhorje18.quicktrade.model;

/**
 * Created by wiijl on 04/01/2018.
 */

public class Categoria {

    //Variables
    String nombre;
    String descripcion;

    public Categoria() {
        //Constructor obligatorio
    }

    public Categoria(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
