/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto_sv;

/**
 *
 * @author massi
 */

public class Archivo extends NodoArbol {
    private int tamanoEnBloques;
    // Guardaremos la referencia al primer bloque del disco (lo añadiremos en Fase 2)
    // private Bloque primerBloque; 

    public Archivo(String nombre, int tamanoEnBloques) {
        super(nombre);
        this.tamanoEnBloques = tamanoEnBloques;
        // this.primerBloque = null;
    }

    @Override
    public int getTamanoEnBloques() {
        return this.tamanoEnBloques;
    }

    // Setter para cuando asignemos los bloques
    public void setTamanoEnBloques(int tamano) {
        this.tamanoEnBloques = tamano;
    }
}
