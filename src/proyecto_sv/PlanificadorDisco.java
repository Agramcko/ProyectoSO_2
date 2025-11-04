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
    private enum Direccion { SUBIENDO, BAJANDO }
    private Direccion direccionSCAN = Direccion.SUBIENDO; // Empezamos "subiendo"
    public PlanificadorDisco(SistemaArchivos sa) {
        this.sistemaArchivos = sa; // Recibe el backend del Simulador
    }

    /**
     * Método privado que ejecuta la solicitud LLAMANDO al backend
     */
    /**
 * MODIFICADO: Método privado que ejecuta la solicitud LLAMANDO al backend.
 * ¡Ahora devuelve el ID del primer bloque que procesó!
 */
private int ejecutarSolicitud(SolicitudIO solicitud) {
    boolean exito = false;
    int bloqueProcesado = -1; // Para guardar el bloque

    if (solicitud.getTipo() == TipoOperacion.CREAR_ARCHIVO) {
        System.out.println("PLANIFICADOR: Ejecutando CREAR " + solicitud.getNombreArchivo());

        // MODIFICADO: necesitamos obtener el directorio padre de la solicitud
        Directorio padre = solicitud.getDirectorioPadre();
        // (Necesitamos modificar SistemaArchivos para que acepte esto)

        // ¡¡MODIFICACIÓN IMPORTANTE EN 'SistemaArchivos' NECESARIA!!
        // Debemos cambiar 'crearArchivo' para que acepte el directorio
        // y devuelva el idPrimerBloque
        bloqueProcesado = sistemaArchivos.crearArchivo(
            solicitud.getNombreArchivo(), 
            solicitud.getTamanoEnBloques(),
            padre // ¡Pasamos el directorio!
        );
        exito = (bloqueProcesado != -1);

    } else if (solicitud.getTipo() == TipoOperacion.ELIMINAR_ARCHIVO) {
        System.out.println("PLANIFICADOR: Ejecutando ELIMINAR " + solicitud.getNombreArchivo());

        // ¡¡MODIFICACIÓN IMPORTANTE EN 'SistemaArchivos' NECESARIA!!
        // Debemos cambiar 'eliminarArchivo' para que acepte el directorio
        // y devuelva el idPrimerBloque
        bloqueProcesado = sistemaArchivos.eliminarArchivo(
            solicitud.getNombreArchivo(),
            solicitud.getDirectorioPadre() // ¡Pasamos el directorio!
        );
        exito = (bloqueProcesado != -1);
    }

    if (exito) {
        System.out.println("PLANIFICADOR: Operación completada con éxito.");
    } else {
        System.out.println("PLANIFICADOR: Operación falló.");
    }

    return bloqueProcesado; // <-- ¡DEVOLVEMOS EL BLOQUE!
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

    // En PlanificadorDisco.java

    // ... (después de tu método ejecutarFIFO(...) ) ...

    /**
     * ¡NUEVO ESQUELETO!
     * 2. Política SSTF (Shortest Seek Time First)
     */
    // En PlanificadorDisco.java

    /**
     * ¡VERSIÓN REAL!
     * 2. Política SSTF (Shortest Seek Time First)
     */
    public SolicitudIO ejecutarSSTF(Cola<SolicitudIO> colaIO) {
        if (colaIO.estaVacia()) {
            return null; // No hay trabajo
        }
        
        System.out.println("PLANIFICADOR: Ejecutando lógica SSTF...");
        
        // --- 1. Encontrar la solicitud más cercana ---
        
        // Obtenemos la lista interna para poder recorrerla
        ListaEnlazada<SolicitudIO> lista = colaIO.getListaInterna();
        NodoLista<SolicitudIO> nodoActual = lista.getInicio();
        
        SolicitudIO solicitudOptima = null;
        int distanciaMinima = Integer.MAX_VALUE;

        // Recorremos TODA la cola
        while (nodoActual != null) {
            SolicitudIO solActual = nodoActual.getDato();
            
            // Usamos el ayudante para estimar la posición
            int posActual = getPosicionSolicitud(solActual);
            
            // Calculamos la distancia
            int distancia = Math.abs(posActual - this.posicionCabezal);

            // Si esta es más cercana que la mínima encontrada...
            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                solicitudOptima = solActual;
            }
            
            nodoActual = nodoActual.getSiguiente();
        }
        
        // --- 2. Procesar la solicitud óptima ---
        
        if (solicitudOptima == null) {
            // Esto no debería pasar si la cola no está vacía, pero por si acaso
            return null; 
        }

        // 3. Eliminarla de la cola (¡usando el método de Fase 1!)
        lista.eliminar(solicitudOptima);
        
        // 4. Ejecutarla (esto devuelve el bloque que procesó)
        int bloqueProcesado = ejecutarSolicitud(solicitudOptima);
        
        // 5. ¡IMPORTANTE! Actualizar la posición del cabezal
        if (bloqueProcesado != -1) {
            this.posicionCabezal = bloqueProcesado;
            System.out.println("PLANIFICADOR: Cabezal movido a bloque " + this.posicionCabezal);
        }

        // 6. Devolver la solicitud para que el Simulador termine el proceso
        return solicitudOptima;
    }

    /**
     * ¡NUEVO ESQUELETO!
     * 3. Política SCAN (Elevador)
     */
    // En PlanificadorDisco.java

    /**
     * ¡VERSIÓN REAL!
     * 3. Política SCAN (Elevador)
     */
    public SolicitudIO ejecutarSCAN(Cola<SolicitudIO> colaIO) {
        if (colaIO.estaVacia()) {
            return null; // No hay trabajo
        }

        System.out.println("PLANIFICADOR: Ejecutando lógica SCAN (Dirección: " + direccionSCAN + ")");

        // --- 1. Encontrar la solicitud óptima según SCAN ---
        ListaEnlazada<SolicitudIO> lista = colaIO.getListaInterna();
        NodoLista<SolicitudIO> nodoActual = lista.getInicio();
        
        SolicitudIO solicitudOptima = null;
        int distanciaMinima = Integer.MAX_VALUE;

        // --- 2. Búsqueda en la dirección actual ---
        while (nodoActual != null) {
            SolicitudIO solActual = nodoActual.getDato();
            int posActual = getPosicionSolicitud(solActual);
            
            boolean enLaMismaDireccion = false;
            if (direccionSCAN == Direccion.SUBIENDO && posActual >= this.posicionCabezal) {
                enLaMismaDireccion = true;
            } else if (direccionSCAN == Direccion.BAJANDO && posActual <= this.posicionCabezal) {
                enLaMismaDireccion = true;
            }

            // Si está en nuestro camino, vemos si es la más cercana
            if (enLaMismaDireccion) {
                int distancia = Math.abs(posActual - this.posicionCabezal);
                if (distancia < distanciaMinima) {
                    distanciaMinima = distancia;
                    solicitudOptima = solActual;
                }
            }
            nodoActual = nodoActual.getSiguiente();
        }

        // --- 3. Si no encontramos nada en nuestro camino, invertimos la dirección ---
        if (solicitudOptima == null) {
            System.out.println("PLANIFICADOR: SCAN llegó al final, invirtiendo dirección.");
            // Invertimos la dirección
            if (this.direccionSCAN == Direccion.SUBIENDO) {
                this.direccionSCAN = Direccion.BAJANDO;
            } else {
                this.direccionSCAN = Direccion.SUBIENDO;
            }
            
            // Volvemos a llamar al método con la dirección invertida
            // (Esto es una recursión simple para no repetir el código de búsqueda)
            return ejecutarSCAN(colaIO);
        }

        // --- 4. Procesar la solicitud óptima ---
        lista.eliminar(solicitudOptima);
        int bloqueProcesado = ejecutarSolicitud(solicitudOptima);
        
        if (bloqueProcesado != -1) {
            this.posicionCabezal = bloqueProcesado;
            System.out.println("PLANIFICADOR: Cabezal movido a bloque " + this.posicionCabezal);
        }

        return solicitudOptima;
    }
    
    /**
     * ¡NUEVO ESQUELETO!
     * 4. Política C-SCAN (Circular SCAN)
     */
    public SolicitudIO ejecutarCSCAN(Cola<SolicitudIO> colaIO) {
        if (colaIO.estaVacia()) {
            return null; // No hay trabajo
        }

        // ¡¡PENDIENTE!!
        // Aquí iría la lógica de C-SCAN
        System.out.println("PLANIFICADOR: (C-SCAN aún no implementado, usando FIFO)");
        return ejecutarFIFO(colaIO); 
    }
    
    /**
     * MÉTODO AYUDANTE PARA SSTF/SCAN/C-SCAN
     * Estima en qué bloque del disco operará una solicitud.
     */
    private int getPosicionSolicitud(SolicitudIO solicitud) {
        
        // Si es CREAR, la posición es el primer bloque libre que encuentre
        if (solicitud.getTipo() == TipoOperacion.CREAR_ARCHIVO) {
            // Usamos el método que creamos en DiscoSD
            return this.sistemaArchivos.getDisco().getPrimerBloqueLibre();
        }
        
        // Si es ELIMINAR (o LEER/ACTUALIZAR)
        if (solicitud.getTipo() == TipoOperacion.ELIMINAR_ARCHIVO) {
            
            // Buscamos el archivo para ver su bloque inicial
            NodoArbol nodo = solicitud.getDirectorioPadre().buscarHijo(solicitud.getNombreArchivo());
            
            if (nodo != null && nodo instanceof Archivo) {
                return ((Archivo) nodo).getIdPrimerBloque();
            }
        }
        
        // Si no se pudo determinar, devolvemos la posición actual (costo 0)
        return this.posicionCabezal;
    }
}
