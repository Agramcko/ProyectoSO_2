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
    private int contadorArchivosAleatorios = 0;

    // --- VARIABLES DE ESTADÍSTICA ---
    private transient int opsExitosas = 0;
    private transient int opsFallidas = 0;

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
            // --- ¡LOG MEJORADO! ---
            log("SIMULADOR: Directorio actual cambiado a: " + dir.getNombre());
        }
    }
    public BufferCache getBufferCache() {
        return this.buffer;
    }

    // --- Lógica CRUD (Backend) ---
    
    /**
     * MODIFICADO: Crea un archivo en el directorio especificado.
     * ¡Ahora devuelve el idPrimerBloque!
     * ¡Y AHORA USA EL LOGGER CON EMOJIS Y ESTADÍSTICAS!
     */
    public int crearArchivo(String nombre, int tamanoEnBloques, Directorio directorioPadre) {
        
        if (tamanoEnBloques > disco.getNumBloquesLibres()) {
            log("PLANIFICADOR: ⛔ ¡DISCO LLENO! No hay " + tamanoEnBloques + " bloques libres para '" + nombre + "'.");
            this.opsFallidas++; // <--- ESTADÍSTICA FALLO
            return -1; // Falla
        }

        if (directorioPadre.buscarHijo(nombre) != null) {
            log("PLANIFICADOR: ❌ Error. El nombre '" + nombre + "' ya existe.");
            this.opsFallidas++; // <--- ESTADÍSTICA FALLO
            return -1; // Falla
        }

        Archivo nuevoArchivo = new Archivo(nombre, tamanoEnBloques);
        int primerBloque = disco.asignarBloques(nuevoArchivo, tamanoEnBloques);

        if (primerBloque == -1) {
            log("PLANIFICADOR: ⛔ ¡DISCO LLENO! No se pudieron asignar los " + tamanoEnBloques + " bloques (fragmentación o error).");
            this.opsFallidas++; // <--- ESTADÍSTICA FALLO
            return -1; // Falla
        }

        nuevoArchivo.setIdPrimerBloque(primerBloque);
        directorioPadre.agregarHijo(nuevoArchivo); // ¡Usamos el padre!

        log("PLANIFICADOR: ✅ Archivo creado: " + nombre + ", inicia en bloque " + primerBloque);
        this.opsExitosas++; // <--- ESTADÍSTICA ÉXITO
        return primerBloque; // ¡Éxito!
    }
    
    /**
     * MODIFICADO: Elimina un archivo del directorio especificado.
     * ¡Ahora también invalida el BufferCache y usa EMOJIS y ESTADÍSTICAS!
     */
    public int eliminarArchivo(String nombre, Directorio directorioPadre) {
        NodoArbol nodo = directorioPadre.buscarHijo(nombre);
        
        if (nodo == null || !(nodo instanceof Archivo)) {
            log("PLANIFICADOR: ❌ Error: Archivo '" + nombre + "' no encontrado.");
            this.opsFallidas++; // <--- ESTADÍSTICA FALLO
            return -1; // Falla
        }
        
        Archivo archivoAEliminar = (Archivo) nodo;
        int idPrimerBloque = archivoAEliminar.getIdPrimerBloque();
        
        log("PLANIFICADOR: 🗑️ Liberando e invalidando bloques para " + nombre + "...");
        
        int idBloqueActual = idPrimerBloque;
        while (idBloqueActual != -1) {
            Bloque bloque = disco.getBloque(idBloqueActual);
            
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
        
        log("PLANIFICADOR: 🗑️ Archivo eliminado: " + nombre);
        this.opsExitosas++; // <--- ESTADÍSTICA ÉXITO
        return idPrimerBloque; // Éxito
    }
        
    /**
     * MODIFICADO: Simula la lectura de un archivo.
     * ¡Ahora utiliza el BufferCache, loggea en una línea y cuenta ESTADÍSTICAS!
     */
    public int leerArchivo(String nombre, Directorio directorioPadre) {
        NodoArbol nodo = directorioPadre.buscarHijo(nombre);
        
        if (nodo == null || !(nodo instanceof Archivo)) {
            log("PLANIFICADOR: ❌ Error: Archivo '" + nombre + "' no encontrado para leer.");
            this.opsFallidas++; // <--- ESTADÍSTICA FALLO
            return -1; // Falla
        }
        
        Archivo archivo = (Archivo) nodo;
        int idBloqueActual = archivo.getIdPrimerBloque();
        
        // --- ¡LÓGICA DE LOG MEJORADA! ---
        StringBuilder sb = new StringBuilder();
        
        // Recorremos la cadena de bloques
        while (idBloqueActual != -1) {
            
            // --- ¡LÓGICA DEL BUFFER (Como estaba antes) ---
            Bloque bloqueLeido = buffer.leer(idBloqueActual);
            
            if (bloqueLeido == null) {
                bloqueLeido = disco.getBloque(idBloqueActual);
                
                if (bloqueLeido != null) {
                    buffer.escribir(bloqueLeido);
                }
            }
            // --- FIN LÓGICA DEL BUFFER ---
            
            if (bloqueLeido == null) break; // Seguridad
            
            // Añadimos el bloque al string
            sb.append("[").append(bloqueLeido.getId()).append("] -> ");
            idBloqueActual = bloqueLeido.getPunteroSiguiente();
        }
        sb.append("FIN");
        
        log("PLANIFICADOR: 📖 Simulación de LECTURA: " + sb.toString());
        
        this.opsExitosas++; // <--- ESTADÍSTICA ÉXITO
        return archivo.getIdPrimerBloque(); // Éxito
    }
    
    /**
     * Crea un nuevo directorio dentro del directorio actual.
     */
    public boolean crearDirectorio(String nombre) {

        if (nombre == null || nombre.trim().isEmpty()) {
            log("PLANIFICADOR: ❌ Error Crear Dir: El nombre no puede estar vacío.");
            return false;
        }

        if (directorioActual.buscarHijo(nombre) != null) {
            log("PLANIFICADOR: ❌ Error Crear Dir: El nombre '" + nombre + "' ya existe.");
            return false;
        }

        Directorio nuevoDir = new Directorio(nombre);
        directorioActual.agregarHijo(nuevoDir);

        log("PLANIFICADOR: ✅ Directorio creado: " + nombre);
        return true;
    }
    
    /**
     * Punto de entrada para eliminar un directorio desde la GUI.
     */
    public boolean eliminarDirectorio(String nombre) {
        NodoArbol nodo = directorioActual.buscarHijo(nombre);

        if (nodo == null) {
            log("PLANIFICADOR: ❌ Error Eliminar Dir: No se encontró '" + nombre + "'.");
            return false;
        }

        if (!(nodo instanceof Directorio)) {
            log("PLANIFICADOR: ❌ Error Eliminar Dir: '" + nombre + "' no es un directorio.");
            return false;
        }

        Directorio dirAEliminar = (Directorio) nodo;

        log("PLANIFICADOR: 🗑️ Eliminación recursiva iniciada para: " + nombre);
        eliminarDirectorioRecursivo(dirAEliminar);

        directorioActual.eliminarHijo(dirAEliminar);

        log("PLANIFICADOR: 🗑️ Directorio eliminado exitosamente: " + nombre);
        return true;
    }

    /**
     * Ayudante recursivo para vaciar un directorio.
     */
    private void eliminarDirectorioRecursivo(Directorio dir) {

        while (!dir.getHijos().estaVacia()) {
            NodoArbol hijo = dir.getHijos().getInicio().getDato();

            if (hijo instanceof Archivo) {
                log("PLANIFICADOR: 🗑️ Borrando archivo interno: " + hijo.getNombre());
                eliminarArchivo(hijo.getNombre(), dir);

            } else if (hijo instanceof Directorio) {
                log("PLANIFICADOR: 🗑️ Entrando a subdirectorio: " + hijo.getNombre());
                eliminarDirectorioRecursivo((Directorio) hijo);
                dir.eliminarHijo(hijo);
            }
        }
    }
    
    /**
     * Punto de entrada para crear el reporte.
     */
    public boolean generarReporteDeEstado() {
        StringBuilder sb = new StringBuilder();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        sb.append("--- REPORTE DEL SISTEMA DE ARCHIVOS ---\n");
        sb.append("Generado: ").append(dtf.format(LocalDateTime.now())).append("\n");
        sb.append("---------------------------------------\n\n");
        sb.append("ESTRUCTURA DEL DIRECTORIO:\n");

        generarReporteRecursivo(this.raiz, sb, "");

        sb.append("\n\n---------------------------------------\n");
        sb.append("ESTADO DEL DISCO (SD):\n");
        sb.append("Total de Bloques: ").append(disco.getNumBloquesTotal()).append("\n");
        sb.append("Bloques Libres: ").append(disco.getNumBloquesLibres()).append("\n");
        sb.append("Bloques Ocupados: ").append(disco.getNumBloquesTotal() - disco.getNumBloquesLibres()).append("\n");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("reporte_disco.txt"))) {
            writer.write(sb.toString());
            log("SIMULADOR: 📈 ¡Reporte 'reporte_disco.txt' generado exitosamente!");
            return true;
        } catch (IOException e) {
            log("SIMULADOR: ❌ Error al escribir el reporte: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Ayudante recursivo para el reporte (¡con emojis!).
     */
    private void generarReporteRecursivo(NodoArbol nodo, StringBuilder sb, String indentacion) {
        if (nodo == null) return;
        sb.append(indentacion); 

        if (nodo instanceof Directorio) {
            sb.append("📁 ").append(nodo.getNombre()).append("/\n"); // Emoji de Directorio

            Directorio dir = (Directorio) nodo;
            NodoLista<NodoArbol> hijoActual = dir.getHijos().getInicio();
            while (hijoActual != null) {
                generarReporteRecursivo(hijoActual.getDato(), sb, indentacion + "  ");
                hijoActual = hijoActual.getSiguiente();
            }

        } else if (nodo instanceof Archivo) {
            Archivo archivo = (Archivo) nodo;
            sb.append("📄 ").append(archivo.getNombre()); // Emoji de Archivo
            sb.append(" (Tamaño: ").append(archivo.getTamanoEnBloques()).append(" bloques)\n");

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
    
    /**
     * ¡NUEVO MÉTODO AYUDANTE!
     * Ayudante de log. Si tenemos un logger GUI, lo usa.
     */
    private void log(String mensaje) {
        if (this.logger != null) {
            this.logger.log(mensaje); // ¡Lo envía a la GUI!
        } else {
            System.out.println(mensaje); // Fallback
        }
    }
    
    /**
     * Devuelve el valor actual del contador de archivos
     */
    public int getContadorArchivosAleatorios() {
        return this.contadorArchivosAleatorios;
    }

    /**
     * Incrementa el contador de archivos
     */
    public void incrementarContadorArchivosAleatorios() {
        this.contadorArchivosAleatorios++;
    }
    

    /**
     * Elimina el 'directorioActual'.
     */
    public boolean eliminarDirectorioActual() {
        
        Directorio dirAEliminar = this.directorioActual;

        if (dirAEliminar == this.raiz) {
            log("PLANIFICADOR: ❌ Error. No se puede eliminar el directorio raíz.");
            return false;
        }
        
        Directorio padre = dirAEliminar.getPadre();
        if (padre == null) {
             log("PLANIFICADOR: ❌ Error. El nodo no tiene padre (Huérfano).");
             return false;
        }

        log("PLANIFICADOR: 🗑️ Eliminación recursiva iniciada para: " + dirAEliminar.getNombre());
        eliminarDirectorioRecursivo(dirAEliminar);

        padre.eliminarHijo(dirAEliminar);
        this.directorioActual = padre;

        log("PLANIFICADOR: 🗑️ Directorio eliminado exitosamente: " + dirAEliminar.getNombre());
        return true;
    }
    
    /**
     * Punto de entrada para re-conectar los punteros 'padre'
     */
    public void reconectarPadres() {
        reconectarPadresRecursivo(this.raiz);
    }

    /**
     * Ayudante recursivo para re-conectar los punteros 'padre'.
     */
    private void reconectarPadresRecursivo(Directorio padre) {
        if (padre == null || padre.getHijos() == null) {
            return;
        }

        NodoLista<NodoArbol> hijoActual = padre.getHijos().getInicio();
        
        while (hijoActual != null) {
            
            NodoArbol nodoHijo = hijoActual.getDato();
            
            // 1. ¡LA RE-CONEXIÓN!
            nodoHijo.setPadre(padre);
            
            // 2. Si este hijo también es un directorio, 
            //    hacemos la llamada recursiva para sus propios hijos.
            if (nodoHijo instanceof Directorio) {
                reconectarPadresRecursivo((Directorio) nodoHijo);
            }
            
            hijoActual = hijoActual.getSiguiente();
        }
    }
    
    /**
     * ¡NUEVO MÉTODO MEJORADO!
     * Renombra un NodoArbol específico (archivo o directorio)
     * que se le pasa como parámetro.
     */
    public boolean renombrarNodo(NodoArbol nodoARenombrar, String nombreNuevo) {

        // 1. No podemos renombrar la raíz
        if (nodoARenombrar == this.raiz) {
            log("PLANIFICADOR: ❌ Error. No se puede renombrar el directorio raíz.");
            this.opsFallidas++; // <--- ESTADÍSTICA FALLO
            return false;
        }

        // 2. Validar nombre nuevo
        if (nombreNuevo == null || nombreNuevo.trim().isEmpty()) {
            log("PLANIFICADOR: ❌ Error. El nombre nuevo no puede estar vacío.");
            this.opsFallidas++; // <--- ESTADÍSTICA FALLO
            return false;
        }

        // 3. Obtener el padre
        Directorio padre = nodoARenombrar.getPadre();
        if (padre == null) {
             log("PLANIFICADOR: ❌ Error. El nodo no tiene padre (Huérfano).");
             this.opsFallidas++; // <--- ESTADÍSTICA FALLO
             return false;
        }

        // 4. Validar que el nombre nuevo NO exista ya en el padre
        if (padre.buscarHijo(nombreNuevo) != null) {
            log("PLANIFICADOR: ❌ Error. El nombre '" + nombreNuevo + "' ya existe.");
            this.opsFallidas++; // <--- ESTADÍSTICA FALLO
            return false;
        }

        // 5. ¡El cambio!
        log("PLANIFICADOR: ✏️ Renombrado '" + nodoARenombrar.getNombre() + "' a '" + nombreNuevo + "'.");
        nodoARenombrar.setNombre(nombreNuevo);
        
        this.opsExitosas++; // <--- ESTADÍSTICA ÉXITO
        return true;
    }
    
    // --- MÉTODOS GETTERS PARA ESTADÍSTICAS ---
    public int getOpsExitosas() {
        return this.opsExitosas;
    }

    public int getOpsFallidas() {
        return this.opsFallidas;
    }
}
