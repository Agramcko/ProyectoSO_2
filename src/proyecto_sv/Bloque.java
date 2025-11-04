/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto_sv;

/**
 * @author Alessandro Gramcko
 * @author massimo Gramcko
 */

public class Bloque {
    // Para saber qué bloque es (ej: 0, 1, 2...)
    private int id; 
    
    // -1 significa que es el último bloque del archivo
    // Otro número (ej: 5) indica que el siguiente bloque es el #5
    private int punteroSiguiente; 
    
    // Para saber si está libre u ocupado
    private boolean ocupado;
    
    // (Opcional, pero útil para la GUI) A qué archivo pertenece
    private Archivo archivoPropietario; 

    public Bloque(int id) {
        this.id = id;
        this.punteroSiguiente = -1; // Por defecto no apunta a nada
        this.ocupado = false; // Por defecto está libre
        this.archivoPropietario = null;
    }

    // --- Getters y Setters ---
    
    public int getId() {
        return id;
    }
    
    public boolean estaOcupado() {
        return ocupado;
    }

    public void ocupar(Archivo propietario) {
        this.ocupado = true;
        this.archivoPropietario = propietario;
    }
    
    // Libera el bloque y resetea sus punteros
    public void liberar() {
        this.ocupado = false;
        this.punteroSiguiente = -1;
        this.archivoPropietario = null;
    }
    
    public int getPunteroSiguiente() {
        return punteroSiguiente;
    }

    public void setPunteroSiguiente(int punteroSiguiente) {
        this.punteroSiguiente = punteroSiguiente;
    }
    
    public Archivo getArchivoPropietario() {
        return archivoPropietario;
    }
}
