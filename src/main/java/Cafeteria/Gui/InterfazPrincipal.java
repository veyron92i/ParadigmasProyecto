package Cafeteria.Gui;

import Cafeteria.Cafeteria;
import Cafeteria.Rmi.EstadoCafeteria;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Interfaz gráfica principal de la simulación de la cafetería.
 * Muestra contadores en tiempo real y permite pausar o reanudar el sistema.
 */
public class InterfazPrincipal extends JFrame {

    private final Cafeteria cafeteria;
    private final Timer refrescoEstado;

    private final JButton btnIniciar;
    private final JButton btnPausa;
    private final JLabel lblEstadoEjecucion;

    private final JLabel lblParque;
    private final JLabel lblEntrada;
    private final JLabel lblColaEntrada;

    private final JLabel lblMostradorClientes;
    private final JLabel lblMostradorVendedores;
    private final JLabel lblMostradorCafe;
    private final JLabel lblMostradorRosquillas;

    private final JLabel lblCajaClientes;
    private final JLabel lblCajaRecaudacion;

    private final JLabel lblAreaConsumicion;

    private final JLabel lblCocina;
    private final JLabel lblDespensaCafe;
    private final JLabel lblDespensaRosquillas;
    private final JLabel lblDespensaCocineros;
    private final JLabel lblDespensaVendedores;

    private final JLabel lblDescansoCocineros;
    private final JLabel lblDescansoVendedores;
    private final JLabel lblClientesFinalizados;

    private boolean iniciado = false;

    private static final DecimalFormat FORMATO_DINERO = new DecimalFormat("0.00€");

    public InterfazPrincipal() {
        super("Simulación Cafetería - Paradigmas de Programación");

        this.cafeteria = new Cafeteria();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Botones de control
        btnIniciar = new JButton("Iniciar simulación");
        btnIniciar.addActionListener(this::iniciarSimulacion);

        btnPausa = new JButton("Pausar");
        btnPausa.setEnabled(false);
        btnPausa.addActionListener(this::alternarPausa);

        lblEstadoEjecucion = new JLabel("Detenido", SwingConstants.CENTER);
        lblEstadoEjecucion.setOpaque(true);
        lblEstadoEjecucion.setBackground(new Color(240, 240, 240));
        lblEstadoEjecucion.setForeground(Color.RED);

        // Indicadores
        lblParque = crearLabelValor();
        lblEntrada = crearLabelValor();
        lblColaEntrada = crearLabelValor();

        lblMostradorClientes = crearLabelValor();
        lblMostradorVendedores = crearLabelValor();
        lblMostradorCafe = crearLabelValor();
        lblMostradorRosquillas = crearLabelValor();

        lblCajaClientes = crearLabelValor();
        lblCajaRecaudacion = crearLabelValor();
        lblCajaRecaudacion.setText("0.00€");

        lblAreaConsumicion = crearLabelValor();

        lblCocina = crearLabelValor();
        lblDespensaCafe = crearLabelValor();
        lblDespensaRosquillas = crearLabelValor();
        lblDespensaCocineros = crearLabelValor();
        lblDespensaVendedores = crearLabelValor();

        lblDescansoCocineros = crearLabelValor();
        lblDescansoVendedores = crearLabelValor();
        lblClientesFinalizados = crearLabelValor();

        // Composición de la interfaz
        add(crearPanelControles(), BorderLayout.NORTH);
        add(crearPanelIndicadores(), BorderLayout.CENTER);

        // Timer de refresco (1 segundo)
        refrescoEstado = new Timer(1000, this::refrescarEstado);

        setPreferredSize(new Dimension(900, 600));
        pack();
        setLocationRelativeTo(null);
    }

    private JLabel crearLabelValor() {
        JLabel lbl = new JLabel("0", SwingConstants.CENTER);
        return lbl;
    }

