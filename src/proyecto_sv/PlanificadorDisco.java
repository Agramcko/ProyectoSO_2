/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto_sv;

/**
 * @author Alessandro Gramcko
 * @author massimo Gramcko
 */

public class PlanificadorDisco {
    
    // Referencia al "Backend" (¡no crea uno nuevo, usa el existente!)
    private SistemaArchivos sistemaArchivos;
    
    private int posicionCabezal = 0; // Para SSTF, SCAN, etc.

    public PlanificadorDisco(SistemaArchivos sa) {
        this.sistemaArchivos = sa; // Recibe el backend del Simulador
    }

    /**
     * Método privado que ejecuta la solicitud LLAMANDO al backend
     */
    private void ejecutarSolicitud(SolicitudIO solicitud) {
        boolean exito = false;
        
        // ¡¡AQUÍ OCURRE LA MAGIA!!
        // El planificador llama al backend (tu SistemaArchivos)
        
        if (solicitud.getTipo() == TipoOperacion.CREAR_ARCHIVO) {
            
            System.out.println("PLANIFICADOR: Ejecutando CREAR " + solicitud.getNombreArchivo());
            
            // Llama a tu método de Fase 2
            exito = sistemaArchivos.crearArchivo(
                solicitud.getNombreArchivo(), 
                solicitud.getTamanoEnBloques()
                // Falta pasarle el directorio padre... 
                // Necesitaríamos modificar crearArchivo para que acepte un Directorio
            );
        }
        else if (solicitud.getTipo() == TipoOperacion.ELIMINAR_ARCHIVO) {
            
            System.out.println("PLANIFICADOR: Ejecutando ELIMINAR " + solicitud.getNombreArchivo());
            
            // Llama a tu método de Fase 2
            exito = sistemaArchivos.eliminarArchivo(solicitud.getNombreArchivo());
        }
        
        if (exito) {
            System.out.println("PLANIFICADOR: Operación completada con éxito.");
        } else {
            System.out.println("PLANIFICADOR: Operación falló.");
        }
    }

    // --- Políticas de Planificación ---

    // 1. Política FIFO (La más simple)
    // 1. Política FIFO (¡Modificada para que devuelva la solicitud!)
    public SolicitudIO ejecutarFIFO(Cola<SolicitudIO> colaIO) {
        if (colaIO.estaVacia()) {
            System.out.println("PLANIFICADOR: No hay solicitudes en la cola de E/S.");
            return null; // <-- NUEVO: Devuelve null si no hizo nada
        }
        
        // Saca la primera solicitud
        SolicitudIO solicitud = colaIO.desencolar();
        
        // La ejecuta
        ejecutarSolicitud(solicitud);
        
        return solicitud; // <-- NUEVO: Devuelve la solicitud que procesó
    }

    // 2. Política SSTF (Shortest Seek Time First)
    // ... (Implementación más compleja, la dejamos para después) ...
    
    // 3. Política SCAN
    // ...
    
    // 4. Política C-SCAN
    // ...
}
