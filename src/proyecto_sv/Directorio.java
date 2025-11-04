/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto_sv;

/**
 * @author Alessandro Gramcko
 * @author massimo Gramcko
 */

public class Directorio extends NodoArbol {
    
    // ¡Usamos nuestra propia ListaEnlazada!
    private ListaEnlazada<NodoArbol> hijos;

    public Directorio(String nombre) {
        super(nombre);
        this.hijos = new ListaEnlazada<>();
    }

    // Métodos para manejar hijos
    public void agregarHijo(NodoArbol hijo) {
        hijo.setPadre(this); // Establecemos el padre del hijo
        this.hijos.agregarAlFinal(hijo);
    }

    // (Necesitaremos más métodos aquí, como buscarHijo, eliminarHijo, etc.)
    // ...

    public ListaEnlazada<NodoArbol> getHijos() {
        return this.hijos;
    }

    @Override
    public int getTamanoEnBloques() {
        // Un directorio en sí mismo no ocupa bloques de datos en esta simulación
        // (Aunque en sistemas reales sí ocupa espacio para guardar la lista de hijos)
        return 0; 
    }
}
