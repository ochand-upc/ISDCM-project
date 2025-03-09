package com.isdcm.minetflix.model;

public class Video {

    private int id;
    private String titulo;
    private String autor;
    private String fecha;         // Usado como VARCHAR(10) en la BD
    private String duracion;      // También VARCHAR(10) en la BD
    private int reproducciones;
    private String descripcion;
    private String formato;
    private String rutavideo;

    // Constructor vacío (requerido para instanciar sin parámetros)
    public Video() {
    }

    // Constructor con todos los campos (excepto 'id' si lo autogenera la BD)
    public Video(String titulo, String autor, String fecha, String duracion,
                 int reproducciones, String descripcion, String formato, String rutavideo) {
        this.titulo = titulo;
        this.autor = autor;
        this.fecha = fecha;
        this.duracion = duracion;
        this.reproducciones = reproducciones;
        this.descripcion = descripcion;
        this.formato = formato;
        this.rutavideo = rutavideo;
    }

    // Getters y Setters para cada atributo

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public int getReproducciones() {
        return reproducciones;
    }

    public void setReproducciones(int reproducciones) {
        this.reproducciones = reproducciones;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getRutavideo() {
        return rutavideo;
    }

    public void setRutavideo(String rutavideo) {
        this.rutavideo = rutavideo;
    }
}
