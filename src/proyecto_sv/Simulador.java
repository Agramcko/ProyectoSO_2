/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto_sv;

/**
 * @author Alessandro Gramcko
 * @author massimo Gramcko
 */
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.File;


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
        this.sistemaArchivos = cargarEstado(); // Disco de 100 bloques
        
        // Pasa el "backend" al planificador para que pueda usarlo
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
        
        // 1. Se crea la solicitud
        SolicitudIO solicitud = new SolicitudIO(tipo, nombre, tamano, 
                                    sistemaArchivos.getDirectorioActual());
        
        // 2. Se crea el proceso
        Proceso p = new Proceso(solicitud);
        p.setEstado(EstadoProceso.LISTO);
        
        // 3. El proceso va a la cola de listos (para la GUI)
        colaDeProcesos.encolar(p);
        
        // 4. La solicitud de E/S va a la cola del disco
        colaDeIO.encolar(solicitud);
        
        System.out.println("Nuevo Proceso " + p.getPid() + " en cola de listos.");
        System.out.println("Nueva SolicitudIO de " + tipo + " en cola de E/S.");
    }
    
    /**
     * NUEVO MÉTODO: Busca un proceso en la cola de listos
     * basado en la solicitud de E/S que generó, lo marca como
     * TERMINADO y lo elimina de la cola.
     */
    private void terminarProceso(SolicitudIO solicitudCompletada) {
        if (solicitudCompletada == null) return;

        NodoLista<Proceso> nodoP = colaDeProcesos.getListaInterna().getInicio();
        while (nodoP != null) {
            
            // Comparamos si es la misma solicitud
            // (Compara por referencia de objeto, que es lo que queremos)
            if (nodoP.getDato().getSolicitud() == solicitudCompletada) {
                
                Proceso p = nodoP.getDato();
                
                // 1. Cambiamos su estado
                p.setEstado(EstadoProceso.TERMINADO);
                System.out.println("Proceso " + p.getPid() + " ha TERMINADO.");
                
                // 2. Lo eliminamos de la cola de "Listos"
                this.colaDeProcesos.getListaInterna().eliminar(p);
                
                // Rompemos el ciclo, ya lo encontramos
                break; 
            }
            nodoP = nodoP.getSiguiente();
        }
    }
    
    /**
 * ¡NUEVO MÉTODO!
 * Carga el estado del SistemaArchivos desde el disco.
 * Si no existe, crea uno nuevo.
 */
private SistemaArchivos cargarEstado() {
    try (FileInputStream fis = new FileInputStream(NOMBRE_ARCHIVO_ESTADO);
         ObjectInputStream ois = new ObjectInputStream(fis)) {
        
        // Lee el objeto completo desde el archivo
        SistemaArchivos saCargado = (SistemaArchivos) ois.readObject();
        
        System.out.println("¡Éxito! Estado del disco cargado desde " + NOMBRE_ARCHIVO_ESTADO);
        return saCargado;

    } catch (java.io.FileNotFoundException e) {
        System.out.println("No se encontró archivo de estado. Creando uno nuevo...");
        // Si no hay archivo, crea un sistema de archivos nuevo
        
        // --- ¡LÍNEA ACTUALIZADA! ---
        return new SistemaArchivos(150); // Tamaño por defecto (150 bloques)

    } catch (IOException | ClassNotFoundException e) {
        System.err.println("Error al cargar el estado. Creando uno nuevo.");
        e.printStackTrace();
        // Si hay otro error, también crea uno nuevo
        
        // --- ¡LÍNEA ACTUALIZADA! ---
        return new SistemaArchivos(150);
    }
}

    /**
     * ¡NUEVO MÉTODO!
     * Guarda el estado actual del SistemaArchivos en el disco.
     */
    public void guardarEstado() {
        try (FileOutputStream fos = new FileOutputStream(NOMBRE_ARCHIVO_ESTADO);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            
            // Escribe el objeto sistemaArchivos (y todo lo que contiene) en el archivo
            oos.writeObject(this.sistemaArchivos);
            
            System.out.println("¡Éxito! Estado del disco guardado en " + NOMBRE_ARCHIVO_ESTADO);

        } catch (IOException e) {
            System.err.println("Error al guardar el estado.");
            e.printStackTrace();
        }
    }

    /**
     * Este método simula un "tick" del reloj del SO.
     * Le dice al planificador que ejecute UNA solicitud de la cola.
     * La GUI (en Fase 4) llamará a esto repetidamente.
     /**
     * MODIFICADO: Este método simula un "tick" del reloj del SO.
     */
    /**
     * MODIFICADO: Este método simula un "tick" del reloj del SO.
     * ¡Ahora obedece a la política de planificación seleccionada!
     */
    public void ejecutarTickPlanificador() {
        
        System.out.println("Planificador va a ejecutar una operación...");
        
        SolicitudIO solicitudProcesada = null;
        
        // 1. ¡NUEVO! Leemos la política actual
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
        
        // 2. Llama al método para "terminar" el proceso (esto es de antes)
        terminarProceso(solicitudProcesada);
    }
    
    // --- Getters para que la GUI pueda "ver" el estado ---
    
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
 * ¡NUEVO MÉTODO!
 * Elimina el archivo de estado guardado (.ser) del disco.
 * Esto fuerza un reinicio limpio la próxima vez que se ejecute.
 */
public boolean reiniciarEstado() {
    try {
        // (Asegúrate de que 'NOMBRE_ARCHIVO_ESTADO' sea el nombre
        // que definiste en tu método guardar/cargar)
        File archivoGuardado = new File(NOMBRE_ARCHIVO_ESTADO);

        if (archivoGuardado.exists()) {
            if (archivoGuardado.delete()) {
                System.out.println("REINICIO: Archivo 'estado_disco.ser' eliminado.");
                return true;
            } else {
                System.err.println("REINICIO: No se pudo eliminar 'estado_disco.ser'.");
                return false;
            }
        }
        // Si no existía, también es un éxito (ya estaba reiniciado)
        return true; 

    } catch (SecurityException e) {
        System.err.println("REINICIO: Error de seguridad al eliminar el archivo.");
        e.printStackTrace();
        return false;
    }
}
/**
 * Recibe el logger desde la GUI (VentanaPrincipal)
 * y lo pasa al sistema de archivos.
 */
public void setLogger(ILogger logger) {
    this.logger = logger;

    if (this.sistemaArchivos != null) {
        this.sistemaArchivos.setLogger(logger);
    }
}
}
