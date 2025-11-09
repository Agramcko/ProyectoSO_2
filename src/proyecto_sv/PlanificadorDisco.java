/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto_sv;

public class PlanificadorDisco {
    
    // Referencia al "Backend"
    private SistemaArchivos sistemaArchivos;
    
    private int posicionCabezal = 0; // Para SSTF, SCAN, etc.
    private enum Direccion { SUBIENDO, BAJANDO }
    private Direccion direccionSCAN = Direccion.SUBIENDO;
    
    // ¡LA VARIABLE DEL LOGGER!
    private ILogger logger = null;
    
    public PlanificadorDisco(SistemaArchivos sa) {
        this.sistemaArchivos = sa; // Recibe el backend del Simulador
    }

    /**
     * Permite actualizar la referencia al Sistema de Archivos
     * (usado para cargar estado).
     * ¡ADEMÁS! Resetea la dirección del SCAN.
     */
    public void setSistemaArchivos(SistemaArchivos sa) {
        this.sistemaArchivos = sa;
        // Resetea el elevador a "subiendo" para pruebas consistentes.
        this.direccionSCAN = Direccion.SUBIENDO;
    }

    /**
     * Método privado que ejecuta la solicitud LLAMANDO al backend.
     */
    private int ejecutarSolicitud(SolicitudIO solicitud) {
        boolean exito = false;
        int bloqueProcesado = -1; 

        if (solicitud.getTipo() == TipoOperacion.CREAR_ARCHIVO) {
            // El log de 'CREAR' (y su emoji ✅) se maneja en SistemaArchivos
            Directorio padre = solicitud.getDirectorioPadre();
            bloqueProcesado = sistemaArchivos.crearArchivo(
                solicitud.getNombreArchivo(), 
                solicitud.getTamanoEnBloques(),
                padre
            );
            exito = (bloqueProcesado != -1);

        } else if (solicitud.getTipo() == TipoOperacion.ELIMINAR_ARCHIVO) {
            // --- ¡EMOJI AÑADIDO! ---
            log("PLANIFICADOR: 🗑️ Ejecutando ELIMINAR " + solicitud.getNombreArchivo());
            bloqueProcesado = sistemaArchivos.eliminarArchivo(
                solicitud.getNombreArchivo(),
                solicitud.getDirectorioPadre()
            );
            exito = (bloqueProcesado != -1);
        
        } else if (solicitud.getTipo() == TipoOperacion.LEER_ARCHIVO) {
            // --- ¡EMOJI AÑADIDO! ---
            log("PLANIFICADOR: 📖 Ejecutando LEER " + solicitud.getNombreArchivo());
            bloqueProcesado = sistemaArchivos.leerArchivo(
                solicitud.getNombreArchivo(),
                solicitud.getDirectorioPadre()
            );
            exito = (bloqueProcesado != -1);
        }

        if (exito) {
            log("PLANIFICADOR: ✅ Operación completada con éxito.");
        } else {
            // El log de "fallo" (ej. ⛔ DISCO LLENO)
            // se maneja ahora dentro de SistemaArchivos.
            log("PLANIFICADOR: ❌ Operación falló (revisar logs anteriores).");
        }

        return bloqueProcesado;
    }

    // --- Políticas de Planificación (¡CON EMOJIS!) ---

    // 1. Política FIFO
    public SolicitudIO ejecutarFIFO(Cola<SolicitudIO> colaIO) {
        if (colaIO.estaVacia()) {
            // log("PLANIFICADOR: [FIFO] No hay solicitudes en la cola.");
            return null;
        }

        SolicitudIO solicitud = colaIO.desencolar();

        // --- ¡EMOJI AÑADIDO! ---
        log("PLANIFICADOR: [FIFO] ➡️ Decisión: Ejecutando " + solicitud.getNombreArchivo() + " (es el primero en la cola)");

        int bloqueProcesado = ejecutarSolicitud(solicitud);

        if (bloqueProcesado != -1) {
            this.posicionCabezal = bloqueProcesado;
            // --- ¡EMOJI AÑADIDO! ---
            log("PLANIFICADOR: 📍 Cabezal movido a bloque " + this.posicionCabezal);
        }
        
        return solicitud;
    }

    // 2. Política SSTF
    public SolicitudIO ejecutarSSTF(Cola<SolicitudIO> colaIO) {
        if (colaIO.estaVacia()) {
           // log("PLANIFICADOR: [SSTF] No hay solicitudes en la cola.");
            return null;
        }
        
        // --- ¡EMOJI AÑADIDO! ---
        log("PLANIFICADOR: [SSTF] 🧠 Buscando... (Cabezal actual en bloque " + this.posicionCabezal + ")");
        
        ListaEnlazada<SolicitudIO> lista = colaIO.getListaInterna();
        NodoLista<SolicitudIO> nodoActual = lista.getInicio();
        
        SolicitudIO solicitudOptima = null;
        int distanciaMinima = Integer.MAX_VALUE;
        int posOptima = -1;

        while (nodoActual != null) {
            SolicitudIO solActual = nodoActual.getDato();
            int posActual = getPosicionSolicitud(solActual);
            int distancia = Math.abs(posActual - this.posicionCabezal);

            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                solicitudOptima = solActual;
                posOptima = posActual;
            }
            
            nodoActual = nodoActual.getSiguiente();
        }
        
        if (solicitudOptima == null) {
            return null; 
        }

        // --- ¡EMOJI AÑADIDO! ---
        log("PLANIFICADOR: [SSTF] 🧠 Decisión: Elegido " + solicitudOptima.getNombreArchivo() + 
            " (Bloque " + posOptima + ") con distancia " + distanciaMinima);
        
