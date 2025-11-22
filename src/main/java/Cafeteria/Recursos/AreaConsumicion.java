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
public class AreaConsumicion {
    private final Semaphore semaforo;
    private final LogCafeteria log;
    private int clientesEnArea;
    private final Object lockContador = new Object();
    
    /**
     * Constructor.
     * @param aforo Número máximo de clientes (30 según enunciado)
     * @param log Sistema de log
     */
    public AreaConsumicion(int aforo, LogCafeteria log) {
        // fair=true para respetar orden de llegada (requisito del enunciado)
        this.semaforo = new Semaphore(aforo, true);
        this.log = log;
        this.clientesEnArea = 0;
    }
    
    /**
     * Cliente intenta entrar al área de consumición.
     * Bloquea si ya hay 30 clientes dentro.
     * El orden de llegada se respeta gracias a fair=true.
     */
    public void entrar(String id) throws InterruptedException {
        semaforo.acquire();
        synchronized (lockContador) {
            clientesEnArea++;
        }
        log.registrar("Cliente " + id + " entra al área de consumición");
    }
    
    /**
     * Cliente sale del área de consumición tras consumir sus productos.
     */
    public void salir(String id) {
        synchronized (lockContador) {
            clientesEnArea--;
        }
        semaforo.release();
        log.registrar("Cliente " + id + " sale del área de consumición");
    }
    
    /**
     * Devuelve el número de clientes actualmente en el área.
     */
    public int getClientesEnArea() {
        synchronized (lockContador) {
            return clientesEnArea;
        }
    }
    
    /**
     * Devuelve los huecos disponibles.
     */
    public int getHuecosDisponibles() {
        return semaforo.availablePermits();
    }
}
