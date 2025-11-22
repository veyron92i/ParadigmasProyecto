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
public class Cocina {
    private final Semaphore semaforo;
    private final LogCafeteria log;
    private int cocinerosEnCocina;
    private final Object lockContador = new Object();
    
    /**
     * Constructor.
     * @param aforo Número máximo de cocineros (100 según enunciado)
     * @param log Sistema de log
     */
    public Cocina(int aforo, LogCafeteria log) {
        this.semaforo = new Semaphore(aforo, true); // fair=true
        this.log = log;
        this.cocinerosEnCocina = 0;
    }
    
    /**
     * Cocinero intenta entrar a la cocina.
     * Bloquea si ya hay 100 cocineros dentro.
     */
    public void entrar(String id) throws InterruptedException {
        semaforo.acquire();
        synchronized (lockContador) {
            cocinerosEnCocina++;
        }
        log.registrar("Cocinero " + id + " entra a la cocina");
    }
    
    /**
     * Cocinero sale de la cocina.
     */
    public void salir(String id) {
        synchronized (lockContador) {
            cocinerosEnCocina--;
        }
        semaforo.release();
        log.registrar("Cocinero " + id + " sale de la cocina");
    }
    
    /**
     * Devuelve el número de cocineros actualmente en la cocina.
     */
    public int getCocinerosEnCocina() {
        synchronized (lockContador) {
            return cocinerosEnCocina;
        }
    }
    
    /**
     * Devuelve los permisos disponibles del semáforo.
     */
    public int getHuecosDisponibles() {
        return semaforo.availablePermits();
    }
}
