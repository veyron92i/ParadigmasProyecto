/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cafeteria.Cliente;

import Cafeteria.Rmi.EstadoCafeteria;
import Cafeteria.Rmi.IRemotaCafeteria;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Cliente Remoto (Parte 2 de la práctica).
 * Se conecta vía RMI al servidor para monitorizar el estado.
 */
public class ClienteRMI extends JFrame {
    
    // Referencia al objeto remoto del servidor
    private IRemotaCafeteria servidor;
    
    // Timer para actualizar la GUI cada segundo
    private final Timer timer;
    
    // Estado local del botón
    private boolean pausado = false; 
    
    // Componentes de la GUI
    private JButton btnPausa;
    private JLabel lblEstadoConexion;
    
    // Labels para los datos (Similares a InterfazPrincipal pero solo lectura)
    private JLabel lblParque, lblEntrada, lblCola;
    private JLabel lblMostradorCli, lblMostradorVend, lblMostradorCafe, lblMostradorRosq;
    private JLabel lblCajaCli, lblCajaDinero;
    private JLabel lblArea;
    private JLabel lblCocina;
    private JLabel lblDespensaCafe, lblDespensaRosq, lblDespensaCoc, lblDespensaVend;
    private JLabel lblSalaCoc, lblSalaVend;
    private JLabel lblFinalizados;
    
    private static final DecimalFormat FORMATO_DINERO = new DecimalFormat("0.00€");

    public ClienteRMI() {
        super("Monitor Remoto - Cafetería Distribuida");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // 1. Inicializar la interfaz gráfica
        initGui();
        
        // 2. Conectar al servidor RMI
        conectarServidor();
        
        // 3. Iniciar el Timer (1000 ms = 1 segundo)
        timer = new Timer(1000, e -> actualizarDatos());
        timer.start();
        
        // Configuración ventana
        setSize(900, 600);
        setLocationRelativeTo(null); // Centrar en pantalla
    }
    
    private void initGui() {
        // --- Panel Superior (Control y Estado) ---
        JPanel panelSup = new JPanel(new GridLayout(1, 2, 10, 10));
        
        btnPausa = new JButton("Pausar / Reanudar");
        btnPausa.addActionListener(this::accionBotonPausa);
        
        lblEstadoConexion = new JLabel("Conectando...", SwingConstants.CENTER);
        lblEstadoConexion.setOpaque(true);
        lblEstadoConexion.setBackground(Color.YELLOW);
        
        panelSup.add(btnPausa);
        panelSup.add(lblEstadoConexion);
        panelSup.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        add(panelSup, BorderLayout.NORTH);
        
        // --- Panel Central (Datos) ---
        JPanel panelDatos = new JPanel(new GridLayout(3, 3, 10, 10));
        panelDatos.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        // Inicializar Labels con guiones
        lblParque = new JLabel("-"); lblEntrada = new JLabel("-"); lblCola = new JLabel("-");
        lblMostradorCli = new JLabel("-"); lblMostradorVend = new JLabel("-"); 
        lblMostradorCafe = new JLabel("-"); lblMostradorRosq = new JLabel("-");
        lblCajaCli = new JLabel("-"); lblCajaDinero = new JLabel("-");
        lblArea = new JLabel("-"); lblCocina = new JLabel("-");
        lblDespensaCafe = new JLabel("-"); lblDespensaRosq = new JLabel("-"); 
        lblDespensaCoc = new JLabel("-"); lblDespensaVend = new JLabel("-");
        lblSalaCoc = new JLabel("-"); lblSalaVend = new JLabel("-");
        lblFinalizados = new JLabel("-");
        
        // Añadir las secciones al panel
        panelDatos.add(crearSeccion("Exterior", "Parque", lblParque, "Entrada", lblEntrada, "Cola Espera", lblCola));
        
        panelDatos.add(crearSeccion("Mostrador", "Clientes", lblMostradorCli, "Vendedores", lblMostradorVend, 
                "Cafés Stock", lblMostradorCafe, "Rosq. Stock", lblMostradorRosq));
        
        panelDatos.add(crearSeccion("Caja", "Clientes", lblCajaCli, "Recaudación", lblCajaDinero));
        
        panelDatos.add(crearSeccion("Área Consumición", "Clientes", lblArea));
        
        panelDatos.add(crearSeccion("Cocina y Despensa", "Cocineros Cocina", lblCocina, "Café Despensa", lblDespensaCafe, 
                "Rosq. Despensa", lblDespensaRosq, "Coci. en Desp.", lblDespensaCoc, "Vend. en Desp.", lblDespensaVend));
        
        panelDatos.add(crearSeccion("Sala Descanso", "Cocineros", lblSalaCoc, "Vendedores", lblSalaVend));
        
        panelDatos.add(crearSeccion("Estadísticas", "Clientes Finalizados", lblFinalizados));
        
        // Relleno para huecos vacíos del grid
        panelDatos.add(new JPanel()); 
        panelDatos.add(new JPanel());
        
        add(panelDatos, BorderLayout.CENTER);
    }
    
