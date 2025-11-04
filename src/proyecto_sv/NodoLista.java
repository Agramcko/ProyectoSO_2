/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto_sv;

/**
 * @author Alessandro Gramcko
 * @author massimo Gramcko
 */


// Representa un nodo individual de la lista enlazada
public class NodoLista<T> {
    private T dato;
    private NodoLista<T> siguiente;

    // Constructor
    public NodoLista(T dato) {
        this.dato = dato;
        this.siguiente = null; // Por defecto, no apunta a nada
    }

    // Getters y Setters
    public T getDato() {
        return dato;
    }

    public void setDato(T dato) {
        this.dato = dato;
    }

    public NodoLista<T> getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoLista<T> siguiente) {
        this.siguiente = siguiente;
    }
}
