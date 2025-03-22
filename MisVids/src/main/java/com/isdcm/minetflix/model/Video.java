package com.isdcm.minetflix.model;

public class Video {
    private int id;
    private String titulo;
    private String autor;
    private String fecha;
    private double duracion;
    private int reproducciones;
    private String descripcion;
    private String mimeType;
    private String rutaVideo;
    private String tipoFuente;
    private Long tamano;// Puede ser NULL para YouTube

    public Video(String titulo, String autor, String fecha, double duracion, int reproducciones, 
                 String descripcion, String mimeType, String rutaVideo, String tipoFuente, Long tamano) {
        this.titulo = titulo;
        this.autor = autor;
        this.fecha = fecha;
        this.duracion = duracion;
        this.reproducciones = reproducciones;
        this.descripcion = descripcion;
        this.mimeType = mimeType;
        this.rutaVideo = rutaVideo;
        this.tipoFuente = tipoFuente;
        this.tamano = tamano;
    }

    public Video() { }

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

    public double getDuracion() {
        return duracion;
    }

    public void setDuracion(double duracion) {
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

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getRutaVideo() {
        return rutaVideo;
    }

    public void setRutaVideo(String rutaVideo) {
        this.rutaVideo = rutaVideo;
    }

    public String getTipoFuente() {
        return tipoFuente;
    }

    public void setTipoFuente(String tipoFuente) {
        this.tipoFuente = tipoFuente;
    }

    public Long getTamano() {
        return tamano;
    }

    public void setTamano(Long tamano) {
        this.tamano = tamano;
    }

    @Override
    public String toString() {
        return "Video{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", fecha=" + fecha +
                ", duracion='" + duracion + '\'' +
                ", reproducciones=" + reproducciones +
                ", descripcion='" + descripcion + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", rutaVideo='" + rutaVideo + '\'' +
                ", tipoFuente='" + tipoFuente + '\'' +
                ", tamano=" + tamano +
                '}';
    }
}