    /**
     * Método auxiliar para crear recuadros con bordes y títulos.
     */
    private JPanel crearSeccion(String titulo, Object... pares) {
        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        p.setBorder(BorderFactory.createTitledBorder(titulo));
        for (int i = 0; i < pares.length; i+=2) {
            p.add(new JLabel(pares[i].toString()));
            p.add((JLabel)pares[i+1]);
        }
        return p;
    }
    
    /**
     * Intenta conectar con el RMI Registry local.
     */
    private void conectarServidor() {
        try {
            // Puerto 1099 es el estándar de RMI
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            // "CafeteriaRMI" debe coincidir con lo puesto en ServidorRMI.java
            servidor = (IRemotaCafeteria) registry.lookup("CafeteriaRMI");
            
            lblEstadoConexion.setText("CONECTADO");
            lblEstadoConexion.setBackground(Color.GREEN);
        } catch (Exception e) {
            lblEstadoConexion.setText("DESCONECTADO");
            lblEstadoConexion.setBackground(Color.RED);
            // No mostramos popup al inicio para no molestar, el label rojo avisa
        }
    }
    
    /**
     * Se ejecuta cada segundo por el Timer.
     * Pide el EstadoCafeteria y actualiza los textos.
     */
    private void actualizarDatos() {
        if (servidor == null) {
            // Reintento silencioso de conexión si se cayó
            conectarServidor();
            return;
        }
        
        try {
            // === LLAMADA REMOTA ===
            EstadoCafeteria estado = servidor.obtenerEstado();
            
            // Actualizar GUI
            lblParque.setText(String.valueOf(estado.getClientesParque()));
            lblEntrada.setText(String.valueOf(estado.getClientesEntrada()));
            lblCola.setText(String.valueOf(estado.getClientesColaEntrada()));
            
            lblMostradorCli.setText(String.valueOf(estado.getClientesMostrador()));
            lblMostradorVend.setText(String.valueOf(estado.getVendedoresMostrador()));
            lblMostradorCafe.setText(String.valueOf(estado.getMostradorCafe()));
            lblMostradorRosq.setText(String.valueOf(estado.getMostradorRosquillas()));
            
            lblCajaCli.setText(String.valueOf(estado.getClientesCaja()));
            lblCajaDinero.setText(FORMATO_DINERO.format(estado.getRecaudacionTotal()));
            
            lblArea.setText(String.valueOf(estado.getClientesArea()));
            lblCocina.setText(String.valueOf(estado.getCocinerosCocina()));
            
            lblDespensaCafe.setText(String.valueOf(estado.getDespensaCafe()));
            lblDespensaRosq.setText(String.valueOf(estado.getDespensaRosquillas()));
            lblDespensaCoc.setText(String.valueOf(estado.getCocinerosDespensa()));
            lblDespensaVend.setText(String.valueOf(estado.getVendedoresDespensa()));
            
            lblSalaCoc.setText(String.valueOf(estado.getCocinerosSala()));
            lblSalaVend.setText(String.valueOf(estado.getVendedoresSala()));
            lblFinalizados.setText(String.valueOf(estado.getClientesFinalizados()));
            
            // Si la llamada tuvo éxito, aseguramos label verde
            lblEstadoConexion.setText("CONECTADO");
            lblEstadoConexion.setBackground(Color.GREEN);
            
        } catch (Exception e) {
            System.err.println("ERROR RMI: ");
            e.printStackTrace();
            lblEstadoConexion.setText("ERROR CONEXIÓN");
            lblEstadoConexion.setBackground(Color.RED);
            servidor = null; // Forzar reconexión en el siguiente tick
        }
    }
    
    /**
     * Gestiona el botón de Pausa/Reanudar remoto.
     */
    private void accionBotonPausa(ActionEvent e) {
        if (servidor == null) {
            JOptionPane.showMessageDialog(this, "No hay conexión con el servidor.");
            return;
        }
        try {
            if (!pausado) {
                servidor.pausar();
                pausado = true;
                btnPausa.setText("Reanudar Sistema"); 
            } else {
                servidor.reanudar();
                pausado = false;
                btnPausa.setText("Pausar Sistema");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al invocar control remoto: " + ex.getMessage());
            lblEstadoConexion.setText("ERROR");
            lblEstadoConexion.setBackground(Color.RED);
            servidor = null;
        }
    }

    /**
     * MAIN del Cliente RMI.
     * Se ejecuta de forma independiente al Servidor.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClienteRMI().setVisible(true));
    }
}
