/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto_sv;

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
    
    public Simulador() {
        // Inicializa todos los componentes
        this.sistemaArchivos = new SistemaArchivos(100); // Disco de 100 bloques
        
        // Pasa el "backend" al planificador para que pueda usarlo
        this.planificador = new PlanificadorDisco(this.sistemaArchivos);
        
        this.colaDeProcesos = new Cola<>();
        this.colaDeIO = new Cola<>();
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
     * Este método simula un "tick" del reloj del SO.
     * Le dice al planificador que ejecute UNA solicitud de la cola.
     * La GUI (en Fase 4) llamará a esto repetidamente.
     /**
     * MODIFICADO: Este método simula un "tick" del reloj del SO.
     */
    public void ejecutarTickPlanificador() {
        
        // (Aquí seleccionas la política, por ahora usamos FIFO)
        System.out.println("Planificador va a ejecutar una operación...");
        
        // 1. Llama al planificador y RECIBE la solicitud procesada
        SolicitudIO solicitudProcesada = planificador.ejecutarFIFO(this.colaDeIO);
        
        // 2. Llama al nuevo método para "terminar" el proceso
        terminarProceso(solicitudProcesada);
    }
    
    // --- Getters para que la GUI pueda "ver" el estado ---
    
    public SistemaArchivos getSistemaArchivos() { return sistemaArchivos; }
    public Cola<Proceso> getColaDeProcesos() { return colaDeProcesos; }
    public Cola<SolicitudIO> getColaDeIO() { return colaDeIO; }
}
