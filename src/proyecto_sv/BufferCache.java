/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto_sv;

/**
 *
 * @author massi
 */

import java.io.Serializable;

/**
 * Representa el buffer (caché) de almacenamiento intermedio.
 * Utiliza nuestra ListaEnlazada para una política de reemplazo FIFO.
 */
public class BufferCache implements Serializable {

    // ¡Usamos nuestra ListaEnlazada de Fase 1!
    private ListaEnlazada<Bloque> cache;
    private int tamanoMaximo;
    
    /**
     * Constructor
     * @param tamano La cantidad de bloques que el buffer puede retener.
     */
    public BufferCache(int tamano) {
        this.tamanoMaximo = tamano;
        this.cache = new ListaEnlazada<>();
        System.out.println("Buffer de " + tamano + " bloques inicializado.");
    }
    
    /**
     * Intenta leer un bloque desde el caché.
     * @param idBloque El ID del bloque a buscar.
     * @return El Bloque si se encuentra (Cache Hit), o null si no (Cache Miss).
     */
    public Bloque leer(int idBloque) {
        // Recorremos la lista enlazada buscando el bloque
        NodoLista<Bloque> actual = cache.getInicio();
        while (actual != null) {
            if (actual.getDato().getId() == idBloque) {
                // ¡¡Cache HIT!! Lo encontramos.
                System.out.println("BUFFER: Cache HIT para bloque " + idBloque);
                return actual.getDato();
            }
            actual = actual.getSiguiente();
        }
        
        // ¡¡Cache MISS!! No estaba.
        System.out.println("BUFFER: Cache MISS para bloque " + idBloque);
        return null;
    }
    
    /**
     * Escribe un bloque en el caché (lo añade).
     * Si el caché está lleno, saca el más antiguo (FIFO).
     * @param bloque El bloque leído del disco, para guardarlo en caché.
     */
    public void escribir(Bloque bloque) {
        if (bloque == null) return;
        
        System.out.println("BUFFER: Escribiendo bloque " + bloque.getId() + " en cache.");
        
        // Política de reemplazo FIFO:
        // Si la lista está llena (o supera el tamaño)...
        if (cache.getTamano() >= this.tamanoMaximo) {
            // ...sacamos el primero que entró (el del inicio).
            Bloque antiguo = cache.eliminarDelInicio();
            System.out.println("BUFFER: Cache lleno. Eliminado bloque " + antiguo.getId() + " (FIFO).");
        }
        
        // Añadimos el nuevo bloque al final.
        cache.agregarAlFinal(bloque);
    }
    
    /**
     * Invalida (elimina) un bloque del caché.
     * Esto es crucial para cuando ELIMINAMOS un archivo.
     * @param idBloque El ID del bloque a eliminar del caché.
     */
    public void invalidar(int idBloque) {
        Bloque bloqueAInvalidar = null;
        
        // No podemos eliminar por ID, debemos pasar el objeto Bloque.
        // Así que primero lo buscamos.
        NodoLista<Bloque> actual = cache.getInicio();
        while (actual != null) {
            if (actual.getDato().getId() == idBloque) {
                bloqueAInvalidar = actual.getDato();
                break;
            }
            actual = actual.getSiguiente();
        }
        
        // Si lo encontramos, usamos el método 'eliminar(dato)' de ListaEnlazada
        if (bloqueAInvalidar != null) {
            boolean exito = cache.eliminar(bloqueAInvalidar);
            if (exito) {
                System.out.println("BUFFER: Invalidado bloque " + idBloque + " del cache.");
            }
        }
    }
}
