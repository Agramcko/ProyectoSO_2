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

    public class PlanificadorDisco {
    
    // Referencia al "Backend"
    private SistemaArchivos sistemaArchivos;
    
    private int posicionCabezal = 0; // Para SSTF, SCAN, etc.
    private enum Direccion { SUBIENDO, BAJANDO }
    private Direccion direccionSCAN = Direccion.SUBIENDO; // Empezamos "subiendo"
    
    // --- ¡INICIO DE CAMBIOS DE LOGGER! ---
    // NO usamos 'transient' aquí porque PlanificadorDisco
    // se crea de cero (new) cada vez, no se serializa.
    private ILogger logger = null;
    // --- FIN DE CAMBIOS DE LOGGER! ---
    
    public PlanificadorDisco(SistemaArchivos sa) {
        this.sistemaArchivos = sa; // Recibe el backend del Simulador
    }

    /**
     * MODIFICADO: Método privado que ejecuta la solicitud LLAMANDO al backend.
     */
    private int ejecutarSolicitud(SolicitudIO solicitud) {
        boolean exito = false;
        int bloqueProcesado = -1; // Para guardar el bloque

        // --- REEMPLAZADO ---
        if (solicitud.getTipo() == TipoOperacion.CREAR_ARCHIVO) {
            log("PLANIFICADOR: Ejecutando CREAR " + solicitud.getNombreArchivo());

            Directorio padre = solicitud.getDirectorioPadre();
            
            bloqueProcesado = sistemaArchivos.crearArchivo(
                solicitud.getNombreArchivo(), 
                solicitud.getTamanoEnBloques(),
                padre
            );
            exito = (bloqueProcesado != -1);

        } else if (solicitud.getTipo() == TipoOperacion.ELIMINAR_ARCHIVO) {
            log("PLANIFICADOR: Ejecutando ELIMINAR " + solicitud.getNombreArchivo());

            bloqueProcesado = sistemaArchivos.eliminarArchivo(
                solicitud.getNombreArchivo(),
                solicitud.getDirectorioPadre()
            );
            exito = (bloqueProcesado != -1);
        
        } else if (solicitud.getTipo() == TipoOperacion.LEER_ARCHIVO) {
            log("PLANIFICADOR: Ejecutando LEER " + solicitud.getNombreArchivo());
            
            bloqueProcesado = sistemaArchivos.leerArchivo(
                solicitud.getNombreArchivo(),
                solicitud.getDirectorioPadre()
            );
            exito = (bloqueProcesado != -1);
        }
        // --- FIN REEMPLAZO ---

        if (exito) {
            log("PLANIFICADOR: Operación completada con éxito.");
        } else {
            log("PLANIFICADOR: Operación falló.");
        }

        return bloqueProcesado;
    }

    // --- Políticas de Planificación ---

    // 1. Política FIFO
    public SolicitudIO ejecutarFIFO(Cola<SolicitudIO> colaIO) {
        if (colaIO.estaVacia()) {
            // --- REEMPLAZADO ---
            log("PLANIFICADOR: [FIFO] No hay solicitudes en la cola.");
            return null;
        }
        
        SolicitudIO solicitud = colaIO.desencolar();
        
        // --- MENSAJE DE LOG AÑADIDO ---
        log("PLANIFICADOR: [FIFO] Ejecutando " + solicitud.getNombreArchivo());
        
        ejecutarSolicitud(solicitud);
        
        return solicitud;
    }

    // 2. Política SSTF (Shortest Seek Time First)
    public SolicitudIO ejecutarSSTF(Cola<SolicitudIO> colaIO) {
        if (colaIO.estaVacia()) {
            log("PLANIFICADOR: [SSTF] No hay solicitudes en la cola.");
            return null; // No hay trabajo
        }
        
        // --- REEMPLAZADO ---
        log("PLANIFICADOR: [SSTF] Ejecutando lógica... (Cabezal en " + this.posicionCabezal + ")");
        
        ListaEnlazada<SolicitudIO> lista = colaIO.getListaInterna();
        NodoLista<SolicitudIO> nodoActual = lista.getInicio();
        
        SolicitudIO solicitudOptima = null;
        int distanciaMinima = Integer.MAX_VALUE;

        while (nodoActual != null) {
            SolicitudIO solActual = nodoActual.getDato();
            
            int posActual = getPosicionSolicitud(solActual);
            
            int distancia = Math.abs(posActual - this.posicionCabezal);

            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                solicitudOptima = solActual;
            }
            
            nodoActual = nodoActual.getSiguiente();
        }
        
        if (solicitudOptima == null) {
            return null; 
        }

        // --- MENSAJE DE LOG AÑADIDO ---
        log("PLANIFICADOR: [SSTF] Eligió " + solicitudOptima.getNombreArchivo() + " (Distancia: " + distanciaMinima + ")");
        
        lista.eliminar(solicitudOptima);
        
        int bloqueProcesado = ejecutarSolicitud(solicitudOptima);
        
        if (bloqueProcesado != -1) {
            this.posicionCabezal = bloqueProcesado;
            // --- REEMPLAZADO ---
            log("PLANIFICADOR: Cabezal movido a bloque " + this.posicionCabezal);
        }

        return solicitudOptima;
    }

    // 3. Política SCAN (Elevador)
    public SolicitudIO ejecutarSCAN(Cola<SolicitudIO> colaIO) {
        if (colaIO.estaVacia()) {
            log("PLANIFICADOR: [SCAN] No hay solicitudes en la cola.");
            return null;
        }

        // --- REEMPLAZADO ---
        log("PLANIFICADOR: [SCAN] Ejecutando lógica... (Dirección: " + direccionSCAN + ", Cabezal en " + this.posicionCabezal + ")");

        ListaEnlazada<SolicitudIO> lista = colaIO.getListaInterna();
        NodoLista<SolicitudIO> nodoActual = lista.getInicio();
        
        SolicitudIO solicitudOptima = null;
        int distanciaMinima = Integer.MAX_VALUE;

        // Búsqueda en la dirección actual
        while (nodoActual != null) {
            SolicitudIO solActual = nodoActual.getDato();
            int posActual = getPosicionSolicitud(solActual);
            
            boolean enLaMismaDireccion = false;
            if (direccionSCAN == Direccion.SUBIENDO && posActual >= this.posicionCabezal) {
                enLaMismaDireccion = true;
            } else if (direccionSCAN == Direccion.BAJANDO && posActual <= this.posicionCabezal) {
                enLaMismaDireccion = true;
            }

            if (enLaMismaDireccion) {
                int distancia = Math.abs(posActual - this.posicionCabezal);
                if (distancia < distanciaMinima) {
                    distanciaMinima = distancia;
                    solicitudOptima = solActual;
                }
            }
            nodoActual = nodoActual.getSiguiente();
        }

        // Si no encontramos nada, invertimos la dirección
        if (solicitudOptima == null) {
            // --- REEMPLAZADO ---
            log("PLANIFICADOR: [SCAN] Llegó al final, invirtiendo dirección.");
            
            if (this.direccionSCAN == Direccion.SUBIENDO) {
                this.direccionSCAN = Direccion.BAJANDO;
            } else {
                this.direccionSCAN = Direccion.SUBIENDO;
            }
            
            return ejecutarSCAN(colaIO);
        }

        // --- MENSAJE DE LOG AÑADIDO ---
        log("PLANIFICADOR: [SCAN] Eligió " + solicitudOptima.getNombreArchivo());

        lista.eliminar(solicitudOptima);
        int bloqueProcesado = ejecutarSolicitud(solicitudOptima);
        
        if (bloqueProcesado != -1) {
            this.posicionCabezal = bloqueProcesado;
            // --- REEMPLAZADO ---
            log("PLANIFICADOR: Cabezal movido a bloque " + this.posicionCabezal);
        }

        return solicitudOptima;
    }
    
    // 4. Política C-SCAN (Circular SCAN)
    public SolicitudIO ejecutarCSCAN(Cola<SolicitudIO> colaIO) {
        if (colaIO.estaVacia()) {
            log("PLANIFICADOR: [C-SCAN] No hay solicitudes en la cola.");
            return null;
        }

        // --- REEMPLAZADO ---
        log("PLANIFICADOR: [C-SCAN] Ejecutando lógica... (Siempre subiendo, Cabezal en " + this.posicionCabezal + ")");

        ListaEnlazada<SolicitudIO> lista = colaIO.getListaInterna();
        NodoLista<SolicitudIO> nodoActual = lista.getInicio();
        
        SolicitudIO solicitudOptima = null; // La que está más cerca "hacia adelante"
        int distanciaMinima = Integer.MAX_VALUE;
        
        SolicitudIO solicitudMasBaja = null; // La que está más cerca del "inicio" (para el salto)
        int posMasBaja = Integer.MAX_VALUE;

        // Recorremos TODA la cola
        while (nodoActual != null) {
            SolicitudIO solActual = nodoActual.getDato();
            int posActual = getPosicionSolicitud(solActual);

            // A. Buscamos solicitudes que estén "hacia adelante"
            if (posActual >= this.posicionCabezal) {
                int distancia = posActual - this.posicionCabezal;
                if (distancia < distanciaMinima) {
                    distanciaMinima = distancia;
                    solicitudOptima = solActual;
                }
            }

            // B. Al mismo tiempo, encontramos la solicitud más cercana al "inicio"
            if (posActual < posMasBaja) {
                posMasBaja = posActual;
                solicitudMasBaja = solActual;
            }
            
            nodoActual = nodoActual.getSiguiente();
        }

        // Si no encontramos nada "hacia adelante", saltamos al inicio
        if (solicitudOptima == null) {
            // --- REEMPLAZADO ---
            log("PLANIFICADOR: [C-SCAN] Llegó al final, volviendo al inicio (Bloque " + posMasBaja + ")");
            
            solicitudOptima = solicitudMasBaja;
        }
        
        if (solicitudOptima == null) {
             return null;
        }

        // --- MENSAJE DE LOG AÑADIDO ---
        log("PLANIFICADOR: [C-SCAN] Eligió " + solicitudOptima.getNombreArchivo());

        lista.eliminar(solicitudOptima);
        int bloqueProcesado = ejecutarSolicitud(solicitudOptima);
        
        if (bloqueProcesado != -1) {
            this.posicionCabezal = bloqueProcesado;
            // --- REEMPLAZADO ---
            log("PLANIFICADOR: Cabezal movido a bloque " + this.posicionCabezal);
        }

        return solicitudOptima;
    }
    
    /**
     * MÉTODO AYUDANTE PARA SSTF/SCAN/C-SCAN
     */
    private int getPosicionSolicitud(SolicitudIO solicitud) {
        
        if (solicitud.getTipo() == TipoOperacion.CREAR_ARCHIVO) {
            return this.sistemaArchivos.getDisco().getPrimerBloqueLibre();
        }
        
        // (Modificado para incluir LEER)
        if (solicitud.getTipo() == TipoOperacion.ELIMINAR_ARCHIVO ||
            solicitud.getTipo() == TipoOperacion.LEER_ARCHIVO ||
            solicitud.getTipo() == TipoOperacion.ACTUALIZAR_ARCHIVO) {
            
            NodoArbol nodo = solicitud.getDirectorioPadre().buscarHijo(solicitud.getNombreArchivo());
            
            if (nodo != null && nodo instanceof Archivo) {
                return ((Archivo) nodo).getIdPrimerBloque();
            }
        }
        
        // Fallback
        return this.posicionCabezal;
    }

    // --- ¡INICIO DE CAMBIOS DE LOGGER! ---
    // --- MÉTODOS NUEVOS ---
    
    /**
     * Recibe el logger desde el Simulador.
     */
    public void setLogger(ILogger logger) {
        this.logger = logger;
    }
    
    /**
     * Ayudante de log. Si tenemos un logger GUI, lo usa.
     */
    private void log(String mensaje) {
        if (this.logger != null) {
            this.logger.log(mensaje); // ¡Lo envía a la GUI!
        } else {
            System.out.println(mensaje); // Fallback
        }
    }
    // --- FIN DE CAMBIOS DE LOGGER! ---
}