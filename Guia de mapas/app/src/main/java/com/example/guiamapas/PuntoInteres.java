package com.example.guiamapas;

public class PuntoInteres {
    private String nombre;
    private String categoria;
    private double latitud;
    private double longitud;
    private String descripcion;
    private boolean esMuseo;
    private int imagenId;
    private String horario; // Nuevo atributo

    public PuntoInteres(String nombre, String categoria, double latitud, double longitud, String descripcion, boolean esMuseo, int imagenId, String horario) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.latitud = latitud;
        this.longitud = longitud;
        this.descripcion = descripcion;
        this.esMuseo = esMuseo;
        this.imagenId = imagenId;
        this.horario = horario;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getImagenId() {
        return imagenId;
    }

    public void setImagenId(int imagenId) {
        this.imagenId = imagenId;
    }
}
