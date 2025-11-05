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
/**
 * MODIFICADO: Método privado que ejecuta la solicitud LLAMANDO al backend.
 * ¡Ahora devuelve el ID del primer bloque que procesó!
 * ¡Y AHORA INCLUYE LÓGICA DE LECTURA!
 */
private int ejecutarSolicitud(SolicitudIO solicitud) {
    boolean exito = false;
    int bloqueProcesado = -1; // Para guardar el bloque

    if (solicitud.getTipo() == TipoOperacion.CREAR_ARCHIVO) {
        System.out.println("PLANIFICADOR: Ejecutando CREAR " + solicitud.getNombreArchivo());

        Directorio padre = solicitud.getDirectorioPadre();
        
        bloqueProcesado = sistemaArchivos.crearArchivo(
            solicitud.getNombreArchivo(), 
            solicitud.getTamanoEnBloques(),
            padre // ¡Pasamos el directorio!
        );
        exito = (bloqueProcesado != -1);

    } else if (solicitud.getTipo() == TipoOperacion.ELIMINAR_ARCHIVO) {
        System.out.println("PLANIFICADOR: Ejecutando ELIMINAR " + solicitud.getNombreArchivo());

        bloqueProcesado = sistemaArchivos.eliminarArchivo(
            solicitud.getNombreArchivo(),
            solicitud.getDirectorioPadre() // ¡Pasamos el directorio!
        );
        exito = (bloqueProcesado != -1);
    
    // --- ¡¡BLOQUE NUEVO AÑADIDO!! ---
    } else if (solicitud.getTipo() == TipoOperacion.LEER_ARCHIVO) {
        System.out.println("PLANIFICADOR: Ejecutando LEER " + solicitud.getNombreArchivo());
        
        // Llamamos al nuevo método de backend 'leerArchivo'
        bloqueProcesado = sistemaArchivos.leerArchivo(
            solicitud.getNombreArchivo(),
            solicitud.getDirectorioPadre()
        );
        exito = (bloqueProcesado != -1);
    // --- FIN DEL BLOQUE NUEVO ---
    
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
    // En PlanificadorDisco.java

    /**
     * ¡VERSIÓN REAL!
     * 4. Política C-SCAN (Circular SCAN)
     */
    public SolicitudIO ejecutarCSCAN(Cola<SolicitudIO> colaIO) {
        if (colaIO.estaVacia()) {
            return null; // No hay trabajo
        }

        System.out.println("PLANIFICADOR: Ejecutando lógica C-SCAN (siempre subiendo)...");

        ListaEnlazada<SolicitudIO> lista = colaIO.getListaInterna();
        NodoLista<SolicitudIO> nodoActual = lista.getInicio();
        
        SolicitudIO solicitudOptima = null; // La que está más cerca "hacia adelante"
        int distanciaMinima = Integer.MAX_VALUE;
        
        SolicitudIO solicitudMasBaja = null; // La que está más cerca del "inicio" (para el salto)
        int posMasBaja = Integer.MAX_VALUE;

        // --- 1. Recorremos TODA la cola ---
        while (nodoActual != null) {
            SolicitudIO solActual = nodoActual.getDato();
            int posActual = getPosicionSolicitud(solActual);

            // A. Buscamos solicitudes que estén "hacia adelante"
            if (posActual >= this.posicionCabezal) {
                int distancia = posActual - this.posicionCabezal; // Distancia simple
                if (distancia < distanciaMinima) {
                    distanciaMinima = distancia;
                    solicitudOptima = solActual;
                }
            }

            // B. Al mismo tiempo, encontramos la solicitud más cercana al "inicio" (bloque 0)
            //    Esto es para cuando tengamos que "saltar"
            if (posActual < posMasBaja) {
                posMasBaja = posActual;
                solicitudMasBaja = solActual;
            }
            
            nodoActual = nodoActual.getSiguiente();
        }

        // --- 2. Decidimos qué hacer ---
        
        // Si no encontramos nada "hacia adelante" (solicitudOptima sigue null),
        // significa que llegamos al final y debemos "saltar" al inicio.
        if (solicitudOptima == null) {
            System.out.println("PLANIFICADOR: C-SCAN llegó al final, volviendo al inicio.");
            
            // La solicitud óptima ahora es la que esté más cerca del inicio
            solicitudOptima = solicitudMasBaja;
        }
        
        if (solicitudOptima == null) {
             // Esto no debería pasar si la cola no estaba vacía.
             return null;
        }

        // --- 3. Procesar la solicitud elegida ---
        lista.eliminar(solicitudOptima);
        int bloqueProcesado = ejecutarSolicitud(solicitudOptima);
        
        if (bloqueProcesado != -1) {
            this.posicionCabezal = bloqueProcesado;
            System.out.println("PLANIFICADOR: Cabezal movido a bloque " + this.posicionCabezal);
        }

        return solicitudOptima;
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
