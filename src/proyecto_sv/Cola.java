/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto_sv;

/**
 * @author Alessandro Gramcko
 * @author massimo Gramcko
 */


public class Cola<T> {
    private ListaEnlazada<T> listaInterna;

    public Cola() {
        this.listaInterna = new ListaEnlazada<>();
    }

    // Encolar (enqueue) - Añadir al final
    public void encolar(T dato) {
        this.listaInterna.agregarAlFinal(dato);
    }

    // Desencolar (dequeue) - Sacar del inicio
    public T desencolar() {
        return this.listaInterna.eliminarDelInicio();
    }

    // Ver el primer elemento (peek)
    public T verFrente() {
        return this.listaInterna.verInicio();
    }

    public boolean estaVacia() {
        return this.listaInterna.estaVacia();
    }

    public int getTamano() {
        return this.listaInterna.getTamano();
    }
}