        lista.eliminar(solicitudOptima);
        
        int bloqueProcesado = ejecutarSolicitud(solicitudOptima);
        
        if (bloqueProcesado != -1) {
            this.posicionCabezal = bloqueProcesado;
            // --- ¡EMOJI AÑADIDO! ---
            log("PLANIFICADOR: 📍 Cabezal movido a bloque " + this.posicionCabezal);
        }

        return solicitudOptima;
    }

    // 3. Política SCAN
    public SolicitudIO ejecutarSCAN(Cola<SolicitudIO> colaIO) {
        if (colaIO.estaVacia()) {
           // log("PLANIFICADOR: [SCAN] No hay solicitudes en la cola.");
            return null;
        }

        // --- ¡EMOJI AÑADIDO! ---
        log("PLANIFICADOR: [SCAN] ↕️ Buscando... (Dirección: " + direccionSCAN + ", Cabezal en " + this.posicionCabezal + ")");

        ListaEnlazada<SolicitudIO> lista = colaIO.getListaInterna();
        NodoLista<SolicitudIO> nodoActual = lista.getInicio();
        
        SolicitudIO solicitudOptima = null;
        int distanciaMinima = Integer.MAX_VALUE;
        int posOptima = -1;

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
                    posOptima = posActual;
                }
            }
            nodoActual = nodoActual.getSiguiente();
        }

        if (solicitudOptima == null) {
            // --- ¡EMOJI AÑADIDO! ---
            log("PLANIFICADOR: [SCAN] ↕️ Llegó al final, invirtiendo dirección.");
            
            if (this.direccionSCAN == Direccion.SUBIENDO) {
                this.direccionSCAN = Direccion.BAJANDO;
            } else {
                this.direccionSCAN = Direccion.SUBIENDO;
            }
            
            return ejecutarSCAN(colaIO);
        }

        // --- ¡EMOJI AÑADIDO! ---
        log("PLANIFICADOR: [SCAN] ↕️ Decisión: Elegido " + solicitudOptima.getNombreArchivo() + 
            " (Bloque " + posOptima + ", en dirección " + direccionSCAN + ")");

        lista.eliminar(solicitudOptima);
        int bloqueProcesado = ejecutarSolicitud(solicitudOptima);
        
        if (bloqueProcesado != -1) {
            this.posicionCabezal = bloqueProcesado;
            // --- ¡EMOJI AÑADIDO! ---
            log("PLANIFICADOR: 📍 Cabezal movido a bloque " + this.posicionCabezal);
        }

        return solicitudOptima;
    }
    
    // 4. Política C-SCAN
    public SolicitudIO ejecutarCSCAN(Cola<SolicitudIO> colaIO) {
        if (colaIO.estaVacia()) {
           // log("PLANIFICADOR: [C-SCAN] No hay solicitudes en la cola.");
            return null;
        }

        // --- ¡EMOJI AÑADIDO! ---
        log("PLANIFICADOR: [C-SCAN] 🔄 Buscando... (Cabezal en " + this.posicionCabezal + ")");

        ListaEnlazada<SolicitudIO> lista = colaIO.getListaInterna();
        NodoLista<SolicitudIO> nodoActual = lista.getInicio();
        
        SolicitudIO solicitudOptima = null;
        int distanciaMinima = Integer.MAX_VALUE;
        
        SolicitudIO solicitudMasBaja = null;
        int posMasBaja = Integer.MAX_VALUE;

        while (nodoActual != null) {
            SolicitudIO solActual = nodoActual.getDato();
            int posActual = getPosicionSolicitud(solActual);

            if (posActual >= this.posicionCabezal) {
                int distancia = posActual - this.posicionCabezal;
                if (distancia < distanciaMinima) {
                    distanciaMinima = distancia;
                    solicitudOptima = solActual;
                }
            }

            if (posActual < posMasBaja) {
                posMasBaja = posActual;
                solicitudMasBaja = solActual;
            }
            
            nodoActual = nodoActual.getSiguiente();
        }

        if (solicitudOptima == null) {
            // --- ¡EMOJI AÑADIDO! ---
            log("PLANIFICADOR: [C-SCAN] 🔄 Llegó al final, saltando al inicio (Bloque " + posMasBaja + ")");
            solicitudOptima = solicitudMasBaja;
        }
        
        if (solicitudOptima == null) {
             return null;
        }

        // --- ¡EMOJI AÑADIDO! ---
        int posFinalElegida = getPosicionSolicitud(solicitudOptima);
        log("PLANIFICADOR: [C-SCAN] 🔄 Decisión: Elegido " + solicitudOptima.getNombreArchivo() + " (Bloque " + posFinalElegida + ")");

        lista.eliminar(solicitudOptima);
        int bloqueProcesado = ejecutarSolicitud(solicitudOptima);
        
        if (bloqueProcesado != -1) {
            this.posicionCabezal = bloqueProcesado;
            // --- ¡EMOJI AÑADIDO! ---
            log("PLANIFICADOR: 📍 Cabezal movido a bloque " + this.posicionCabezal);
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
        
        if (solicitud.getTipo() == TipoOperacion.ELIMINAR_ARCHIVO ||
            solicitud.getTipo() == TipoOperacion.LEER_ARCHIVO ||
            solicitud.getTipo() == TipoOperacion.ACTUALIZAR_ARCHIVO) {
            
            NodoArbol nodo = solicitud.getDirectorioPadre().buscarHijo(solicitud.getNombreArchivo());
            
            if (nodo != null && nodo instanceof Archivo) {
                return ((Archivo) nodo).getIdPrimerBloque();
            }
        }
        
        return this.posicionCabezal;
    }

    // --- MÉTODOS DEL LOGGER ---
    
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
}