package com.petscare.org.modelo.objetos;

public class Noticia {
    private String titulo;
    private String fuente;

    //ESTE METODO SOLICITA EL PARAMETRO DEFINIDO, QUE EN ESTE CASO ES "NOMBRE"
    public Noticia(String titulo, String fecha){
        this.titulo = titulo;
        this.fuente = fecha;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getFuenteNoticia() {
        return fuente;
    }

    public void setFuenteNoticia(String Fuente) {
        this.fuente = fuente;
    }
}
