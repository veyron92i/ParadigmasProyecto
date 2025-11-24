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
 * @author Nikolay
 */
public class Mostrador {
    // Semáforos para control de aforo
    private final Semaphore semaforoClientes;
    private final Semaphore semaforoVendedores;
    private final LogCafeteria log;
    
    // Estanterías del mostrador
    private int unidadesCafe;
    private int unidadesRosquillas;
    
    // Contadores de personas
    private int clientesEnMostrador;
    private int vendedoresEnMostrador;
    
    // Lock para productos
    private final Lock lockCafe = new ReentrantLock(true);
    private final Lock lockRosquillas = new ReentrantLock(true);
    private final Condition hayCafes = lockCafe.newCondition();
    private final Condition hayRosquillas = lockRosquillas.newCondition();
    
    // Lock para contadores
    private final Object lockContadores = new Object();
    
    public Mostrador(int aforoClientes, int aforoVendedores, LogCafeteria log) {
        this.semaforoClientes = new Semaphore(aforoClientes, true);
        this.semaforoVendedores = new Semaphore(aforoVendedores, true);
        this.log = log;
        this.unidadesCafe = 0;
        this.unidadesRosquillas = 0;
        this.clientesEnMostrador = 0;
        this.vendedoresEnMostrador = 0;
    }
    
    // ==================== MÉTODOS PARA CLIENTES ====================
    
    /**
     * Cliente entra al mostrador para seleccionar productos.
     * Bloquea si ya hay 5 clientes.
     */
    public void entrarCliente(String id) throws InterruptedException {
        semaforoClientes.acquire();
        synchronized (lockContadores) {
            clientesEnMostrador++;
        }
        log.registrar("Cliente " + id + " accede al mostrador");
    }
    
    /**
     * Cliente sale del mostrador.
     */
    public void salirCliente(String id) {
        synchronized (lockContadores) {
            clientesEnMostrador--;
        }
        semaforoClientes.release();
        log.registrar("Cliente " + id + " sale del mostrador");
    }
    
    /**
     * Cliente toma cafés de la estantería.
     * Toma lo que haya disponible (puede ser menos de lo deseado).
     */
    public int tomarCafes(String id, int cantidadDeseada) {
        lockCafe.lock();
        try {
            int disponibles = Math.min(cantidadDeseada, unidadesCafe);
            unidadesCafe -= disponibles;
            return disponibles;
        } finally {
            lockCafe.unlock();
        }
    }
    
    /**
     * Cliente toma rosquillas de la estantería.
     * Toma lo que haya disponible (puede ser menos de lo deseado).
     */
    public int tomarRosquillas(String id, int cantidadDeseada) {
        lockRosquillas.lock();
        try {
            int disponibles = Math.min(cantidadDeseada, unidadesRosquillas);
            unidadesRosquillas -= disponibles;
            return disponibles;
        } finally {
            lockRosquillas.unlock();
        }
    }
    
    // ==================== MÉTODOS PARA VENDEDORES ====================
    
    /**
     * Vendedor entra al mostrador para reponer productos.
     * Bloquea si ya hay 20 vendedores.
     */
    public void entrarVendedor(String id) throws InterruptedException {
        semaforoVendedores.acquire();
        synchronized (lockContadores) {
            vendedoresEnMostrador++;
        }
        log.registrar("Vendedor " + id + " accede al mostrador");
    }
    
    /**
     * Vendedor sale del mostrador.
     */
    public void salirVendedor(String id) {
        synchronized (lockContadores) {
            vendedoresEnMostrador--;
        }
        semaforoVendedores.release();
        log.registrar("Vendedor " + id + " sale del mostrador");
    }
    
    /**
     * Vendedor repone cafés en la estantería.
     * Notifica a clientes que pueden estar esperando.
     */
    public void reponerCafes(String id, int cantidad) {
        lockCafe.lock();
        try {
            unidadesCafe += cantidad;
            hayCafes.signalAll(); // Notificar a clientes esperando
        } finally {
            lockCafe.unlock();
        }
    }
    
    /**
     * Vendedor repone rosquillas en la estantería.
     * Notifica a clientes que pueden estar esperando.
     */
    public void reponerRosquillas(String id, int cantidad) {
        lockRosquillas.lock();
        try {
            unidadesRosquillas += cantidad;
            hayRosquillas.signalAll(); // Notificar a clientes esperando
        } finally {
            lockRosquillas.unlock();
        }
    }
    
    // ==================== GETTERS PARA CONSULTA ====================
    
    public int getUnidadesCafe() {
        lockCafe.lock();
        try {
            return unidadesCafe;
        } finally {
            lockCafe.unlock();
        }
    }
    
    public int getUnidadesRosquillas() {
        lockRosquillas.lock();
        try {
            return unidadesRosquillas;
        } finally {
            lockRosquillas.unlock();
        }
    }
    
    public int getClientesEnMostrador() {
        synchronized (lockContadores) {
            return clientesEnMostrador;
        }
    }
    
    public int getVendedoresEnMostrador() {
        synchronized (lockContadores) {
            return vendedoresEnMostrador;
        }
    }
}
