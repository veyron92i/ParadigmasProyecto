/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cafeteria.Recursos;

import Cafeteria.Log.LogCafeteria;


/**
 *
 * @author Nikolay
 */
public class SalaDescanso {
    private final LogCafeteria log;
    private int cocinerosEnSala;
    private int vendedoresEnSala;
    private final Object lockContadores = new Object();
    
    public SalaDescanso(LogCafeteria log) {
        this.log = log;
        this.cocinerosEnSala = 0;
        this.vendedoresEnSala = 0;
    }
    
    // ==================== MÉTODOS PARA COCINEROS ====================
    
    /**
     * Cocinero entra a la sala de descanso.
     * No hay límite de aforo.
     */
    public void entrarCocinero(String id) {
        synchronized (lockContadores) {
            cocinerosEnSala++;
        }
        log.registrar("Cocinero " + id + " entra a la sala de descanso");
    }
    
    /**
     * Cocinero sale de la sala de descanso.
     */
    public void salirCocinero(String id) {
        synchronized (lockContadores) {
            cocinerosEnSala--;
        }
        log.registrar("Cocinero " + id + " sale de la sala de descanso");
    }
    
    // ==================== MÉTODOS PARA VENDEDORES ====================
    
    /**
     * Vendedor entra a la sala de descanso.
     * No hay límite de aforo.
     */
    public void entrarVendedor(String id) {
        synchronized (lockContadores) {
            vendedoresEnSala++;
        }
        log.registrar("Vendedor " + id + " entra a la sala de descanso");
    }
    
    /**
     * Vendedor sale de la sala de descanso.
     */
    public void salirVendedor(String id) {
        synchronized (lockContadores) {
            vendedoresEnSala--;
        }
        log.registrar("Vendedor " + id + " sale de la sala de descanso");
    }
    
    // ==================== GETTERS ====================
    
    /**
     * Devuelve el total de empleados en la sala (cocineros + vendedores).
     */
    public int getTotalEnSala() {
        synchronized (lockContadores) {
            return cocinerosEnSala + vendedoresEnSala;
        }
    }
    
    /**
     * Devuelve el número de cocineros en la sala.
     */
    public int getCocinerosEnSala() {
        synchronized (lockContadores) {
            return cocinerosEnSala;
        }
    }
    
    /**
     * Devuelve el número de vendedores en la sala.
     */
    public int getVendedoresEnSala() {
        synchronized (lockContadores) {
            return vendedoresEnSala;
        }
    }
}
