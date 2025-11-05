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
    public BufferCache getBufferCache() {
    return this.buffer;
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
/**
 * ¡NUEVO MÉTODO!
 * Renombra un archivo o directorio en el directorio actual.
 * Es una operación instantánea (metadatos).
 * Devuelve 'true' si fue exitoso.
 */
public boolean renombrarNodo(String nombreViejo, String nombreNuevo) {

    // 1. Validar que el nombre nuevo no esté vacío
    if (nombreNuevo == null || nombreNuevo.trim().isEmpty()) {
        System.err.println("Renombrar: El nombre nuevo no puede estar vacío.");
        return false;
    }

    // 2. Validar que el nombre nuevo NO exista ya
    if (directorioActual.buscarHijo(nombreNuevo) != null) {
        System.err.println("Renombrar: El nombre '" + nombreNuevo + "' ya existe.");
        return false;
    }

    // 3. Buscar el nodo (archivo/directorio) viejo
    NodoArbol nodoARenombrar = directorioActual.buscarHijo(nombreViejo);

    if (nodoARenombrar == null) {
        System.err.println("Renombrar: No se encontró '" + nombreViejo + "'.");
        return false;
    }

    // 4. ¡El cambio! (Usando el método del Paso 3)
    nodoARenombrar.setNombre(nombreNuevo);

    System.out.println("Renombrar: '" + nombreViejo + "' ahora es '" + nombreNuevo + "'.");
    return true;
}
/**
 * ¡NUEVO MÉTODO!
 * Crea un nuevo directorio dentro del directorio actual.
 * Es una operación instantánea (metadatos).
 * Devuelve 'true' si fue exitoso.
 */
public boolean crearDirectorio(String nombre) {

    // 1. Validar que el nombre no esté vacío
    if (nombre == null || nombre.trim().isEmpty()) {
        System.err.println("Crear Dir: El nombre no puede estar vacío.");
        return false;
    }

    // 2. Validar que el nombre NO exista ya
    if (directorioActual.buscarHijo(nombre) != null) {
        System.err.println("Crear Dir: El nombre '" + nombre + "' ya existe.");
        return false;
    }

    // 3. Crear el objeto Directorio
    Directorio nuevoDir = new Directorio(nombre);

    // 4. Añadirlo al árbol (al directorio actual)
    directorioActual.agregarHijo(nuevoDir);

    System.out.println("Directorio creado: " + nombre);
    return true;
}
/**
 * ¡NUEVO MÉTODO PÚBLICO!
 * Punto de entrada para eliminar un directorio desde la GUI.
 * Llama al ayudante recursivo y luego elimina el directorio en sí.
 */
public boolean eliminarDirectorio(String nombre) {
    // 1. Buscar el directorio en el directorio actual
    NodoArbol nodo = directorioActual.buscarHijo(nombre);

    if (nodo == null) {
        System.err.println("Eliminar Dir: No se encontró '" + nombre + "'.");
        return false;
    }

    if (!(nodo instanceof Directorio)) {
        System.err.println("Eliminar Dir: '" + nombre + "' no es un directorio.");
        return false;
    }

    Directorio dirAEliminar = (Directorio) nodo;

    // 2. Llamar al ayudante recursivo para vaciarlo
    System.out.println("Eliminación recursiva iniciada para: " + nombre);
    eliminarDirectorioRecursivo(dirAEliminar);

    // 3. Una vez vacío, eliminar el directorio del árbol
    directorioActual.eliminarHijo(dirAEliminar);

    System.out.println("Directorio eliminado exitosamente: " + nombre);
    return true;
}

/**
 * ¡NUEVO MÉTODO PRIVADO (AYUDANTE RECURSIVO)!
 * Vacía un directorio de todo su contenido.
 */
private void eliminarDirectorioRecursivo(Directorio dir) {

    // Usamos un 'while' porque la lista de hijos se modificará en cada iteración
    while (!dir.getHijos().estaVacia()) {

        // Obtenemos el primer hijo
        NodoArbol hijo = dir.getHijos().getInicio().getDato();

        if (hijo instanceof Archivo) {
            // Si es un archivo, usamos nuestro método 'eliminarArchivo'
            // (que ya maneja la liberación de bloques y el buffer)
            System.out.println("Borrando archivo: " + hijo.getNombre());
            // ¡Importante! Usamos la versión de 'eliminarArchivo' que
            // recibe el directorio padre donde buscar.
            eliminarArchivo(hijo.getNombre(), dir);

        } else if (hijo instanceof Directorio) {
            // Si es un sub-directorio, llamamos a esta misma función
            System.out.println("Entrando a subdirectorio: " + hijo.getNombre());
            eliminarDirectorioRecursivo((Directorio) hijo);

            // Cuando la recursión termina, el sub-directorio está vacío
            // y ahora podemos eliminarlo de la lista de 'dir'
            dir.eliminarHijo(hijo);
        }
    }
    // Cuando el 'while' termina, 'dir' está vacío.
}
}
