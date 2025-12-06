package Cafeteria;

import Cafeteria.Actores.Cliente;
import Cafeteria.Actores.Cocinero;
import Cafeteria.Actores.Vendedor;
import Cafeteria.Control.Paso;
import Cafeteria.Log.LogCafeteria;
import Cafeteria.Recursos.AreaConsumicion;
import Cafeteria.Recursos.Caja;
import Cafeteria.Recursos.Cocina;
import Cafeteria.Recursos.Despensa;
import Cafeteria.Recursos.EntradaCafeteria;
import Cafeteria.Recursos.Mostrador;
import Cafeteria.Recursos.Parque;
import Cafeteria.Recursos.SalaDescanso;
import Cafeteria.Rmi.EstadoCafeteria;
import Cafeteria.Rmi.ServidorRMI;

public class Cafeteria {

    // ==================== RECURSOS COMPARTIDOS ====================
    private final Despensa despensa;
    private final Mostrador mostrador;
    private final Cocina cocina;
    private final Caja caja;
    private final AreaConsumicion areaConsumicion;
    private final SalaDescanso salaDescanso;
    private final Parque parque;
    private final EntradaCafeteria entradaCafeteria;

    // ==================== SERVICIOS ====================
    private final LogCafeteria log;
    private final Paso paso;

    // ==================== HILOS GENERADORES ====================
    private Thread generadorClientes;
    private Thread generadorVendedores;
    private Thread generadorCocineros;

    // ==================== CONSTANTES ====================
    public static final int NUM_CLIENTES = 8000;
    public static final int NUM_VENDEDORES = 500;
    public static final int NUM_COCINEROS = 500;

    // ==================== ESTADO SIMULACIÓN ====================
    private volatile int clientesFinalizados = 0;


    // ==================== CONSTRUCTOR ====================
    public Cafeteria() {
        this.log = new LogCafeteria("evolucion_cafeteria.txt");
        this.paso = new Paso();

        this.despensa = new Despensa(50, 50, log);
        this.mostrador = new Mostrador(5, 20, log);
        this.cocina = new Cocina(100, log);
        this.caja = new Caja(10, log);
        this.areaConsumicion = new AreaConsumicion(30, log);

        this.salaDescanso = new SalaDescanso(log);
        this.parque = new Parque(log);
        this.entradaCafeteria = new EntradaCafeteria(20, log);
    }


    // ==================== INICIO DE LA SIMULACIÓN ====================
    public void iniciar() {
        log.registrar("Sistema de cafetería iniciado");

        try {
            ServidorRMI.iniciar(this);
        } catch (Exception e) {
            log.registrar("Error RMI: " + e.getMessage());
        }
        generadorClientes = new Thread(this::generarClientes, "GeneradorClientes");
        generadorVendedores = new Thread(this::generarVendedores, "GeneradorVendedores");
        generadorCocineros = new Thread(this::generarCocineros, "GeneradorCocineros");

        generadorClientes.start();
        generadorVendedores.start();
        generadorCocineros.start();
    }


    // ==================== CONTROL DE PAUSA ====================
    public void verificarPausa() {
        paso.mirar(); // <-- Método real de tu clase Paso
    }


    // ==================== REGISTRO DE FINALIZACIÓN ====================
    public synchronized void clienteFinalizado() {
        clientesFinalizados++;
        if (clientesFinalizados == NUM_CLIENTES) {
            log.registrar("Todos los clientes han terminado. Finalizando...");
            finalizarSimulacion();
        }
    }


    // ==================== FINALIZACIÓN ====================
    private void finalizarSimulacion() {
        if (generadorVendedores != null) generadorVendedores.interrupt();
        if (generadorCocineros != null) generadorCocineros.interrupt();
        log.registrar("Simulación finalizada.");
    }


    // =========================================================
    //                  GENERADORES DE HILOS
    // =========================================================

    private void generarClientes() {
        try {
            for (int i = 1; i <= NUM_CLIENTES; i++) {
                verificarPausa();
                Cliente c = new Cliente(String.valueOf(i), this);
                c.start();
                Thread.sleep(1000 + (int)(Math.random() * 2000)); // 1-3 s
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void generarVendedores() {
        try {
            for (int i = 1; i <= NUM_VENDEDORES; i++) {
                verificarPausa();
                Vendedor v = new Vendedor(String.valueOf(i), this);
                v.start();
                Thread.sleep(500 + (int)(Math.random() * 2000)); // 0.5–2.5 s
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void generarCocineros() {
        try {
            for (int i = 1; i <= NUM_COCINEROS; i++) {
                verificarPausa();
                Cocinero co = new Cocinero(String.valueOf(i), this);
                co.start();
                Thread.sleep(1000 + (int)(Math.random() * 1000)); // 1–2 s
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    // =========================================================
    //                        RMI SNAPSHOT
    // =========================================================
    public EstadoCafeteria generarEstado() {
        return new EstadoCafeteria(
            // DESPENSA
            despensa.getUnidadesCafe(),
            despensa.getUnidadesRosquillas(),
            despensa.getCocinerosEnDespensa(),
            despensa.getVendedoresEnDespensa(),

            // MOSTRADOR
            mostrador.getUnidadesCafe(),
            mostrador.getUnidadesRosquillas(),
            mostrador.getClientesEnMostrador(),
            mostrador.getVendedoresEnMostrador(),

            // COCINA
            cocina.getCocinerosEnCocina(),

            // CAJA
            caja.getClientesEnCaja(),
            caja.getRecaudacionTotal(),

            // ÁREA DE CONSUMICIÓN
            areaConsumicion.getClientesEnArea(),

            // ENTRADA
            entradaCafeteria.getClientesEnEntrada(),
            entradaCafeteria.getClientesEnCola(),

            // PARQUE
            parque.getClientesEnParque(),

            // SALA DE DESCANSO
            salaDescanso.getCocinerosEnSala(),
            salaDescanso.getVendedoresEnSala(),

            // SIMULACIÓN
            clientesFinalizados
        );
    }


    // =========================================================
    //                      GETTERS
    // =========================================================
    public Despensa getDespensa() { return despensa; }
    public Mostrador getMostrador() { return mostrador; }
    public Cocina getCocina() { return cocina; }
    public Caja getCaja() { return caja; }
    public AreaConsumicion getAreaConsumicion() { return areaConsumicion; }
    public SalaDescanso getSalaDescanso() { return salaDescanso; }
    public Parque getParque() { return parque; }
    public EntradaCafeteria getEntradaCafeteria() { return entradaCafeteria; }
    public LogCafeteria getLog() { return log; }
    public Paso getPaso() { return paso; }

}
