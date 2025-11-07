/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto_sv;

// --- ¡IMPORTS AÑADIDOS! ---
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
// --- FIN IMPORTS ---

/**

 * @author Alessandro Gramcko

 * @author massimo Gramcko

 */
public class Simulador {
    
    // 1. Contiene el "Backend"
    private SistemaArchivos sistemaArchivos;
    
    // 2. Contiene el "Planificador"
    private PlanificadorDisco planificador;

    // 3. ¡Contiene las Colas de Fase 1!
    private Cola<Proceso> colaDeProcesos; // Para la GUI
    private Cola<SolicitudIO> colaDeIO;  // Para el Planificador
    private ModoUsuario modoActual;
    private PoliticaPlanificacion politicaActual;
    private static final String NOMBRE_ARCHIVO_ESTADO = "estado_disco.ser";
    private ILogger logger = null;
    
    public Simulador() {
        // Inicializa todos los componentes
        this.sistemaArchivos = cargarEstado();
        
        this.planificador = new PlanificadorDisco(this.sistemaArchivos);
        
        this.colaDeProcesos = new Cola<>();
        this.colaDeIO = new Cola<>();
        this.modoActual = ModoUsuario.ADMINISTRADOR;
        this.politicaActual = PoliticaPlanificacion.FIFO;
    }

    
    /**
     * Este es el método que la GUI llamará.
     * NO ejecuta la operación, solo la encola.
     */
    public void nuevaSolicitudUsuario(TipoOperacion tipo, String nombre, int tamano) {
        
        SolicitudIO solicitud = new SolicitudIO(tipo, nombre, tamano, 
                                        sistemaArchivos.getDirectorioActual());
        
        Proceso p = new Proceso(solicitud);
        p.setEstado(EstadoProceso.LISTO);
        
        colaDeProcesos.encolar(p);
        colaDeIO.encolar(solicitud);
        
        // --- LOG CORREGIDO ---
        log("SIMULADOR: Nuevo Proceso " + p.getPid() + " en cola de listos.");
        log("SIMULADOR: Nueva SolicitudIO de " + tipo + " en cola de E/S.");
    }
    
    /**
     * Busca un proceso en la cola de listos
     * y lo marca como TERMINADO.
     */
    private void terminarProceso(SolicitudIO solicitudCompletada) {
        if (solicitudCompletada == null) return;

        NodoLista<Proceso> nodoP = colaDeProcesos.getListaInterna().getInicio();
        while (nodoP != null) {
            
            if (nodoP.getDato().getSolicitud() == solicitudCompletada) {
                
                Proceso p = nodoP.getDato();
                
                p.setEstado(EstadoProceso.TERMINADO);
                // --- LOG CORREGIDO ---
                log("SIMULADOR: Proceso " + p.getPid() + " ha TERMINADO.");
                
                this.colaDeProcesos.getListaInterna().eliminar(p);
                
                break; 
            }
            nodoP = nodoP.getSiguiente();
        }
    }
    
    /**
     * Carga el estado del SistemaArchivos desde el disco.
     */
    private SistemaArchivos cargarEstado() {
        try (FileInputStream fis = new FileInputStream(NOMBRE_ARCHIVO_ESTADO);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            
            SistemaArchivos saCargado = (SistemaArchivos) ois.readObject();
            
            // Re-conecta todos los punteros 'padre'
            saCargado.reconectarPadres();
            
            // --- LOG CORREGIDO ---
            log("SIMULADOR: ¡Éxito! Estado del disco cargado desde " + NOMBRE_ARCHIVO_ESTADO);
            return saCargado;

        } catch (java.io.FileNotFoundException e) {
            log("SIMULADOR: No se encontró archivo de estado. Creando uno nuevo (150 bloques)...");
            return new SistemaArchivos(150); 

        } catch (IOException | ClassNotFoundException e) {
            log("SIMULADOR: Error al cargar el estado. Creando uno nuevo (150 bloques).");
            e.printStackTrace();
            return new SistemaArchivos(150);
        }
    }

