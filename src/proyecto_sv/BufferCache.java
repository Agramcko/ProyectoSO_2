/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto_sv;

import java.io.Serializable;

/**
 * Representa el buffer (caché) de almacenamiento intermedio.
 * Utiliza nuestra ListaEnlazada para una política de reemplazo FIFO.
 */
public class BufferCache implements Serializable {

    // ¡Usamos nuestra ListaEnlazada de Fase 1!
    private ListaEnlazada<Bloque> cache;
    private int tamanoMaximo;
    
    // Usamos 'transient' para que no intente guardarse en el archivo .ser
    private transient ILogger logger = null;
    
    // --- VARIABLES DE ESTADÍSTICA ---
    private transient int contadorHits = 0;
    private transient int contadorMisses = 0;
    
    /**
     * Constructor
     * @param tamano La cantidad de bloques que el buffer puede retener.
     */
    public BufferCache(int tamano) {
        this.tamanoMaximo = tamano;
        this.cache = new ListaEnlazada<>();
        
        log("BUFFER: ⚙️ Buffer de " + tamano + " bloques inicializado.");
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
                // --- ¡EMOJI AÑADIDO! ---
                log("BUFFER: ⚡ ¡Cache HIT! para bloque " + idBloque);
                
                // --- ¡CONTADOR AÑADIDO! ---
                this.contadorHits++;
                
                return actual.getDato(); // ¡Cache Hit!
            }
            actual = actual.getSiguiente();
        }

        // --- ¡EMOJI AÑADIDO! ---
        log("BUFFER: 🐢 Cache MISS para bloque " + idBloque);
        
        // --- ¡CONTADOR AÑADIDO! ---
        this.contadorMisses++;
        
        return null; // ¡Cache Miss!
    }

    /**
     * Escribe un bloque en el caché.
     * Maneja la política de evicción (expulsión) FIFO si el caché está lleno.
     * @param bloque El bloque a escribir.
     */
    public void escribir(Bloque bloque) {
        
        // 1. Revisa si el bloque ya existe (para evitar duplicados)
        if (leer(bloque.getId()) != null) {
            return;
        }

        // 2. Revisa si el caché está lleno
        if (cache.getTamano() >= tamanoMaximo) {
            // Está lleno. Elimina el primer bloque (el más antiguo)
            Bloque bloqueEliminado = cache.eliminarDelInicio();
            
            if (bloqueEliminado != null) {
                log("BUFFER: ♻️ Cache lleno. Eliminado bloque " + bloqueEliminado.getId() + " (FIFO).");
            }
        }

        // 3. Añade el nuevo bloque al final de la lista
        cache.agregarAlFinal(bloque);
        log("BUFFER: 💾 Escribiendo bloque " + bloque.getId() + " en cache.");
    }

    /**
     * Devuelve la lista interna para la GUI.
     */
    public ListaEnlazada<Bloque> getCacheInterno() {
        return this.cache;
    }
    
    /**
     * Devuelve cuántos bloques están actualmente en el caché.
     */
    public int getTamanoActual() {
        return this.cache.getTamano();
    }
    
    /**
     * Devuelve el tamaño máximo del caché.
     */
    public int getTamanoMaximo() {
        return this.tamanoMaximo;
    }
    
    /**
     * Vacía el buffer por completo.
     * Útil para pruebas de planificador de disco.
     */
    public void limpiar() {
        this.cache = new ListaEnlazada<>();
        
        // Reiniciamos contadores al limpiar (opcional, pero recomendado)
        this.contadorHits = 0;
        this.contadorMisses = 0;
        
        log("BUFFER: 🧹 ¡Caché limpiado manualmente!");
    }

    /**
     * Busca un bloque en el caché por su ID y lo elimina.
     * Esto es crucial para cuando un archivo se borra del disco.
     */
    public void invalidar(int idBloque) {
        // 1. Busca el bloque en la lista
        NodoLista<Bloque> actual = cache.getInicio();
        Bloque bloqueAInvalidar = null;
        
        while (actual != null) {
            if (actual.getDato().getId() == idBloque) {
                bloqueAInvalidar = actual.getDato();
                break;
            }
            actual = actual.getSiguiente();
        }

        // 2. Si lo encuentra, lo elimina
        if (bloqueAInvalidar != null) {
            cache.eliminar(bloqueAInvalidar);
            log("BUFFER: 👻 Bloque " + idBloque + " invalidado (eliminado) del caché.");
        }
    }
    
    // --- MÉTODOS GETTER PARA ESTADÍSTICAS ---
    public int getContadorHits() {
        return this.contadorHits;
    }

    public int getContadorMisses() {
        return this.contadorMisses;
    }

    // --- MÉTODOS DEL LOGGER ---
    
    public void setLogger(ILogger logger) {
        this.logger = logger;
    }
    
    private void log(String mensaje) {
        if (this.logger != null) {
            this.logger.log(mensaje);
        } else {
            System.out.println(mensaje);
        }
    }
}
