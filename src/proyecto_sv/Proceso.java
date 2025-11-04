/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto_sv;

/**
 * @author Alessandro Gramcko
 * @author massimo Gramcko
 */

public class Proceso {
    private static int proximoId = 1; // Generador de IDs
    private int pid; // Process ID
    private EstadoProceso estado;
    private SolicitudIO solicitud; // La operación que este proceso quiere hacer

    public Proceso(SolicitudIO solicitud) {
        this.pid = proximoId++;
        this.solicitud = solicitud;
        this.estado = EstadoProceso.NUEVO; // Nace en estado NUEVO
    }

    // --- Getters y Setters ---
    public int getPid() { return pid; }
    public SolicitudIO getSolicitud() { return solicitud; }
    public EstadoProceso getEstado() { return estado; }
    public void setEstado(EstadoProceso estado) { this.estado = estado; }
}
