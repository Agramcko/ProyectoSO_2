/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto_sv;

/**
 * @author Alessandro Gramcko
 * @author massimo Gramcko
 */

public abstract class NodoArbol {
    protected String nombre;
    protected Directorio padre; // Referencia al directorio padre

    public NodoArbol(String nombre) {
        this.nombre = nombre;
        this.padre = null; // La raíz no tendrá padre
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Directorio getPadre() {
        return padre;
    }

    public void setPadre(Directorio padre) {
        this.padre = padre;
    }
    
    // Método abstracto para que las subclases definan su tamaño
    public abstract int getTamanoEnBloques();
}
