/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package proyecto_sv;

/**
 * @author Alessandro Gramcko
 * @author massimo Gramcko
 */

import java.util.Random;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Component;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Enumeration;

public class VentanaPrincipal extends javax.swing.JFrame implements ILogger {

    // Esta es la variable que controlará todo el backend
    private Simulador simulador;
    private DefaultTreeModel modeloArbol;
    private DefaultMutableTreeNode raizArbol;
    private boolean estaActualizandoArbol = false; // Nuestro "semáforo"
    
   /**
 * Constructor de la Ventana Principal.
 * AHORA ACEPTA el modo de inicio como parámetro.
 */
public VentanaPrincipal(ModoUsuario modoInicial) {
    
    initComponents();
    
    // --- ¡NUEVO! APLICAR EL RENDERER DE ICONOS (Paso 2.3) ---
    // (Asegúrate de haber añadido la clase 'MyTreeCellRenderer' al final de tu archivo)
    arbolArchivos.setCellRenderer(new MyTreeCellRenderer());
    // --- FIN ---
    
    // 1. Creamos la instancia del "cerebro"
    this.simulador = new Simulador();
    simulador.setLogger(this);

    // --- ¡NUEVO! INICIALIZAR EL MODELO DEL ÁRBOL UNA SOLA VEZ (Paso 3) ---
    Directorio raizBackend = simulador.getSistemaArchivos().getRaiz();
    this.raizArbol = new DefaultMutableTreeNode(raizBackend); // Guardamos la raíz
    this.modeloArbol = new DefaultTreeModel(this.raizArbol); // Guardamos el modelo
    arbolArchivos.setModel(this.modeloArbol); // ¡Lo asignamos!
    // --- FIN ---

    // --- ¡NUEVA LÓGICA DE INICIO! ---
    
    // 2. Establecemos el modo inicial en el backend (el que vino del diálogo)
    simulador.setModo(modoInicial);
    
    // 3. Sincronizamos los botones de radio visualmente
    if (modoInicial == ModoUsuario.ADMINISTRADOR) {
        radioAdmin.setSelected(true);
    } else {
        radioUsuario.setSelected(true);
    }
    // --- FIN DE LA NUEVA LÓGICA ---

    // 4. Establecemos el layout del disco (tu código existente)
    int totalBloquesDisco = 100; 
    int filas = (int) Math.ceil(Math.sqrt(totalBloquesDisco));
    int columnas = filas;
    panelDisco.setLayout(new java.awt.GridLayout(filas, columnas, 2, 2));
    
    // 5. Iniciamos el Timer (tu código existente)
    javax.swing.Timer timer = new javax.swing.Timer(2000, new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            simulador.ejecutarTickPlanificador();
            actualizarGUICompleta();
        }
    });
    timer.start(); // ¡Inicia el reloj!
    
    // 6. Añadimos el oyente para guardar al cerrar (tu código existente)
    this.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent e) {
            System.out.println("GUI: Guardando estado antes de salir...");
            simulador.guardarEstado();
        }
    });

    // 7. ¡IMPORTANTE! Establecemos los permisos y actualizamos la GUI una vez al inicio
    actualizarPermisosGUI(); // <-- Lee el modo y deshabilita botones si es Usuario
    actualizarGUICompleta(); // <-- Dibuja el estado cargado (disco, árbol, etc.)
    
    // --- ¡NUEVO! OYENTE DE SELECCIÓN DEL ÁRBOL (Paso 1.4) ---
    arbolArchivos.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
        public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
            
            // --- ¡AÑADE ESTA LÍNEA DE GUARDIA! ---
            if (estaActualizandoArbol) {
                return; // ¡No hagas nada! El Timer está trabajando.
            }
            // --- FIN LÍNEA NUEVA ---
            
            // 1. Obtener el nodo de la GUI que fue seleccionado
            javax.swing.tree.DefaultMutableTreeNode nodoSwing;
            nodoSwing = (javax.swing.tree.DefaultMutableTreeNode) arbolArchivos.getLastSelectedPathComponent();
            
            if (nodoSwing == null) return; // No hay nada seleccionado
            
            // 2. Obtener el objeto de BACKEND que guardamos dentro
            Object objetoBackend = nodoSwing.getUserObject();
            
            // 3. Si es un Directorio, lo establecemos como actual
            if (objetoBackend instanceof Directorio) {
                simulador.getSistemaArchivos().setDirectorioActual((Directorio) objetoBackend);
            }
        }
    });
    // --- FIN DEL OYENTE ---
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        splitPanePrincipal = new javax.swing.JSplitPane();
        scrollArbol = new javax.swing.JScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        arbolArchivos = new javax.swing.JTree();
        splitPaneDerecho = new javax.swing.JSplitPane();
        splitPaneVisuals = new javax.swing.JSplitPane();
        scrollDisco = new javax.swing.JScrollPane();
        panelDisco = new javax.swing.JPanel();
        scrollTabla = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaAsignacion = new javax.swing.JTable();
        panelControlesGeneral = new javax.swing.JPanel();
        panelAcciones = new javax.swing.JPanel();
        lblNombre = new javax.swing.JLabel();
        txtNombreArchivo = new javax.swing.JTextField();
        lblTamano = new javax.swing.JLabel();
        spinnerTamano = new javax.swing.JSpinner();
        btnCrearArchivo = new javax.swing.JButton();
        btnEliminarArchivo = new javax.swing.JButton();
        btnLeerArchivo = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtNuevoNombre = new javax.swing.JTextField();
        btnRenombrar = new javax.swing.JButton();
        btnCrearDirectorio = new javax.swing.JButton();
        btnEliminarDirectorio = new javax.swing.JButton();
        btnGenerarReporte = new javax.swing.JButton();
        btnGenerarAleatorios = new javax.swing.JButton();
        btnReiniciar = new javax.swing.JButton();
        panelSistema = new javax.swing.JPanel();
        comboPolitica = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        radioAdmin = new javax.swing.JRadioButton();
        radioUsuario = new javax.swing.JRadioButton();
        scrollBuffer = new javax.swing.JScrollPane();
        areaBuffer = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        areaLogConsola = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        areaLogPlanificador = new javax.swing.JTextArea();
        scrollColas = new javax.swing.JScrollPane();
        areaColasProcesos = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        splitPanePrincipal.setResizeWeight(0.3);

        arbolArchivos.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jScrollPane1.setViewportView(arbolArchivos);

        scrollArbol.setViewportView(jScrollPane1);

        splitPanePrincipal.setLeftComponent(scrollArbol);

        splitPaneDerecho.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitPaneDerecho.setResizeWeight(0.6);

        scrollDisco.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Disco SD", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Black", 1, 12))); // NOI18N

        javax.swing.GroupLayout panelDiscoLayout = new javax.swing.GroupLayout(panelDisco);
        panelDisco.setLayout(panelDiscoLayout);
        panelDiscoLayout.setHorizontalGroup(
            panelDiscoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        panelDiscoLayout.setVerticalGroup(
            panelDiscoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 408, Short.MAX_VALUE)
        );

        scrollDisco.setViewportView(panelDisco);

        splitPaneVisuals.setLeftComponent(scrollDisco);

        scrollTabla.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tabla de Asignación", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Black", 1, 12))); // NOI18N

        tablaAsignacion.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nombre", "Tamaño (Bloques)", "Bloque Inicial"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tablaAsignacion);

        scrollTabla.setViewportView(jScrollPane2);

        splitPaneVisuals.setRightComponent(scrollTabla);

        splitPaneDerecho.setTopComponent(splitPaneVisuals);

        panelControlesGeneral.setLayout(new javax.swing.BoxLayout(panelControlesGeneral, javax.swing.BoxLayout.LINE_AXIS));

        panelAcciones.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Acciones", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Black", 1, 12))); // NOI18N

        lblNombre.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        lblNombre.setText("Nombre:");

        txtNombreArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombreArchivoActionPerformed(evt);
            }
        });

        lblTamano.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        lblTamano.setText("Tamaño (Bloques):");

        spinnerTamano.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

        btnCrearArchivo.setBackground(new java.awt.Color(51, 153, 255));
        btnCrearArchivo.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        btnCrearArchivo.setForeground(new java.awt.Color(255, 255, 255));
        btnCrearArchivo.setText("Crear Archivo");
        btnCrearArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCrearArchivoActionPerformed(evt);
            }
        });

        btnEliminarArchivo.setBackground(new java.awt.Color(255, 51, 51));
        btnEliminarArchivo.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        btnEliminarArchivo.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarArchivo.setText("Eliminar Archivo");
        btnEliminarArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarArchivoActionPerformed(evt);
            }
        });

        btnLeerArchivo.setBackground(new java.awt.Color(255, 255, 0));
        btnLeerArchivo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLeerArchivo.setText("Leer Archivo");
        btnLeerArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLeerArchivoActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        jLabel2.setText("Nuevo Nombre:");

        txtNuevoNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNuevoNombreActionPerformed(evt);
            }
        });

        btnRenombrar.setBackground(new java.awt.Color(51, 153, 255));
        btnRenombrar.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        btnRenombrar.setForeground(new java.awt.Color(255, 255, 255));
        btnRenombrar.setText("Renombrar");
        btnRenombrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRenombrarActionPerformed(evt);
            }
        });

        btnCrearDirectorio.setBackground(new java.awt.Color(51, 153, 255));
        btnCrearDirectorio.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        btnCrearDirectorio.setForeground(new java.awt.Color(255, 255, 255));
        btnCrearDirectorio.setText("Crear Directorio");
        btnCrearDirectorio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCrearDirectorioActionPerformed(evt);
            }
        });

        btnEliminarDirectorio.setBackground(new java.awt.Color(255, 51, 51));
        btnEliminarDirectorio.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        btnEliminarDirectorio.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarDirectorio.setText("Eliminar Directorio");
        btnEliminarDirectorio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarDirectorioActionPerformed(evt);
            }
        });

        btnGenerarReporte.setBackground(new java.awt.Color(51, 204, 0));
        btnGenerarReporte.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        btnGenerarReporte.setForeground(new java.awt.Color(255, 255, 255));
        btnGenerarReporte.setText("Generar Reporte");
        btnGenerarReporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarReporteActionPerformed(evt);
            }
        });

        btnGenerarAleatorios.setBackground(new java.awt.Color(51, 204, 0));
        btnGenerarAleatorios.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        btnGenerarAleatorios.setForeground(new java.awt.Color(255, 255, 255));
        btnGenerarAleatorios.setText("Generar Aleatorios (20)");
        btnGenerarAleatorios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarAleatoriosActionPerformed(evt);
            }
        });

        btnReiniciar.setBackground(new java.awt.Color(255, 0, 51));
        btnReiniciar.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        btnReiniciar.setForeground(new java.awt.Color(255, 255, 255));
        btnReiniciar.setText("Reiniciar Todo");
        btnReiniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReiniciarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelAccionesLayout = new javax.swing.GroupLayout(panelAcciones);
        panelAcciones.setLayout(panelAccionesLayout);
        panelAccionesLayout.setHorizontalGroup(
            panelAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAccionesLayout.createSequentialGroup()
                .addGroup(panelAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelAccionesLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNuevoNombre))
                    .addGroup(panelAccionesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnCrearArchivo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(panelAccionesLayout.createSequentialGroup()
                                .addComponent(lblNombre)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtNombreArchivo))
                            .addGroup(panelAccionesLayout.createSequentialGroup()
                                .addComponent(lblTamano)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spinnerTamano, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 57, Short.MAX_VALUE))
                            .addComponent(btnEliminarArchivo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnRenombrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(panelAccionesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnLeerArchivo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelAccionesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnCrearDirectorio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnEliminarDirectorio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelAccionesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnGenerarReporte, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnGenerarAleatorios, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(panelAccionesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnReiniciar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelAccionesLayout.setVerticalGroup(
            panelAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAccionesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNombre)
                    .addComponent(txtNombreArchivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTamano)
                    .addComponent(spinnerTamano, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnCrearArchivo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnEliminarArchivo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 120, Short.MAX_VALUE)
                .addGroup(panelAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtNuevoNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnRenombrar)
                .addGap(27, 27, 27)
                .addComponent(btnLeerArchivo)
                .addGap(18, 18, 18)
                .addComponent(btnCrearDirectorio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEliminarDirectorio)
                .addGap(18, 18, 18)
                .addComponent(btnReiniciar)
                .addGap(5, 5, 5)
                .addComponent(btnGenerarAleatorios)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGenerarReporte)
                .addContainerGap())
        );

        panelControlesGeneral.add(panelAcciones);

        panelSistema.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Sistema", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Black", 1, 12))); // NOI18N

        comboPolitica.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FIFO", "SSTF", "SCAN", "C-SCAN" }));
        comboPolitica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboPoliticaActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        jLabel1.setText("Política:");

        buttonGroup1.add(radioAdmin);
        radioAdmin.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        radioAdmin.setForeground(new java.awt.Color(51, 204, 0));
        radioAdmin.setText("Modo Administrador");
        radioAdmin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioAdminActionPerformed(evt);
            }
        });

        buttonGroup1.add(radioUsuario);
        radioUsuario.setFont(new java.awt.Font("Segoe UI Black", 1, 12)); // NOI18N
        radioUsuario.setForeground(new java.awt.Color(51, 153, 255));
        radioUsuario.setText("Modo Usuario");
        radioUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioUsuarioActionPerformed(evt);
            }
        });

        scrollBuffer.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Buffer de Bloques", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Black", 1, 12))); // NOI18N

        areaBuffer.setColumns(20);
        areaBuffer.setRows(5);
        scrollBuffer.setViewportView(areaBuffer);

        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Consola de Eventos Buffer", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Black", 1, 12))); // NOI18N

        areaLogConsola.setEditable(false);
        areaLogConsola.setColumns(20);
        areaLogConsola.setRows(5);
        jScrollPane3.setViewportView(areaLogConsola);

        jScrollPane4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Log del Planificador", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Black", 1, 12))); // NOI18N

        areaLogPlanificador.setEditable(false);
        areaLogPlanificador.setColumns(20);
        areaLogPlanificador.setRows(5);
        jScrollPane4.setViewportView(areaLogPlanificador);

        javax.swing.GroupLayout panelSistemaLayout = new javax.swing.GroupLayout(panelSistema);
        panelSistema.setLayout(panelSistemaLayout);
        panelSistemaLayout.setHorizontalGroup(
            panelSistemaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSistemaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSistemaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelSistemaLayout.createSequentialGroup()
                        .addComponent(jScrollPane3)
                        .addGap(2, 2, 2))
                    .addComponent(scrollBuffer, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)
                    .addGroup(panelSistemaLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboPolitica, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelSistemaLayout.createSequentialGroup()
                        .addGroup(panelSistemaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(radioUsuario)
                            .addComponent(radioAdmin))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelSistemaLayout.setVerticalGroup(
            panelSistemaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSistemaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSistemaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboPolitica, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addComponent(radioAdmin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(radioUsuario)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollBuffer, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        panelControlesGeneral.add(panelSistema);

        scrollColas.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Colas", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Black", 1, 12))); // NOI18N

        areaColasProcesos.setColumns(20);
        areaColasProcesos.setRows(5);
        scrollColas.setViewportView(areaColasProcesos);

        panelControlesGeneral.add(scrollColas);

        splitPaneDerecho.setRightComponent(panelControlesGeneral);

        splitPanePrincipal.setRightComponent(splitPaneDerecho);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPanePrincipal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1063, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPanePrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 835, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void radioAdminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioAdminActionPerformed
      simulador.setModo(ModoUsuario.ADMINISTRADOR);
    actualizarPermisosGUI(); // Llamamos al método que actualiza los botones
    }//GEN-LAST:event_radioAdminActionPerformed

    private void radioUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioUsuarioActionPerformed
      simulador.setModo(ModoUsuario.USUARIO);
    actualizarPermisosGUI(); // Llamamos al método que actualiza los botones
    }//GEN-LAST:event_radioUsuarioActionPerformed

    private void comboPoliticaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboPoliticaActionPerformed
        // 1. Obtener el texto seleccionado (ej. "FIFO", "SSTF")
    String seleccion = (String) comboPolitica.getSelectedItem();

    // 2. Convertir el texto a nuestro enum
    switch (seleccion) {
        case "FIFO":
            simulador.setPolitica(PoliticaPlanificacion.FIFO);
            break;
        case "SSTF":
            simulador.setPolitica(PoliticaPlanificacion.SSTF);
            break;
        case "SCAN":
            simulador.setPolitica(PoliticaPlanificacion.SCAN);
            break;
        case "C-SCAN":
            simulador.setPolitica(PoliticaPlanificacion.C_SCAN);
            break;
    }

    System.out.println("GUI: Política cambiada a " + simulador.getPolitica());
    }//GEN-LAST:event_comboPoliticaActionPerformed

    private void btnReiniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReiniciarActionPerformed
        // 1. Advertencia al usuario (¡esto es destructivo!)
    int confirm = javax.swing.JOptionPane.showConfirmDialog(this,
        "¿Está seguro de que desea reiniciar la simulación?\n" +
        "Se borrará todo el estado guardado (estado_disco.ser) y el programa se cerrará.\n" +
        "Deberá volver a ejecutarlo.",
        "Confirmar Reinicio Completo",
        javax.swing.JOptionPane.YES_NO_OPTION,
        javax.swing.JOptionPane.WARNING_MESSAGE);

    if (confirm != javax.swing.JOptionPane.YES_OPTION) {
        return; // El usuario canceló
    }

    // 2. Llamamos al backend para borrar el archivo
    boolean exito = simulador.reiniciarEstado();

    if (exito) {
        // 3. Notificamos y cerramos
        javax.swing.JOptionPane.showMessageDialog(this, 
            "¡Reinicio completado!\nEl estado guardado fue eliminado. El programa se cerrará.", 
            "Reinicio Exitoso", 
            javax.swing.JOptionPane.INFORMATION_MESSAGE);

        // 4. Salir del programa
        System.exit(0); 

    } else {
        javax.swing.JOptionPane.showMessageDialog(this, 
            "Error: No se pudo eliminar el archivo 'estado_disco.ser'.\n" +
            "Revise los permisos de la carpeta.", 
            "Error de Reinicio", 
            javax.swing.JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnReiniciarActionPerformed

    private void btnGenerarAleatoriosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarAleatoriosActionPerformed
        // 1. Creamos un generador de números aleatorios
        Random rand = new Random();

        System.out.println("GUI: Encolando 20 solicitudes aleatorias...");

        // 2. Hacemos un bucle de 20
        for (int i = 0; i < 20; i++) {

            // 3. Creamos un nombre de archivo y un tamaño aleatorio
            String nombre = "archivo_aleatorio_" + i + ".txt";

            // Tamaño aleatorio (entre 1 y 5 bloques)
            int tamano = rand.nextInt(5) + 1;

            // 4. ¡IMPORTANTE! Encolamos la solicitud USANDO EL SIMULADOR
            // (Esto asegura que pasen por el Planificador de Disco)
            simulador.nuevaSolicitudUsuario(
                TipoOperacion.CREAR_ARCHIVO,
                nombre,
                tamano
            );
        }

        // 5. Notificamos al usuario y actualizamos la GUI
        javax.swing.JOptionPane.showMessageDialog(this,
            "¡20 solicitudes de archivos aleatorios fueron añadidas a la cola!",
            "Generación Aleatoria",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);

        // Actualizamos la GUI para ver las colas llenas
        actualizarGUICompleta();
    }//GEN-LAST:event_btnGenerarAleatoriosActionPerformed

    private void btnGenerarReporteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarReporteActionPerformed
        // 1. Llamamos al backend directamente
        boolean exito = simulador.getSistemaArchivos().generarReporteDeEstado();

        if (exito) {
            // 2. Notificamos al usuario
            javax.swing.JOptionPane.showMessageDialog(this,
                "¡Reporte 'reporte_disco.txt' generado exitosamente!\n" +
                "Busca el archivo en la carpeta de tu proyecto.",
                "Reporte Generado",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
        } else {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Error al generar el reporte. Revisa la consola.",
                "Error de Reporte",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnGenerarReporteActionPerformed

    private void btnEliminarDirectorioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarDirectorioActionPerformed
        // Esta operación es instantánea (pero compleja) y NO usa el planificador.

        // 1. Obtenemos el nombre del campo 'txtNombreArchivo'
        String nombre = txtNombreArchivo.getText();

        if (nombre.trim().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Debe ingresar un nombre de directorio en el campo 'Nombre'.",
                "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Advertencia al usuario (¡esto es destructivo!)
        int confirm = javax.swing.JOptionPane.showConfirmDialog(this,
            "¿Está seguro de que desea eliminar el directorio '" + nombre + "'?\n" +
            "¡TODOS los archivos y subdirectorios dentro de él se borrarán permanentemente!",
            "Confirmar Eliminación Recursiva",
            javax.swing.JOptionPane.YES_NO_OPTION,
            javax.swing.JOptionPane.WARNING_MESSAGE);

        if (confirm != javax.swing.JOptionPane.YES_OPTION) {
            return; // El usuario canceló
        }

        // 3. Llamamos al backend directamente
        boolean exito = simulador.getSistemaArchivos().eliminarDirectorio(nombre);

        if (exito) {
            // 4. Limpiamos campos y actualizamos la GUI
            txtNombreArchivo.setText("");
            actualizarGUICompleta(); // Para que desaparezca del JTree
        } else {
            javax.swing.JOptionPane.showMessageDialog(this,
                "No se pudo eliminar el directorio.",
                "Error al Eliminar Directorio", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnEliminarDirectorioActionPerformed

    private void btnCrearDirectorioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCrearDirectorioActionPerformed
        // Esta operación es instantánea y NO usa el planificador.

        // 1. Obtenemos el nombre del campo 'txtNombreArchivo'
        String nombre = txtNombreArchivo.getText();

        if (nombre.trim().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Debe ingresar un nombre en el campo 'Nombre' para el directorio.",
                "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Llamamos al backend directamente
        boolean exito = simulador.getSistemaArchivos().crearDirectorio(nombre);

        if (exito) {
            // 3. Limpiamos campos y actualizamos la GUI
            txtNombreArchivo.setText("");

            // ¡Forzamos la actualización INMEDIATA de la GUI!
            actualizarGUICompleta(); // Esto hará que aparezca en el JTree
        } else {
            // (El SistemaArchivos ya imprimió el error en consola)
            javax.swing.JOptionPane.showMessageDialog(this,
                "No se pudo crear el directorio (quizás el nombre ya existe).",
                "Error al Crear Directorio", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnCrearDirectorioActionPerformed

    private void btnRenombrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRenombrarActionPerformed
        // Esta operación es instantánea y NO usa el planificador.

        // 1. Obtenemos los nombres
        String nombreViejo = txtNombreArchivo.getText(); // El nombre actual
        String nombreNuevo = txtNuevoNombre.getText();   // El nombre deseado

        if (nombreViejo.trim().isEmpty() || nombreNuevo.trim().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Debe ingresar el nombre actual Y el nombre nuevo.",
                "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Llamamos al backend directamente
        boolean exito = simulador.getSistemaArchivos().renombrarNodo(nombreViejo, nombreNuevo);

        if (exito) {
            // 3. Limpiamos campos y actualizamos la GUI
            txtNombreArchivo.setText("");
            txtNuevoNombre.setText("");

            // ¡Forzamos la actualización INMEDIATA de la GUI!
            actualizarGUICompleta();
        } else {
            // (El SistemaArchivos ya imprimió el error en consola)
            javax.swing.JOptionPane.showMessageDialog(this,
                "No se pudo renombrar. Verifique los nombres (quizás ya existe).",
                "Error al Renombrar", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnRenombrarActionPerformed

    private void txtNuevoNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNuevoNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNuevoNombreActionPerformed

    private void btnLeerArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeerArchivoActionPerformed
        String nombre = txtNombreArchivo.getText();
        if (nombre == null || nombre.trim().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Debe ingresar un nombre de archivo para leer.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        simulador.nuevaSolicitudUsuario(
            TipoOperacion.LEER_ARCHIVO,
            nombre,
            0 // Tamaño no aplica
        );

        System.out.println("GUI: Solicitud para LEER '" + nombre + "' fue encolada.");
        actualizarGUICompleta();
    }//GEN-LAST:event_btnLeerArchivoActionPerformed

    private void btnEliminarArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarArchivoActionPerformed
        // 1. Obtener el nombre del archivo del JTree
        // (Forma simple: usar el mismo campo de texto de "Nombre")
        String nombre = txtNombreArchivo.getText();

        // (Forma avanzada: ...)
        // ...

        if (nombre == null || nombre.trim().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Debe ingresar un nombre de archivo para eliminar.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. ¡LA CLAVE! Pedir al simulador que ENCOLE la solicitud de borrado
        simulador.nuevaSolicitudUsuario(
            TipoOperacion.ELIMINAR_ARCHIVO,
            nombre,
            0 // El tamaño no importa para eliminar
        );

        // 3. Limpiar el campo
        txtNombreArchivo.setText("");

        System.out.println("GUI: Solicitud para ELIMINAR '" + nombre + "' fue encolada.");

        // --- ¡¡LÍNEA AÑADIDA!! ---
        // Actualizamos la GUI INMEDIATAMENTE para ver la cola "en espera"
        actualizarGUICompleta();
    }//GEN-LAST:event_btnEliminarArchivoActionPerformed

    private void btnCrearArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCrearArchivoActionPerformed
        // 1. Obtenemos los datos de la GUI
        // (Usa los nombres de variable que definiste)
        String nombre = txtNombreArchivo.getText();
        int tamano = (Integer) spinnerTamano.getValue();

        // 2. Validamos la entrada
        if (nombre == null || nombre.trim().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Debe ingresar un nombre de archivo.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        // (El JSpinner ya valida que el tamaño sea >= 1, si lo configuraste bien)

        // 3. ¡¡LA PARTE CLAVE!!
        // NO creamos el archivo aquí.
        // Le pedimos al SIMULADOR que "encole" la solicitud.

        simulador.nuevaSolicitudUsuario(
            TipoOperacion.CREAR_ARCHIVO,
            nombre,
            tamano
        );

        // 4. (Opcional) Limpiamos los campos
        txtNombreArchivo.setText("");
        spinnerTamano.setValue(1);

        System.out.println("GUI: Solicitud para CREAR '" + nombre + "' fue encolada.");

        // --- ¡¡LÍNEA AÑADIDA!! ---
        // Actualizamos la GUI INMEDIATAMENTE para ver la cola "en espera"
        actualizarGUICompleta();
    }//GEN-LAST:event_btnCrearArchivoActionPerformed

    private void txtNombreArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreArchivoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreArchivoActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* (El código 'try-catch' de look and feel se queda igual) */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            
            // ¡¡EL CONTENIDO DE RUN() ES EL QUE CAMBIA!!
            public void run() {
                
                // --- ¡¡INICIO DE LA LÓGICA DE SELECCIÓN!! ---
                
                Object[] options = {"Modo Administrador", "Modo Usuario"};
                
                int n = JOptionPane.showOptionDialog(
                    null,
                    "Bienvenido. Por favor, seleccione el modo de operación para iniciar:",
                    "Selección de Modo",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
                
                ModoUsuario modoElegido;

                if (n == JOptionPane.NO_OPTION) {
                    modoElegido = ModoUsuario.USUARIO;
                } else if (n == JOptionPane.YES_OPTION) {
                    modoElegido = ModoUsuario.ADMINISTRADOR;
                } else {
                    System.out.println("Selección cancelada. Saliendo.");
                    System.exit(0);
                    return;
                }
                
                // --- FIN DE LA LÓGICA DE SELECCIÓN ---
                
                
                // --- ¡¡INICIO DEL CÓDIGO ACTUALIZADO (PARA EL TAMAÑO)!! ---
                
                // 1. Creamos la VentanaPrincipal (sin hacerla visible)
                VentanaPrincipal ventana = new VentanaPrincipal(modoElegido);
                
                // 2. ¡LA MAGIA! Le decimos que se ajuste al tamaño
                //    de todos los componentes que tiene dentro.
                ventana.pack();
                
                // 3. (Recomendado) Para evitar que se vuelva a encoger
                ventana.setMinimumSize(ventana.getPreferredSize());
                
                // 4. La centramos en la pantalla
                ventana.setLocationRelativeTo(null);
                
                // 5. Ahora sí, la hacemos visible
                ventana.setVisible(true);
                
                // --- FIN DEL CÓDIGO ACTUALIZADO ---
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTree arbolArchivos;
    private javax.swing.JTextArea areaBuffer;
    private javax.swing.JTextArea areaColasProcesos;
    private javax.swing.JTextArea areaLogConsola;
    private javax.swing.JTextArea areaLogPlanificador;
    private javax.swing.JButton btnCrearArchivo;
    private javax.swing.JButton btnCrearDirectorio;
    private javax.swing.JButton btnEliminarArchivo;
    private javax.swing.JButton btnEliminarDirectorio;
    private javax.swing.JButton btnGenerarAleatorios;
    private javax.swing.JButton btnGenerarReporte;
    private javax.swing.JButton btnLeerArchivo;
    private javax.swing.JButton btnReiniciar;
    private javax.swing.JButton btnRenombrar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> comboPolitica;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblTamano;
    private javax.swing.JPanel panelAcciones;
    private javax.swing.JPanel panelControlesGeneral;
    private javax.swing.JPanel panelDisco;
    private javax.swing.JPanel panelSistema;
    private javax.swing.JRadioButton radioAdmin;
    private javax.swing.JRadioButton radioUsuario;
    private javax.swing.JScrollPane scrollArbol;
    private javax.swing.JScrollPane scrollBuffer;
    private javax.swing.JScrollPane scrollColas;
    private javax.swing.JScrollPane scrollDisco;
    private javax.swing.JScrollPane scrollTabla;
    private javax.swing.JSpinner spinnerTamano;
    private javax.swing.JSplitPane splitPaneDerecho;
    private javax.swing.JSplitPane splitPanePrincipal;
    private javax.swing.JSplitPane splitPaneVisuals;
    private javax.swing.JTable tablaAsignacion;
    private javax.swing.JTextField txtNombreArchivo;
    private javax.swing.JTextField txtNuevoNombre;
    // End of variables declaration//GEN-END:variables

// --- INICIO MÉTODOS DE ACTUALIZACIÓN DE GUI ---

/**
 * Método "mágico" que lee todo el estado del Simulador
 * y lo dibuja en los componentes de la GUI.
 */
private void actualizarGUICompleta() {
    System.out.println("GUI: Actualizando vistas...");
    
    // 1. Actualizar el Árbol (JTree)
    actualizarArbol();
    
    // 2. Actualizar la Tabla de Asignación (JTable)
    actualizarTablaAsignacion();
    
    // 3. Actualizar la Vista del Disco (JPanel)
    actualizarVistaDisco();
    
    // 4. Actualizar la Vista de Colas (JTextArea)
    actualizarVistaColas();
    actualizarVistaBuffer();
}

/**
 * ¡NUEVO MÉTODO AYUDANTE RECURSIVO!
 * Sincroniza los nodos del JTree (Swing) para que coincidan
 * con el estado del backend, sin destruir los nodos existentes.
 */
/**
 * ¡NUEVO MÉTODO AYUDANTE RECURSIVO!
 * (Versión con parche anti-NPE)
 */
private void sincronizarNodos(Directorio dirBackend, DefaultMutableTreeNode nodoSwingPadre) {

    if (dirBackend == null) {
        return; 
    }

    // ... (El código de los pasos 1, 2 y 3 no cambia) ...
    ListaEnlazada<NodoArbol> hijosBackend = dirBackend.getHijos();
    Map<String, DefaultMutableTreeNode> hijosSwingMap = new HashMap<>();
    Enumeration hijosEnum = nodoSwingPadre.children();
    while (hijosEnum.hasMoreElements()) {
        DefaultMutableTreeNode nodoHijo = (DefaultMutableTreeNode) hijosEnum.nextElement();
        Object objetoBackend = nodoHijo.getUserObject();
        if (objetoBackend instanceof NodoArbol) {
            hijosSwingMap.put(((NodoArbol) objetoBackend).getNombre(), nodoHijo);
        }
    }
    NodoLista<NodoArbol> nodoActual = (hijosBackend != null) ? hijosBackend.getInicio() : null;
    
    while (nodoActual != null) {
        NodoArbol hijoBackend = nodoActual.getDato();
        if (hijoBackend == null) {
            nodoActual = nodoActual.getSiguiente();
            continue;
        }
        String nombreHijo = hijoBackend.getNombre();
        DefaultMutableTreeNode nodoHijoSwing = hijosSwingMap.get(nombreHijo);

        if (nodoHijoSwing == null) {
            // -- CASO 1: No existe (Crear) --
            DefaultMutableTreeNode nuevoNodoSwing = new DefaultMutableTreeNode(hijoBackend);
            if (hijoBackend instanceof Directorio) {
                sincronizarNodos((Directorio) hijoBackend, nuevoNodoSwing);
            }
            // ¡Esta línea es una notificación! Es perfecta.
            modeloArbol.insertNodeInto(nuevoNodoSwing, nodoSwingPadre, nodoSwingPadre.getChildCount());
            
        } else {
            // -- CASO 2: Sí existe (Actualizar) --
            nodoHijoSwing.setUserObject(hijoBackend); 
            
            // --- ¡¡LÍNEA AÑADIDA!! ---
            // Le decimos al modelo que ESTE nodo cambió (ej. su nombre)
            modeloArbol.nodeChanged(nodoHijoSwing); 
            // --- FIN LÍNEA AÑADIDA ---
            
            if (hijoBackend instanceof Directorio) {
                sincronizarNodos((Directorio) hijoBackend, nodoHijoSwing);
            }
            hijosSwingMap.remove(nombreHijo);
        }
        nodoActual = nodoActual.getSiguiente();
    }

    // -- CASO 3: Eliminar --
    for (DefaultMutableTreeNode nodoAFlushing : hijosSwingMap.values()) {
        // ¡Esta línea es una notificación! Es perfecta.
        modeloArbol.removeNodeFromParent(nodoAFlushing);
    }
}
/**
 * Tarea: Leer el Árbol del Simulador y dibujarlo en el JTree
 * ¡VERSIÓN FINAL!
 * Sincroniza el árbol en lugar de recrearlo, para
 * mantener el estado de expansión.
 */
private void actualizarArbol() {
    
    // Si ya estamos actualizando, no hagas nada
    if (this.estaActualizandoArbol) return;

    // --- ¡LEVANTAMOS EL SEMÁFORO! ---
    this.estaActualizandoArbol = true;
    
    // System.out.println("--- ¡NUEVO ACTUALIZAR ARBOL (SYNC) EJECUTADO! ---");
    
    try {
        Directorio raizBackend = simulador.getSistemaArchivos().getRaiz();
        if (raizBackend == null) return;
        
        this.raizArbol.setUserObject(raizBackend);
        
        // Llamamos al ayudante recursivo.
        // Este método AHORA se encarga de TODAS las notificaciones.
        sincronizarNodos(raizBackend, this.raizArbol);
        
        // --- ¡¡LÍNEA ELIMINADA!! ---
        // this.modeloArbol.nodeStructureChanged(this.raizArbol); // <-- ¡BORRADA!

    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        // --- ¡BAJAMOS EL SEMÁFORO! ---
        this.estaActualizandoArbol = false;
    }
}

/**
 * Tarea: Recorrer el árbol, encontrar Archivos y listarlos en la JTable
 */
private void actualizarTablaAsignacion() {
    
    // 1. Obtener el modelo de la JTable
    DefaultTableModel modeloTabla = (DefaultTableModel) tablaAsignacion.getModel();

    // 2. Limpiar la tabla de datos anteriores
    modeloTabla.setRowCount(0);

    // 3. Obtener la raíz del sistema de archivos
    Directorio raiz = simulador.getSistemaArchivos().getRaiz();

    // 4. Llamar a la función recursiva para llenar la tabla
    try {
        if (raiz != null) {
            llenarTablaRecursivo(raiz, modeloTabla);
        }
    } catch (Exception e) {
        // (Manejo de errores por si algo falla durante la actualización)
        e.printStackTrace();
    }
}

/**
 * Tarea: Recorrer el DiscoSD y pintar los bloques en el panelDisco
 */
private void actualizarVistaDisco() {
    
    
    // 1. Limpiamos el panel de cualquier bloque viejo
    panelDisco.removeAll();
    
    try {
        // 2. Obtenemos el "backend" del disco
        DiscoSD disco = simulador.getSistemaArchivos().getDisco();
        
        // 3. Recorremos CADA bloque del disco
        for (int i = 0; i < disco.getNumBloquesTotal(); i++) {
            Bloque bloqueActual = disco.getBloque(i);
            
            // 4. Creamos un "cuadrito" (JPanel) para representar el bloque
            JPanel panelBloque = new JPanel();
            
            // 5. Le ponemos un borde para que se vea la cuadrícula
            panelBloque.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            
            // 6. ¡La lógica de pintado!
            if (bloqueActual.estaOcupado()) {
                // Si está ocupado, lo pintamos de rojo
                panelBloque.setBackground(Color.RED);
                
                // (Opcional) Mostrar a quién pertenece
                String tooltip = "Bloque: " + i + " | Archivo: " + bloqueActual.getArchivoPropietario().getNombre();
                panelBloque.setToolTipText(tooltip);
                
            } else {
                // Si está libre, lo pintamos de verde
                panelBloque.setBackground(Color.GREEN);
            }
            
            // 7. Añadimos el "cuadrito" al panel principal
            panelDisco.add(panelBloque);
        }
    } catch (Exception e) {
        // (Manejo de errores por si algo falla durante la actualización)
        e.printStackTrace();
    }
    
    // 8. ¡MUY IMPORTANTE! Forzamos a la GUI a redibujar el panel
    panelDisco.revalidate();
    panelDisco.repaint();
}

/**
 * Tarea: Leer las colas del Simulador y mostrarlas en el JTextArea
 */
private void actualizarVistaColas() {
    
    // 1. Limpiamos el área de texto
    areaColasProcesos.setText(""); // Asumiendo que se llama así

    try {
        // --- 2. Mostrar la Cola de Procesos (Listos) ---
        areaColasProcesos.append("--- Cola de Procesos (Listos) ---\n");
        Cola<Proceso> colaProcesos = simulador.getColaDeProcesos();
        
        if (colaProcesos.estaVacia()) {
            areaColasProcesos.append("(Vacía)\n");
        } else {
            // Recorremos la lista interna para mostrarla
            NodoLista<Proceso> nodoP = colaProcesos.getListaInterna().getInicio();
            while (nodoP != null) {
                Proceso p = nodoP.getDato();
                areaColasProcesos.append(
                    "PID: " + p.getPid() + " (" + p.getSolicitud().getTipo() + ")\n"
                );
                nodoP = nodoP.getSiguiente();
            }
        }

        // --- 3. Mostrar la Cola de E/S (Bloqueados) ---
        areaColasProcesos.append("\n--- Cola de E/S (Disco) ---\n");
        Cola<SolicitudIO> colaIO = simulador.getColaDeIO();

        if (colaIO.estaVacia()) {
            areaColasProcesos.append("(Vacía)\n");
        } else {
            // Recorremos la lista interna
            NodoLista<SolicitudIO> nodoIO = colaIO.getListaInterna().getInicio();
            while (nodoIO != null) {
                SolicitudIO s = nodoIO.getDato();
                areaColasProcesos.append(
                    s.getTipo() + " - " + s.getNombreArchivo() + "\n"
                );
                nodoIO = nodoIO.getSiguiente();
            }
        }
        
    } catch (Exception e) {
        areaColasProcesos.setText("Error al actualizar colas:\n" + e.getMessage());
        e.printStackTrace();
    }
}

// --- FIN MÉTODOS DE ACTUALIZACIÓN DE GUI ---

/**
 * Método auxiliar RECURSIVO para recorrer el árbol de directorios
 * y añadir los archivos encontrados a la tabla.
 */
private void llenarTablaRecursivo(Directorio directorioActual, DefaultTableModel modeloTabla) {
    
    // 1. Obtener la lista de hijos del directorio actual
    ListaEnlazada<NodoArbol> hijos = directorioActual.getHijos();
    if (hijos == null || hijos.estaVacia()) {
        return; // No hay hijos, termina la recursión
    }

    // 2. Recorrer la lista enlazada de hijos
    NodoLista<NodoArbol> nodoActual = hijos.getInicio();
    while (nodoActual != null) {
        
        NodoArbol nodo = nodoActual.getDato();

        // 3. Comprobar el tipo de nodo
        if (nodo instanceof Archivo) {
            // Si es un Archivo, lo añadimos a la tabla
            Archivo archivo = (Archivo) nodo;
            
            // 4. Añadir la fila
            modeloTabla.addRow(new Object[]{
                archivo.getNombre(),
                archivo.getTamanoEnBloques(),
                archivo.getIdPrimerBloque()
                // (El proyecto también pedía el Proceso que lo creó[cite: 21],
                //  podríamos añadirlo después si guardamos esa info)
            });
            
        } else if (nodo instanceof Directorio) {
            // Si es un Directorio, llamamos recursivamente
            llenarTablaRecursivo((Directorio) nodo, modeloTabla);
        }
        
        // 5. Avanzar al siguiente hijo
        nodoActual = nodoActual.getSiguiente();
    }
}

/**
 * Método auxiliar RECURSIVO para construir el árbol visual (JTree)
 * a partir de nuestro árbol de directorios (backend).
 */

/**
 * Revisa el modo actual del simulador y habilita o deshabilita
 * los controles de la GUI (botones) según los permisos.
 */
private void actualizarPermisosGUI() {
    ModoUsuario modo = simulador.getModo();
    boolean esAdmin = (modo == ModoUsuario.ADMINISTRADOR);
    
    // Permisos de ESCRITURA (Solo Admin)
    btnCrearArchivo.setEnabled(esAdmin);
    btnEliminarArchivo.setEnabled(esAdmin);
    
    // --- ¡NUEVAS LÍNEAS! ---
    btnRenombrar.setEnabled(esAdmin);
    txtNuevoNombre.setEnabled(esAdmin);
    btnCrearDirectorio.setEnabled(esAdmin);
    btnEliminarDirectorio.setEnabled(esAdmin);
    btnGenerarReporte.setEnabled(esAdmin);
    btnGenerarAleatorios.setEnabled(esAdmin);
    btnReiniciar.setEnabled(esAdmin);
    // --- FIN LÍNEAS NUEVAS ---
    
    // Permisos de LECTURA (Todos)
    btnLeerArchivo.setEnabled(true);
}
/**
 * Tarea: Leer el BufferCache y mostrar su estado en el JTextArea.
 */
private void actualizarVistaBuffer() {
    areaBuffer.setText(""); // Limpiar el área

    try {
        // (Necesitamos una forma de acceder al buffer desde el Simulador)
        // (Iremos a Simulador y SistemaArchivos para añadir este getter)

        ListaEnlazada<Bloque> cache = simulador.getSistemaArchivos().getBufferCache().getCacheInterno();

        areaBuffer.append("--- Buffer (FIFO) ---\n");
        areaBuffer.append("(Inicio)\n");

        if (cache.estaVacia()) {
            areaBuffer.append("(Vacío)\n");
        } else {
            NodoLista<Bloque> actual = cache.getInicio();
            while (actual != null) {
                areaBuffer.append("Bloque " + actual.getDato().getId() + "\n");
                actual = actual.getSiguiente();
            }
        }
        areaBuffer.append("(Fin)\n");

    } catch (Exception e) {
        areaBuffer.setText("Error al leer buffer.");
    }
}
// --- INICIO DE CLASE INTERNA PARA RENDERER ---
// Pega esto dentro de VentanaPrincipal.java, pero al final

 /**
 * ¡Implementación de la interfaz ILogger!
 * Recibe un mensaje del backend y actúa como un "router":
 * - Si el mensaje es del BUFFER, va a la consola de eventos.
 * - Si es del PLANIFICADOR, va a la consola del planificador.
 */
@Override
public void log(String mensaje) {

    if (mensaje == null) return;

    // ¡EL ROUTER!
    if (mensaje.startsWith("BUFFER:")) {

        // --- Lo envía a la consola 1 ---
        areaLogConsola.append(mensaje + "\n");
        areaLogConsola.setCaretPosition(areaLogConsola.getDocument().getLength());

    } else if (mensaje.startsWith("PLANIFICADOR:")) {

        // --- Lo envía a la consola 2 ---
        areaLogPlanificador.append(mensaje + "\n");
        areaLogPlanificador.setCaretPosition(areaLogPlanificador.getDocument().getLength());

    } else {

        // --- Fallback (para mensajes desconocidos) ---
        areaLogConsola.append(mensaje + "\n");
        areaLogConsola.setCaretPosition(areaLogConsola.getDocument().getLength());
    }
}

class MyTreeCellRenderer extends DefaultTreeCellRenderer {

    // Cargamos los iconos estándar de Java
    Icon dirIcono = UIManager.getIcon("Tree.closedIcon");
    Icon archIcono = UIManager.getIcon("Tree.leafIcon");

    @Override
    public Component getTreeCellRendererComponent(javax.swing.JTree tree,
            Object value, boolean sel, boolean expanded, boolean leaf,
            int row, boolean hasFocus) {
        
        // 1. Llama al método original para que haga el trabajo (pintar, etc.)
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        // 2. Obtenemos el nodo de la GUI
        javax.swing.tree.DefaultMutableTreeNode nodoSwing = (javax.swing.tree.DefaultMutableTreeNode) value;
        
        // 3. Obtenemos nuestro objeto de BACKEND
        Object objetoBackend = nodoSwing.getUserObject();

        // 4. Decidimos el icono
        if (objetoBackend instanceof Directorio) {
            setIcon(dirIcono);
        } else if (objetoBackend instanceof Archivo) {
            setIcon(archIcono);
        }

        return this;
    }
}
// --- FIN DE CLASE INTERNA ---
}
