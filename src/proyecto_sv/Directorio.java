/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto_sv;

/**
 * @author Alessandro Gramcko
 * @author massimo Gramcko
 */

import java.io.Serializable;
public class Directorio extends NodoArbol implements Serializable {
    
    // ¡Usamos nuestra propia ListaEnlazada!
    private ListaEnlazada<NodoArbol> hijos;

    public Directorio(String nombre) {
        super(nombre);
        this.hijos = new ListaEnlazada<>();
    }
    
    /**
     * NUEVO MÉTODO: Busca un hijo (Archivo o Directorio) por su nombre.
     * Devuelve el NodoArbol si lo encuentra, o null si no.
     */
    public NodoArbol buscarHijo(String nombre) {
        if (hijos.estaVacia()) {
            return null;
        }

        NodoLista<NodoArbol> actual = hijos.getInicio();
        while (actual != null) {
            if (actual.getDato().getNombre().equals(nombre)) {
                return actual.getDato(); // ¡Encontrado!
            }
            actual = actual.getSiguiente();
        }
        return null; // No encontrado
    }

    /**
     * NUEVO MÉTODO: Elimina un hijo de la lista de hijos.
     * Usa el método 'eliminar(dato)' que creamos en ListaEnlazada.
     */
    public boolean eliminarHijo(NodoArbol hijo) {
        if (hijo == null) return false;
        return hijos.eliminar(hijo);
    }

    // Métodos para manejar hijos
    public void agregarHijo(NodoArbol hijo) {
        this.hijos.agregarAlFinal(hijo);
        hijo.setPadre(this); // Establecemos el padre del hijo
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
