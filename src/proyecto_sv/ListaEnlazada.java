/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto_sv;

/**
 * @author Alessandro Gramcko
 * @author massimo Gramcko
 */


public class ListaEnlazada<T> {
    private NodoLista<T> inicio;
    private NodoLista<T> fin;
    private int tamano;

    public ListaEnlazada() {
        this.inicio = null;
        this.fin = null;
        this.tamano = 0;
    }

    public boolean estaVacia() {
        return this.inicio == null;
    }

    public int getTamano() {
        return this.tamano;
    }

    // Añade un elemento al final de la lista
    public void agregarAlFinal(T dato) {
        NodoLista<T> nuevoNodo = new NodoLista<>(dato);
        if (estaVacia()) {
            this.inicio = nuevoNodo;
            this.fin = nuevoNodo;
        } else {
            this.fin.setSiguiente(nuevoNodo);
            this.fin = nuevoNodo;
        }
        this.tamano++;
    }

    // Elimina el primer elemento de la lista (útil para Colas)
    public T eliminarDelInicio() {
        if (estaVacia()) {
            return null; // O lanzar una excepción
        }
        
        T dato = this.inicio.getDato();
        this.inicio = this.inicio.getSiguiente();
        
        if (this.inicio == null) {
            this.fin = null; // La lista quedó vacía
        }
        
        this.tamano--;
        return dato;
    }

    // (Opcional pero recomendado) Método para obtener el primer elemento sin eliminarlo
    public T verInicio() {
        if (estaVacia()) {
            return null;
        }
        return this.inicio.getDato();
    }
}