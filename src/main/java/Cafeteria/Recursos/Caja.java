/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cafeteria.Recursos;

import Cafeteria.Log.LogCafeteria;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Nikolay
 */
public class Caja {
    private final Semaphore semaforo;
    private final LogCafeteria log;
    private double recaudacionTotal;
    private int clientesEnCaja;
    
    private final Object lockRecaudacion = new Object();
    private final Object lockContador = new Object();
    
    /**
     * Constructor.
     * @param aforo Número máximo de clientes (10 según enunciado)
     * @param log Sistema de log
     */
    public Caja(int aforo, LogCafeteria log) {
        this.semaforo = new Semaphore(aforo, true); // fair=true para orden FIFO
        this.log = log;
        this.recaudacionTotal = 0.0;
        this.clientesEnCaja = 0;
    }
    
    /**
     * Cliente intenta acceder a una máquina de autopago.
     * Bloquea si las 10 máquinas están ocupadas.
     */
    public void entrar(String id) throws InterruptedException {
        semaforo.acquire();
        synchronized (lockContador) {
            clientesEnCaja++;
        }
        log.registrar("Cliente " + id + " accede a la caja");
    }
    
    /**
     * Cliente realiza el pago.
     * @param id Identificador del cliente
     * @param cantidad Monto a pagar
     */
    public void pagar(String id, double cantidad) {
        synchronized (lockRecaudacion) {
            recaudacionTotal += cantidad;
        }
        log.registrar(String.format("Cliente %s paga %.2f€. Recaudación total: %.2f€", 
            id, cantidad, getRecaudacionTotal()));
    }
    
    /**
     * Cliente termina de pagar y libera la máquina.
     */
    public void salir(String id) {
        synchronized (lockContador) {
            clientesEnCaja--;
        }
        semaforo.release();
        log.registrar("Cliente " + id + " sale de la caja");
    }
    
    /**
     * Devuelve la recaudación total acumulada.
     */
    public double getRecaudacionTotal() {
        synchronized (lockRecaudacion) {
            return recaudacionTotal;
        }
    }
    
    /**
     * Devuelve el número de clientes actualmente en la caja.
     */
    public int getClientesEnCaja() {
        synchronized (lockContador) {
            return clientesEnCaja;
        }
    }
    
    /**
     * Devuelve las máquinas disponibles.
     */
    public int getMaquinasDisponibles() {
        return semaforo.availablePermits();
    }
}