    /**
     * Guarda el estado actual del SistemaArchivos en el disco.
     */
    public void guardarEstado() {
        try (FileOutputStream fos = new FileOutputStream(NOMBRE_ARCHIVO_ESTADO);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            
            oos.writeObject(this.sistemaArchivos);
            
            // --- LOG CORREGIDO ---
            log("SIMULADOR: ¡Éxito! Estado del disco guardado en " + NOMBRE_ARCHIVO_ESTADO);

        } catch (IOException e) {
            log("SIMULADOR: Error al guardar el estado.");
            e.printStackTrace();
        }
    }

    /**
     * Simula un "tick" del reloj del SO.
     */
    public void ejecutarTickPlanificador() {
        
        // log("SIMULADOR: Tick del planificador..."); // (Opcional: es muy ruidoso)
        
        SolicitudIO solicitudProcesada = null;
        
        switch (politicaActual) {
            
            case FIFO:
                solicitudProcesada = planificador.ejecutarFIFO(this.colaDeIO);
                break;
                
            case SSTF:
                solicitudProcesada = planificador.ejecutarSSTF(this.colaDeIO);
                break;
                
            case SCAN:
                solicitudProcesada = planificador.ejecutarSCAN(this.colaDeIO);
                break;
                
            case C_SCAN:
                solicitudProcesada = planificador.ejecutarCSCAN(this.colaDeIO);
                break;
        }
        
        terminarProceso(solicitudProcesada);
    }
    
    // --- Getters y Setters ---
    
    public SistemaArchivos getSistemaArchivos() { return sistemaArchivos; }
    public Cola<Proceso> getColaDeProcesos() { return colaDeProcesos; }
    public Cola<SolicitudIO> getColaDeIO() { return colaDeIO; }
    
    public void setModo(ModoUsuario modo) {
        this.modoActual = modo;
    }

    public ModoUsuario getModo() {
        return this.modoActual;
    }
    public void setPolitica(PoliticaPlanificacion politica) {
        this.politicaActual = politica;
    }

    public PoliticaPlanificacion getPolitica() {
        return this.politicaActual;
    }

    /**
     * Elimina el archivo de estado guardado (.ser) del disco.
     */
    public boolean reiniciarEstado() {
        try {
            File archivoGuardado = new File(NOMBRE_ARCHIVO_ESTADO);

            if (archivoGuardado.exists()) {
                if (archivoGuardado.delete()) {
                    log("SIMULADOR: Archivo 'estado_disco.ser' eliminado.");
                    return true;
                } else {
                    log("SIMULADOR: No se pudo eliminar 'estado_disco.ser'.");
                    return false;
                }
            }
            return true; 

        } catch (SecurityException e) {
            log("SIMULADOR: Error de seguridad al eliminar el archivo.");
            e.printStackTrace();
            return false;
        }
    }
    
    // --- ¡MÉTODO setLogger CORREGIDO! ---
    /**
     * Recibe el logger desde la GUI (VentanaPrincipal)
     * y lo pasa a AMBOS sub-módulos.
     */
    public void setLogger(ILogger logger) {
        this.logger = logger;
        
        // Pasa el logger al Sistema de Archivos
        if (this.sistemaArchivos != null) {
            this.sistemaArchivos.setLogger(logger);
        }
        
        // --- ¡LA LÍNEA QUE FALTABA! ---
        // Pasa el logger al Planificador de Disco
        if (this.planificador != null) {
            this.planificador.setLogger(logger);
        }
    }
    
    // --- ¡MÉTODO AYUDANTE DE LOG AÑADIDO! ---
    /**
     * Ayudante de log para los mensajes del propio Simulador.
     */
    private void log(String mensaje) {
        if (this.logger != null) {
            this.logger.log(mensaje); // ¡Lo envía a la GUI!
        } else {
            System.out.println(mensaje); // Fallback
        }
    }
}
