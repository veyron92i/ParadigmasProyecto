/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cafeteria.Recursos;

import Cafeteria.Log.LogCafeteria;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author veyron92i
 */
public class Despensa {
    // Semáforos para control de aforo (separados por tipo de actor)
    private final Semaphore semaforoCocineros;
    private final Semaphore semaforoVendedores;
    private final LogCafeteria log;
    
    // Variables compartidas de productos
    private int unidadesCafe;
    private int unidadesRosquillas;
    
    // Contadores de personas
    private int cocinerosEnDespensa;
    private int vendedoresEnDespensa;
    
    // Lock único para productos (evita interbloqueos - Sesión 4)
    private final Lock lockProductos = new ReentrantLock(true);
    private final Condition hayCafes = lockProductos.newCondition();
    private final Condition hayRosquillas = lockProductos.newCondition();
    
    // Lock para contadores
    private final Object lockContadores = new Object();
    
    public Despensa(int aforoCocineros, int aforoVendedores, LogCafeteria log) {
        // Semáforos fair=true para respetar orden FIFO
        this.semaforoCocineros = new Semaphore(aforoCocineros, true);
        this.semaforoVendedores = new Semaphore(aforoVendedores, true);
        this.log = log;
        this.unidadesCafe = 0;
        this.unidadesRosquillas = 0;
        this.cocinerosEnDespensa = 0;
        this.vendedoresEnDespensa = 0;
    }
    
    // ==================== MÉTODOS PARA COCINEROS ====================
    
    /**
     * Cocinero intenta entrar a la despensa.
     * Bloquea si ya hay 50 cocineros dentro.
     */
    public void entrarCocinero(String id) throws InterruptedException {
        semaforoCocineros.acquire();
        synchronized (lockContadores) {
            cocinerosEnDespensa++;
        }
        log.registrar("Cocinero " + id + " entra a la despensa");
    }
    
    /**
     * Cocinero sale de la despensa.
     */
    public void salirCocinero(String id) {
        synchronized (lockContadores) {
            cocinerosEnDespensa--;
        }
        semaforoCocineros.release();
        log.registrar("Cocinero " + id + " sale de la despensa");
    }
    
    /**
     * Cocinero deja cafés en la estantería.
     * Notifica a vendedores que pueden estar esperando.
     */
    public void dejarCafes(String id, int cantidad) {
        lockProductos.lock();
        try {
            unidadesCafe += cantidad;
            hayCafes.signalAll(); // Despertar vendedores esperando cafés
        } finally {
            lockProductos.unlock();
        }
    }
    
    /**
     * Cocinero deja rosquillas en la estantería.
     * Notifica a vendedores que pueden estar esperando.
     */
    public void dejarRosquillas(String id, int cantidad) {
        lockProductos.lock();
        try {
            unidadesRosquillas += cantidad;
            hayRosquillas.signalAll(); // Despertar vendedores esperando rosquillas
        } finally {
            lockProductos.unlock();
        }
    }
    
    // ==================== MÉTODOS PARA VENDEDORES ====================
    
    /**
     * Vendedor intenta entrar a la despensa.
     * Bloquea si ya hay 50 vendedores dentro.
     */
    public void entrarVendedor(String id) throws InterruptedException {
        semaforoVendedores.acquire();
        synchronized (lockContadores) {
            vendedoresEnDespensa++;
        }
        log.registrar("Vendedor " + id + " entra a la despensa");
    }
    
    /**
     * Vendedor sale de la despensa.
     */
    public void salirVendedor(String id) {
        synchronized (lockContadores) {
            vendedoresEnDespensa--;
        }
        semaforoVendedores.release();
        log.registrar("Vendedor " + id + " sale de la despensa");
    }
    
    /**
     * Vendedor toma cafés de la estantería.
     * Toma lo que haya disponible (puede ser menos de lo deseado).
     * NO bloquea si no hay suficientes.
     */
    public int tomarCafes(String id, int cantidadDeseada) {
        lockProductos.lock();
        try {
            int disponibles = Math.min(cantidadDeseada, unidadesCafe);
            unidadesCafe -= disponibles;
            return disponibles;
        } finally {
            lockProductos.unlock();
        }
    }
    
    /**
     * Vendedor toma rosquillas de la estantería.
     * Toma lo que haya disponible (puede ser menos de lo deseado).
     * NO bloquea si no hay suficientes.
     */
    public int tomarRosquillas(String id, int cantidadDeseada) {
        lockProductos.lock();
        try {
            int disponibles = Math.min(cantidadDeseada, unidadesRosquillas);
            unidadesRosquillas -= disponibles;
            return disponibles;
        } finally {
            lockProductos.unlock();
        }
    }
    
    /**
     * Vendedor espera a que haya al menos un café disponible.
     * Método alternativo con bloqueo.
     */
    public void esperarCafes(String id, int cantidadMinima) throws InterruptedException {
        lockProductos.lock();
        try {
            while (unidadesCafe < cantidadMinima) {
                hayCafes.await();
            }
        } finally {
            lockProductos.unlock();
        }
    }
    
    /**
     * Vendedor espera a que haya al menos una rosquilla disponible.
     * Método alternativo con bloqueo.
     */
    public void esperarRosquillas(String id, int cantidadMinima) throws InterruptedException {
        lockProductos.lock();
        try {
            while (unidadesRosquillas < cantidadMinima) {
                hayRosquillas.await();
            }
        } finally {
            lockProductos.unlock();
        }
    }
    
    // ==================== GETTERS PARA CONSULTA ====================
    
    public int getUnidadesCafe() {
        lockProductos.lock();
        try {
            return unidadesCafe;
        } finally {
            lockProductos.unlock();
        }
    }
    
    public int getUnidadesRosquillas() {
        lockProductos.lock();
        try {
            return unidadesRosquillas;
        } finally {
            lockProductos.unlock();
        }
    }
    
    public int getCocinerosEnDespensa() {
        synchronized (lockContadores) {
            return cocinerosEnDespensa;
        }
    }
    
    public int getVendedoresEnDespensa() {
        synchronized (lockContadores) {
            return vendedoresEnDespensa;
        }
    }
}
