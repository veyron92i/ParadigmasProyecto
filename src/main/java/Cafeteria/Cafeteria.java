/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package Cafeteria;

import Cafeteria.Actores.*;
import Cafeteria.Recursos.*;
import Cafeteria.Control.Paso;
import Cafeteria.Log.LogCafeteria;

/**
 *
 * @author veyron92i
 */
public class Cafeteria {
    // Recursos compartidos
    private final Despensa despensa;
    private final Mostrador mostrador;
    private final Cocina cocina;
    private final Caja caja;
    private final AreaConsumicion areaConsumicion;
    private final SalaDescanso salaDescanso;
    private final Parque parque;
    private final EntradaCafeteria entradaCafeteria;
    private final LogCafeteria log;
    
    // Control de pausa usando clase Paso (Lock + Condition)
    private final Paso paso;
    
    // Constantes según el enunciado
    public static final int NUM_CLIENTES = 8000;
    public static final int NUM_VENDEDORES = 500;
    public static final int NUM_COCINEROS = 500;
    
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
    
    /**
     * Inicia la simulación creando los hilos generadores de actores.
     * Los tres generadores se ejecutan de forma concurrente.
     */
    public void iniciar() {
        log.registrar("Sistema de cafetería iniciado");
        
        // Crear hilos generadores de actores (concurrentemente)
        Thread generadorClientes = new Thread(() -> generarClientes(), "GeneradorClientes");
        Thread generadorVendedores = new Thread(() -> generarVendedores(), "GeneradorVendedores");
        Thread generadorCocineros = new Thread(() -> generarCocineros(), "GeneradorCocineros");
        
        generadorClientes.start();
        generadorVendedores.start();
        generadorCocineros.start();
    }
    
    /**
     * Genera 8000 clientes con intervalos de 1-3 segundos.
     */
    private void generarClientes() {
        for (int i = 1; i <= NUM_CLIENTES; i++) {
            verificarPausa();
            String id = String.format("C-%04d", i);
            Cliente cliente = new Cliente(id, this);
            cliente.start();
            log.registrar("Cliente " + id + " es creado");
            
            // Intervalo aleatorio entre 1 y 3 segundos
            dormir(1000 + (int)(Math.random() * 2000));
        }
        log.registrar("Generación de clientes completada");
    }
    
    /**
     * Genera 500 vendedores con intervalos de 0.5-2.5 segundos.
     */
    private void generarVendedores() {
        for (int i = 1; i <= NUM_VENDEDORES; i++) {
            verificarPausa();
            String id = String.format("V-%04d", i);
            Vendedor vendedor = new Vendedor(id, this);
            vendedor.start();
            log.registrar("Vendedor " + id + " es creado");
            
            // Intervalo aleatorio entre 0.5 y 2.5 segundos
            dormir(500 + (int)(Math.random() * 2000));
        }
        log.registrar("Generación de vendedores completada");
    }
    
    /**
     * Genera 500 cocineros con intervalos de 1-2 segundos.
     */
    private void generarCocineros() {
        for (int i = 1; i <= NUM_COCINEROS; i++) {
            verificarPausa();
            String id = String.format("B-%04d", i);
            Cocinero cocinero = new Cocinero(id, this);
            cocinero.start();
            log.registrar("Cocinero " + id + " es creado");
            
            // Intervalo aleatorio entre 1 y 2 segundos
            dormir(1000 + (int)(Math.random() * 1000));
        }
        log.registrar("Generación de cocineros completada");
    }
    
    private void dormir(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Los hilos llaman a este método para verificar si deben pausarse.
     */
    public void verificarPausa() {
        paso.mirar();
    }
    
    public void pausar() {
        paso.cerrar();
        log.registrar("Sistema PAUSADO");
    }
    
    public void reanudar() {
        paso.abrir();
        log.registrar("Sistema REANUDADO");
    }
    
    public boolean isPausado() {
        return paso.estaCerrado();
    }
    
    // Getters para recursos
    public Despensa getDespensa() { return despensa; }
    public Mostrador getMostrador() { return mostrador; }
    public Cocina getCocina() { return cocina; }
    public Caja getCaja() { return caja; }
    public AreaConsumicion getAreaConsumicion() { return areaConsumicion; }
    public SalaDescanso getSalaDescanso() { return salaDescanso; }
    public Parque getParque() { return parque; }
    public EntradaCafeteria getEntradaCafeteria() { return entradaCafeteria; }
    public LogCafeteria getLog() { return log; }
    
}
