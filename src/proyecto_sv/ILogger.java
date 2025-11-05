/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package proyecto_sv;

/**
 *
 * @author massi
 */


/**
 * Define un "contrato" simple para cualquier clase
 * que quiera actuar como un logger.
 */
public interface ILogger {
    // Un solo método: registrar un mensaje.
    void log(String mensaje);
}
