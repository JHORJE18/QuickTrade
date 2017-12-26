package com.jhorje18.quicktrade.model;

/**
 * Created by JHORJE on 26/12/17.
 */

public class Producto {

    //Variables
    String usuario;
    String nombre;
    String descripcion;
    String categoria;
    String precio;

    public Producto(String usuario, String nombre, String descripcion, String categoria, String precio) {
        this.usuario = usuario;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.precio = precio;
    }

    public Producto (){
        //Constructor obligatorio FireBase
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }
}
