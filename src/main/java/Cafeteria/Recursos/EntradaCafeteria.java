/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cafeteria.Recursos;

import Cafeteria.Log.LogCafeteria;
import Cafeteria.util.ListaHilos;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Nikolay
 */
public class EntradaCafeteria {
    private final Semaphore semaforo;
    private final LogCafeteria log;
    
    // Listas para visualización (patrón Sesión 5)
    private final ListaHilos colaEspera;
    private final ListaHilos dentroEntrada;
    
    private int clientesEnEntrada;
    private final Object lockContador = new Object();
    
    /**
     * Constructor.
     * @param aforo Número máximo de clientes (20 según enunciado)
     * @param log Sistema de log
     */
    public EntradaCafeteria(int aforo, LogCafeteria log) {
        // fair=true para que entren en orden FIFO
        this.semaforo = new Semaphore(aforo, true);
        this.log = log;
        this.clientesEnEntrada = 0;
        this.colaEspera = new ListaHilos("ColaEspera");
        this.dentroEntrada = new ListaHilos("DentroEntrada");
    }
    
    /**
     * Cliente intenta entrar a la cafetería.
     * Si hay aforo, entra. Si no, espera en la cola exterior.
     * 
     * Patrón de la clase Exposicion (Sesión 5):
     * 1. Se añade a la cola de espera
     * 2. Intenta adquirir el semáforo (bloquea si no hay hueco)
     * 3. Sale de la cola y entra a la zona de entrada
     */
    public void entrar(String id) throws InterruptedException {
        // Añadir a la cola de espera ANTES de intentar entrar
        colaEspera.meter(id);
        log.registrar("Cliente " + id + " espera para entrar a la cafetería");
        
        // Intentar entrar (bloquea si el aforo está completo)
        semaforo.acquire();
        
        // Una vez adquirido el permiso, salir de cola y entrar
        colaEspera.sacar(id);
        dentroEntrada.meter(id);
        
        synchronized (lockContador) {
            clientesEnEntrada++;
        }
        
        log.registrar("Cliente " + id + " entra a la cafetería");
    }
    
    /**
     * Cliente sale de la zona de entrada (pasa al mostrador).
     * Libera un hueco para otro cliente de la cola.
     */
    public void salir(String id) {
        dentroEntrada.sacar(id);
        
        synchronized (lockContador) {
            clientesEnEntrada--;
        }
        
        // Liberar permiso del semáforo
        semaforo.release();
    }
    
    /**
     * Devuelve el número de clientes en la entrada.
     */
    public int getClientesEnEntrada() {
        synchronized (lockContador) {
            return clientesEnEntrada;
        }
    }
    
    /**
     * Devuelve el número de clientes esperando fuera.
     */
    public int getClientesEnCola() {
        return colaEspera.size();
    }
    
    /**
     * Devuelve los huecos disponibles.
     */
    public int getHuecosDisponibles() {
        return semaforo.availablePermits();
    }
    
    /**
     * Devuelve la lista de clientes en cola (para GUI).
     */
    public ListaHilos getColaEspera() {
        return colaEspera;
    }
    
    /**
     * Devuelve la lista de clientes dentro (para GUI).
     */
    public ListaHilos getDentroEntrada() {
        return dentroEntrada;
    }
}
