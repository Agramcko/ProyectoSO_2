/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto_sv;

/**
 * @author Alessandro Gramcko
 * @author massimo Gramcko
 */

// Modificación de la clase Archivo (Fase 1)

public class Archivo extends NodoArbol {
    private int tamanoEnBloques;
    
    // ¡NUEVO! Guardamos el ID del primer bloque [cite: 62]
    private int idPrimerBloque; 

    public Archivo(String nombre, int tamanoEnBloques) {
        super(nombre);
        this.tamanoEnBloques = tamanoEnBloques;
        this.idPrimerBloque = -1; // Aún no asignado en el disco
    }

    @Override
    public int getTamanoEnBloques() {
        return this.tamanoEnBloques;
    }

    // --- Getters y Setters nuevos ---
    
    public int getIdPrimerBloque() {
        return idPrimerBloque;
    }

    public void setIdPrimerBloque(int idPrimerBloque) {
        this.idPrimerBloque = idPrimerBloque;
    }
    
    public void setTamanoEnBloques(int tamano) {
        this.tamanoEnBloques = tamano;
    }
}
