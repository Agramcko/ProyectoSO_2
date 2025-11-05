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
public class SistemaArchivos implements Serializable {
    private Directorio raiz;
    private DiscoSD disco;
    // (Opcional) Guardar el directorio actual en el que está el usuario
    private Directorio directorioActual;
    private BufferCache buffer;

    public SistemaArchivos(int tamanoDisco) {
        this.disco = new DiscoSD(tamanoDisco);
        // Creamos el directorio raíz
        this.raiz = new Directorio("root");
        this.directorioActual = this.raiz; 
        this.buffer = new BufferCache(8);
    }
    
    public Directorio getRaiz() {
        return raiz;
    }
    
    public DiscoSD getDisco() {
        return disco;
    }
    
    public Directorio getDirectorioActual() {
        return this.directorioActual;
    }
    
    // (Opcional pero recomendado) Un setter para cambiar de directorio
    public void setDirectorioActual(Directorio dir) {
        this.directorioActual = dir;
    }

    // --- Lógica CRUD (Backend) ---
    // [cite: 36]
    
    /**
     * Crea un archivo en el directorio actual.
     * Esta es la operación que debe ser gestionada por un PROCESO [cite: 15, 16]
     */
    /**
 * MODIFICADO: Crea un archivo en el directorio especificado.
 * ¡Ahora devuelve el idPrimerBloque!
 */
public int crearArchivo(String nombre, int tamanoEnBloques, Directorio directorioPadre) {
    if (tamanoEnBloques > disco.getNumBloquesLibres()) {
        System.err.println("No hay espacio suficiente.");
        return -1; // Falla
    }

    // (Opcional: verificar si el nombre ya existe en directorioPadre)
    if (directorioPadre.buscarHijo(nombre) != null) {
        System.err.println("Error: El nombre de archivo ya existe.");
        return -1; // Falla
    }

    Archivo nuevoArchivo = new Archivo(nombre, tamanoEnBloques);
    int primerBloque = disco.asignarBloques(nuevoArchivo, tamanoEnBloques);

    if (primerBloque == -1) {
        System.err.println("No se pudieron asignar los bloques.");
        return -1; // Falla
    }

    nuevoArchivo.setIdPrimerBloque(primerBloque);
    directorioPadre.agregarHijo(nuevoArchivo); // ¡Usamos el padre!

    System.out.println("Archivo creado: " + nombre + ", inicia en bloque " + primerBloque);
    return primerBloque; // ¡Éxito!
}

    /**
     * Elimina un archivo del directorio actual.
     */
    /**
     * Elimina un archivo del directorio actual.
     * ¡VERSIÓN ACTUALIZADA!
     */
    /**
 * MODIFICADO: Elimina un archivo del directorio especificado.
 * ¡Ahora devuelve el idPrimerBloque!
 */
/**
     * MODIFICADO: Elimina un archivo del directorio especificado.
     * ¡Ahora también invalida el BufferCache!
     */
    public int eliminarArchivo(String nombre, Directorio directorioPadre) {
        NodoArbol nodo = directorioPadre.buscarHijo(nombre);
        
        if (nodo == null || !(nodo instanceof Archivo)) {
            System.err.println("Error: Archivo '" + nombre + "' no encontrado.");
            return -1; // Falla
        }
        
        Archivo archivoAEliminar = (Archivo) nodo;
        int idPrimerBloque = archivoAEliminar.getIdPrimerBloque();
        
        // --- ¡NUEVA LÓGICA DE BORRADO! ---
        // Reemplazamos la llamada simple a 'disco.liberarBloques()'
        
        int idBloqueActual = idPrimerBloque;
        System.out.println("ELIMINANDO: Liberando e invalidando bloques...");
        
        while (idBloqueActual != -1) {
            Bloque bloque = disco.getBloque(idBloqueActual);
            
            // Seguridad para evitar bucles infinitos si algo sale mal
            if (bloque == null || !bloque.estaOcupado()) {
                break; 
            }
            
            int idSiguiente = bloque.getPunteroSiguiente();
            
            // 1. Liberar en el disco
            bloque.liberar();
            disco.notificarBloqueLiberado(); // ¡Avisamos al disco!
            
            // 2. ¡Invalidar en el buffer!
            buffer.invalidar(idBloqueActual);
            
            idBloqueActual = idSiguiente;
        }
        // --- FIN LÓGICA NUEVA ---

        // 3. Eliminar del árbol de directorios
        directorioPadre.eliminarHijo(archivoAEliminar);
        
        System.out.println("Archivo eliminado: " + nombre);
        return idPrimerBloque; // Éxito
    }
    
    // (Implementar crearDirectorio, eliminarDirectorio[cite: 47], etc.)
/**
 * Simula la lectura de un archivo.
 * Devuelve el idPrimerBloque.
 */
/**
 * MODIFICADO: Simula la lectura de un archivo.
 * ¡Ahora utiliza el BufferCache!
 */
public int leerArchivo(String nombre, Directorio directorioPadre) {
    NodoArbol nodo = directorioPadre.buscarHijo(nombre);
    
    if (nodo == null || !(nodo instanceof Archivo)) {
        System.err.println("Error: Archivo '" + nombre + "' no encontrado para leer.");
        return -1; // Falla
    }
    
    Archivo archivo = (Archivo) nodo;
    int idBloqueActual = archivo.getIdPrimerBloque();
    
    System.out.print("Simulación de LECTURA: Leyendo bloques -> ");
    
    // Recorremos la cadena de bloques
    while (idBloqueActual != -1) {
        
        // --- ¡¡LÓGICA DEL BUFFER AÑADIDA!! ---
        
        // 1. Intentamos leer del buffer
        Bloque bloqueLeido = buffer.leer(idBloqueActual);
        
        // 2. Si es null (Cache MISS), lo leemos del disco
        if (bloqueLeido == null) {
            bloqueLeido = disco.getBloque(idBloqueActual);
            
            // 3. Y lo escribimos en el buffer para la próxima vez
            if (bloqueLeido != null) {
                buffer.escribir(bloqueLeido);
            }
        }
        // Si no era null, fue un ¡Cache HIT! y nos ahorramos el disco.
        
        // --- FIN LÓGICA DEL BUFFER ---
        
        if (bloqueLeido == null) break; // Seguridad
        
        System.out.print(bloqueLeido.getId() + " -> ");
        idBloqueActual = bloqueLeido.getPunteroSiguiente();
    }
    System.out.println("FIN");
    
    return archivo.getIdPrimerBloque(); // Éxito
}
}
