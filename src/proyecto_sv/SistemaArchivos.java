/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto_sv;

/**
 * @author Alessandro Gramcko
 * @author massimo Gramcko
 */

public class SistemaArchivos {
    private Directorio raiz;
    private DiscoSD disco;
    // (Opcional) Guardar el directorio actual en el que está el usuario
    private Directorio directorioActual; 

    public SistemaArchivos(int tamanoDisco) {
        this.disco = new DiscoSD(tamanoDisco);
        // Creamos el directorio raíz
        this.raiz = new Directorio("root");
        this.directorioActual = this.raiz; 
    }
    
    public Directorio getRaiz() {
        return raiz;
    }
    
    public DiscoSD getDisco() {
        return disco;
    }

    // --- Lógica CRUD (Backend) ---
    // [cite: 36]
    
    /**
     * Crea un archivo en el directorio actual.
     * Esta es la operación que debe ser gestionada por un PROCESO [cite: 15, 16]
     */
    public boolean crearArchivo(String nombre, int tamanoEnBloques) {
        // 1. Verificar si hay espacio en el disco
        if (tamanoEnBloques > disco.getNumBloquesLibres()) {
            System.err.println("No hay espacio suficiente.");
            return false;
        }
        
        // (Opcional: verificar si el nombre ya existe en directorioActual)

        // 2. Crear el objeto Archivo
        Archivo nuevoArchivo = new Archivo(nombre, tamanoEnBloques);
        
        // 3. Solicitar los bloques al disco
        int primerBloque = disco.asignarBloques(nuevoArchivo, tamanoEnBloques);
        
        if (primerBloque == -1) {
            // Falló la asignación (quizás por fragmentación, aunque
            // nuestro 'encontrarBloqueLibre' simple no sufre de eso)
            System.err.println("No se pudieron asignar los bloques.");
            return false;
        }
        
        // 4. Conectar el archivo con el disco
        nuevoArchivo.setIdPrimerBloque(primerBloque);
        
        // 5. Añadir el archivo al árbol de directorios
        this.directorioActual.agregarHijo(nuevoArchivo);
        
        System.out.println("Archivo creado: " + nombre + ", inicia en bloque " + primerBloque);
        return true;
    }

    /**
     * Elimina un archivo del directorio actual.
     */
    public boolean eliminarArchivo(String nombre) {
        // 1. Buscar el archivo en el directorio actual
        // (Necesitaremos mejorar la ListaEnlazada para buscar/eliminar por nombre)
        
        // ... Lógica para buscar 'archivoAEliminar' ...
        Archivo archivoAEliminar = null; // (Temporal, esto debe buscarse)
        
        if (archivoAEliminar == null) {
            System.err.println("Archivo no encontrado.");
            return false;
        }

        // 2. Obtener su primer bloque
        int idPrimerBloque = archivoAEliminar.getIdPrimerBloque();
        
        // 3. Liberar los bloques en el disco [cite: 33, 46]
        disco.liberarBloques(idPrimerBloque);
        
        // 4. Eliminar el archivo del árbol de directorios
        // (Necesitaremos un método en Directorio para 'eliminarHijo')
        // this.directorioActual.eliminarHijo(archivoAEliminar);
        
        System.out.println("Archivo eliminado: " + nombre);
        return true;
    }
    
    // (Implementar crearDirectorio, eliminarDirectorio[cite: 47], etc.)
}
