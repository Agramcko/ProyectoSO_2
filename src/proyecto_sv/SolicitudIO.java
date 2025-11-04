/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto_sv;

/**
 * @author Alessandro Gramcko
 * @author massimo Gramcko
 */

public class SolicitudIO {
    private TipoOperacion tipo;
    private String nombreArchivo;
    private int tamanoEnBloques; // Usado solo para CREAR_ARCHIVO
    private Directorio directorioPadre; // Dónde se ejecutará

    // Constructor para CREAR
    public SolicitudIO(TipoOperacion tipo, String nombre, int tamano, Directorio padre) {
        this.tipo = tipo;
        this.nombreArchivo = nombre;
        this.tamanoEnBloques = tamano;
        this.directorioPadre = padre;
    }

    // Constructor para ELIMINAR (u otros)
    public SolicitudIO(TipoOperacion tipo, String nombre, Directorio padre) {
        this.tipo = tipo;
        this.nombreArchivo = nombre;
        this.tamanoEnBloques = 0; // No aplica
        this.directorioPadre = padre;
    }

    // --- Getters ---
    public TipoOperacion getTipo() { return tipo; }
    public String getNombreArchivo() { return nombreArchivo; }
    public int getTamanoEnBloques() { return tamanoEnBloques; }
    public Directorio getDirectorioPadre() { return directorioPadre; }
}