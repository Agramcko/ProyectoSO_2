/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyecto_sv;

/**
 *
 * @author Alessandro
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // --- 1. EL CÓDIGO "NIMBUS" ---
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        // --- FIN DEL CÓDIGO NIMBUS ---

        
        // --- 2. EL CÓDIGO PARA ARRANCAR TU VENTANA ---
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                
                // --- ¡ESTA ES LA LÍNEA CORREGIDA! ---
                // Le pasamos el modo por defecto (ADMINISTRADOR)
                // para que coincida con tu constructor.
                new VentanaPrincipal(ModoUsuario.ADMINISTRADOR).setVisible(true);
            }
        });
    }
    
}
