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
public class ListaEnlazada<T> implements Serializable {
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
    
    /**
     * NUEVO MÉTODO 1:
     * Devuelve el primer NODO (no el dato).
     * Esto permite que otras clases puedan recorrer la lista manualmente.
     */
    public NodoLista<T> getInicio() {
        return this.inicio;
    }

    /**
     * NUEVO MÉTODO 2:
     * Busca un dato específico en la lista y lo elimina.
     * Devuelve 'true' si lo encontró y eliminó, 'false' si no.
     */
    public boolean eliminar(T dato) {
        // Si la lista está vacía, no hay nada que eliminar.
        if (estaVacia()) {
            return false;
        }

        // --- Caso 1: El elemento a eliminar es el PRIMERO (el 'inicio') ---
        if (this.inicio.getDato().equals(dato)) {
            // Simplemente usamos el método que ya teníamos
            eliminarDelInicio();
            return true;
        }

        // --- Caso 2: El elemento está en el MEDIO o al FINAL ---
        
        // Empezamos a buscar desde el segundo elemento
        NodoLista<T> anterior = this.inicio;
        NodoLista<T> actual = this.inicio.getSiguiente();

        // Recorremos la lista
        while (actual != null) {
            
            // Comparamos el dato actual con el que buscamos
            if (actual.getDato().equals(dato)) {
                
                // ¡Lo encontramos!
                // Hacemos que el nodo 'anterior' "salte" al 'actual'
                // y apunte directamente al 'siguiente' de actual.
                anterior.setSiguiente(actual.getSiguiente());
                
                // Si el nodo que eliminamos era el ÚLTIMO (el 'fin')
                if (actual == this.fin) {
                    this.fin = anterior; // El nuevo 'fin' es el 'anterior'
                }
                
                this.tamano--; // Reducimos el tamaño
                return true; // ¡Éxito!
            }
            
            // Si no era este, avanzamos en la lista
            anterior = actual;
            actual = actual.getSiguiente();
        }

        // Si salimos del 'while', es porque recorrimos todo y no lo encontramos
        return false;
    }
}