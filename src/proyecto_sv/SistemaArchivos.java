/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto_sv;

/**
 * @author Alessandro Gramcko
 * @author massimo Gramcko
 */
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.time.LocalDateTime; // (Para la fecha del reporte)
import java.time.format.DateTimeFormatter;
import java.io.Serializable;

public class SistemaArchivos implements Serializable {
    private Directorio raiz;
    private DiscoSD disco;
    // (Opcional) Guardar el directorio actual en el que está el usuario
    private Directorio directorioActual;
    private BufferCache buffer;
    private transient ILogger logger = null;

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
    if (dir != null) {
        this.directorioActual = dir;
        System.out.println("Directorio actual cambiado a: " + dir.getNombre());
    }
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
// Pega esto dentro de la clase SistemaArchivos

/**
 * ¡NUEVO MÉTODO PÚBLICO!
 * Punto de entrada para crear el reporte.
 * Crea un StringBuilder y llama al ayudante recursivo.
 */
public boolean generarReporteDeEstado() {
    // 1. Usamos un StringBuilder para construir el reporte en memoria
    StringBuilder sb = new StringBuilder();

    // 2. Encabezado del reporte
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    sb.append("--- REPORTE DEL SISTEMA DE ARCHIVOS ---\n");
    sb.append("Generado: ").append(dtf.format(LocalDateTime.now())).append("\n");
    sb.append("---------------------------------------\n\n");
    sb.append("ESTRUCTURA DEL DIRECTORIO:\n");

    // 3. Llamada al ayudante recursivo (empezando desde la raíz)
    generarReporteRecursivo(this.raiz, sb, "");

    // 4. Información del Disco
    sb.append("\n\n---------------------------------------\n");
    sb.append("ESTADO DEL DISCO (SD):\n");
    sb.append("Total de Bloques: ").append(disco.getNumBloquesTotal()).append("\n");
    sb.append("Bloques Libres: ").append(disco.getNumBloquesLibres()).append("\n");
    sb.append("Bloques Ocupados: ").append(disco.getNumBloquesTotal() - disco.getNumBloquesLibres()).append("\n");

    // 5. Escribir el StringBuilder a un archivo .txt
    try (BufferedWriter writer = new BufferedWriter(new FileWriter("reporte_disco.txt"))) {
        writer.write(sb.toString());
        System.out.println("¡Reporte 'reporte_disco.txt' generado exitosamente!");
        return true;
    } catch (IOException e) {
        System.err.println("Error al escribir el reporte: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

/**
 * ¡NUEVO MÉTODO PRIVADO (AYUDANTE RECURSIVO)!
 * Recorre el árbol y añade la información al StringBuilder.
 */
private void generarReporteRecursivo(NodoArbol nodo, StringBuilder sb, String indentacion) {
    if (nodo == null) return;

    sb.append(indentacion); // Aplica la sangría

    if (nodo instanceof Directorio) {
        // Si es un Directorio
        sb.append("📁 ").append(nodo.getNombre()).append("/\n");

        // Llamada recursiva para cada hijo
        Directorio dir = (Directorio) nodo;
        NodoLista<NodoArbol> hijoActual = dir.getHijos().getInicio();
        while (hijoActual != null) {
            generarReporteRecursivo(hijoActual.getDato(), sb, indentacion + "  ");
            hijoActual = hijoActual.getSiguiente();
        }

    } else if (nodo instanceof Archivo) {
        // Si es un Archivo
        Archivo archivo = (Archivo) nodo;
        sb.append("📄 ").append(archivo.getNombre());
        sb.append(" (Tamaño: ").append(archivo.getTamanoEnBloques()).append(" bloques)\n");

        // Detalle de la cadena de bloques
        sb.append(indentacion).append("     └ (Bloques: ");
        int idBloqueActual = archivo.getIdPrimerBloque();
        while (idBloqueActual != -1) {
            sb.append("[").append(idBloqueActual).append("] -> ");
            Bloque bloque = disco.getBloque(idBloqueActual);
            if (bloque == null) break;
            idBloqueActual = bloque.getPunteroSiguiente();
        }
        sb.append("FIN)\n");
    }
}
/**
 * Recibe el logger desde el Simulador
 * y lo pasa al BufferCache.
 */
public void setLogger(ILogger logger) {
    this.logger = logger;

    if (this.buffer != null) {
        this.buffer.setLogger(logger);
    }
}
}
