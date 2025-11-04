/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto_sv;

/**
 * @author Alessandro Gramcko
 * @author massimo Gramcko
 */

public class DiscoSD {
    // Usamos un arreglo simple para representar el disco
    private Bloque[] bloques;
    private int numBloquesTotal;
    private int numBloquesLibres;

    // Define un tamaño máximo para el disco [cite: 34, 35]
    public DiscoSD(int tamanoTotal) {
        this.numBloquesTotal = tamanoTotal;
        this.numBloquesLibres = tamanoTotal;
        this.bloques = new Bloque[tamanoTotal];
        
        // Inicializamos todos los bloques como libres
        for (int i = 0; i < tamanoTotal; i++) {
            this.bloques[i] = new Bloque(i);
        }
    }
    
    public int getNumBloquesLibres() {
        return numBloquesLibres;
    }
    
    public int getNumBloquesTotal() {
        return numBloquesTotal;
    }
    
    // Método para encontrar el próximo bloque libre (estrategia simple: el primero que encuentre)
    private int encontrarBloqueLibre() {
        for (int i = 0; i < this.numBloquesTotal; i++) {
            if (!this.bloques[i].estaOcupado()) {
                return i; // Retorna el ID del bloque libre
            }
        }
        return -1; // No hay espacio
    }

    // --- Lógica CRUD del Disco ---
    
    /**
     * Asigna 'cantidad' bloques para un archivo.
     * Retorna el ID del primer bloque asignado (o -1 si no hay espacio).
     */
    public int asignarBloques(Archivo archivo, int cantidad) {
        if (cantidad > this.numBloquesLibres) {
            System.err.println("Error: Espacio insuficiente en disco.");
            return -1; // No hay suficiente espacio
        }

        int idBloqueAnterior = -1;
        int idPrimerBloque = -1;

        for (int i = 0; i < cantidad; i++) {
            int idBloqueActual = encontrarBloqueLibre();
            if (idBloqueActual == -1) {
                // Esto no debería pasar si revisamos el espacio al inicio,
                // pero es una buena práctica de seguridad.
                liberarBloques(idPrimerBloque); // Liberar lo que ya asignamos
                return -1;
            }

            Bloque bloqueActual = this.bloques[idBloqueActual];
            bloqueActual.ocupar(archivo);

            if (idPrimerBloque == -1) {
                idPrimerBloque = idBloqueActual; // Guardamos el primer bloque
            }

            if (idBloqueAnterior != -1) {
                // Enlazamos el bloque anterior con el actual
                this.bloques[idBloqueAnterior].setPunteroSiguiente(idBloqueActual);
            }
            
            idBloqueAnterior = idBloqueActual;
            this.numBloquesLibres--;
        }

        // El último bloque asignado no apunta a nada (-1)
        if (idBloqueAnterior != -1) {
            this.bloques[idBloqueAnterior].setPunteroSiguiente(-1);
        }
        
        return idPrimerBloque;
    }

    /**
     * Libera todos los bloques de una cadena, comenzando por el idPrimerBloque.
     */
    public void liberarBloques(int idPrimerBloque) {
        int idBloqueActual = idPrimerBloque;
        
        while (idBloqueActual != -1) {
            if (idBloqueActual >= this.numBloquesTotal || idBloqueActual < 0) {
                 System.err.println("Error: Puntero de bloque inválido durante liberación.");
                 break;
            }
            
            Bloque bloqueActual = this.bloques[idBloqueActual];
            if (!bloqueActual.estaOcupado()) {
                 // El bloque ya estaba libre, rompemos para evitar ciclos infinitos
                 break; 
            }
            
            int idSiguiente = bloqueActual.getPunteroSiguiente();
            bloqueActual.liberar();
            this.numBloquesLibres++;
            
            idBloqueActual = idSiguiente;
        }
    }
    
    // Método para obtener un bloque específico (útil para la GUI y la lectura)
    public Bloque getBloque(int id) {
        if (id >= 0 && id < this.numBloquesTotal) {
            return this.bloques[id];
        }
        return null;
    }
    
    /**
     * NUEVO MÉTODO PÚBLICO:
     * Encuentra el ID del primer bloque libre.
     * El planificador (SSTF) usa esto para "estimar" dónde
     * ocurrirá una operación de CREAR.
     */
    public int getPrimerBloqueLibre() {
        for (int i = 0; i < this.numBloquesTotal; i++) {
            if (!this.bloques[i].estaOcupado()) {
                return i; // Retorna el ID del bloque libre
            }
        }
        return -1; // No hay espacio
    }
}