    private JPanel crearPanelControles() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(btnIniciar);
        panel.add(btnPausa);
        panel.add(lblEstadoEjecucion);
        return panel;
    }

    private JPanel crearPanelIndicadores() {
        JPanel panel = new JPanel(new GridLayout(3, 3, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(crearSeccion("Parque y entrada",
                new Etiqueta("Clientes en parque", lblParque),
                new Etiqueta("Clientes en entrada", lblEntrada),
                new Etiqueta("Clientes esperando aforo", lblColaEntrada)));

        panel.add(crearSeccion("Mostrador",
                new Etiqueta("Clientes en mostrador", lblMostradorClientes),
                new Etiqueta("Vendedores en mostrador", lblMostradorVendedores),
                new Etiqueta("Cafés en mostrador", lblMostradorCafe),
                new Etiqueta("Rosquillas en mostrador", lblMostradorRosquillas)));

        panel.add(crearSeccion("Caja",
                new Etiqueta("Clientes en caja", lblCajaClientes),
                new Etiqueta("Recaudación", lblCajaRecaudacion)));

        panel.add(crearSeccion("Área de consumición",
                new Etiqueta("Clientes consumiendo", lblAreaConsumicion)));

        panel.add(crearSeccion("Cocina y despensa",
                new Etiqueta("Cocineros en cocina", lblCocina),
                new Etiqueta("Café en despensa", lblDespensaCafe),
                new Etiqueta("Rosquillas en despensa", lblDespensaRosquillas),
                new Etiqueta("Cocineros en despensa", lblDespensaCocineros),
                new Etiqueta("Vendedores en despensa", lblDespensaVendedores)));

        panel.add(crearSeccion("Sala de descanso",
                new Etiqueta("Cocineros descansando", lblDescansoCocineros),
                new Etiqueta("Vendedores descansando", lblDescansoVendedores)));

        panel.add(crearSeccion("Progreso simulación",
                new Etiqueta("Clientes finalizados", lblClientesFinalizados)));

        // Celdas vacías si sobran huecos
        // (GridLayout(3,3): ya llevamos 7 secciones, quedarán 2 vacías)
        panel.add(new JPanel());
        panel.add(new JPanel());

        return panel;
    }

    private JPanel crearSeccion(String titulo, Etiqueta... etiquetas) {
        JPanel panel = new JPanel(new GridLayout(etiquetas.length + 1, 1, 5, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JLabel tituloLabel = new JLabel(titulo, SwingConstants.CENTER);
        tituloLabel.setOpaque(true);
        tituloLabel.setBackground(new Color(230, 230, 230));
        panel.add(tituloLabel);

        for (Etiqueta e : etiquetas) {
            JPanel fila = new JPanel(new GridLayout(1, 2, 5, 5));
            fila.add(new JLabel(e.texto()));
            fila.add(e.valor());
            panel.add(fila);
        }
        return panel;
    }

    private void iniciarSimulacion(ActionEvent evt) {
        if (iniciado) {
            return;
        }
        iniciado = true;
        btnIniciar.setEnabled(false);
        btnPausa.setEnabled(true);
        lblEstadoEjecucion.setText("En marcha");
        lblEstadoEjecucion.setForeground(Color.GREEN);

        cafeteria.iniciar();
        refrescoEstado.start();
    }

    private void alternarPausa(ActionEvent evt) {
        if (!iniciado) {
            return;
        }

        if (cafeteria.getPaso().estaCerrado()) {
            cafeteria.getPaso().abrir();
            btnPausa.setText("Pausar");
            lblEstadoEjecucion.setText("En marcha");
            lblEstadoEjecucion.setForeground(Color.GREEN);
        } else {
            cafeteria.getPaso().cerrar();
            btnPausa.setText("Reanudar");
            lblEstadoEjecucion.setText("En pausa");
            lblEstadoEjecucion.setForeground(Color.ORANGE);
        }
    }

    private void refrescarEstado(ActionEvent evt) {
        EstadoCafeteria estado = cafeteria.generarEstado();

        lblParque.setText(String.valueOf(estado.getClientesParque()));
        lblEntrada.setText(String.valueOf(estado.getClientesEntrada()));
        lblColaEntrada.setText(String.valueOf(estado.getClientesColaEntrada()));

        lblMostradorClientes.setText(String.valueOf(estado.getClientesMostrador()));
        lblMostradorVendedores.setText(String.valueOf(estado.getVendedoresMostrador()));
        lblMostradorCafe.setText(String.valueOf(estado.getMostradorCafe()));
        lblMostradorRosquillas.setText(String.valueOf(estado.getMostradorRosquillas()));

        lblCajaClientes.setText(String.valueOf(estado.getClientesCaja()));
        lblCajaRecaudacion.setText(FORMATO_DINERO.format(estado.getRecaudacionTotal()));

        lblAreaConsumicion.setText(String.valueOf(estado.getClientesArea()));

        lblCocina.setText(String.valueOf(estado.getCocinerosCocina()));
        lblDespensaCafe.setText(String.valueOf(estado.getDespensaCafe()));
        lblDespensaRosquillas.setText(String.valueOf(estado.getDespensaRosquillas()));
        lblDespensaCocineros.setText(String.valueOf(estado.getCocinerosDespensa()));
        lblDespensaVendedores.setText(String.valueOf(estado.getVendedoresDespensa()));

        lblDescansoCocineros.setText(String.valueOf(estado.getCocinerosSala()));
        lblDescansoVendedores.setText(String.valueOf(estado.getVendedoresSala()));
        lblClientesFinalizados.setText(String.valueOf(estado.getClientesFinalizados()));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InterfazPrincipal().setVisible(true));
    }

    private record Etiqueta(String texto, JLabel valor) {}
}
