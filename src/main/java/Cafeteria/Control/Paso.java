/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cafeteria.Control;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Nikolay
 */
public class Paso {
    private boolean cerrado = false;
    private final Lock cerrojo = new ReentrantLock();
    private final Condition parar = cerrojo.newCondition();
    
    /**
     * Los hilos llaman a este método para verificar si deben detenerse.
     * Si cerrado=true, el hilo queda bloqueado hasta que se llame a abrir().
     */
    public void mirar() {
        cerrojo.lock();
        try {
            while (cerrado) {
                try {
                    parar.await(); // Espera hasta que se abra el paso
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } finally {
            cerrojo.unlock();
        }
    }
    
    /**
     * Abre el paso y despierta a todos los hilos que estén esperando.
     */
    public void abrir() {
        cerrojo.lock();
        try {
            cerrado = false;
            parar.signalAll(); // Despertar a todos los hilos bloqueados
        } finally {
            cerrojo.unlock();
        }
    }
    
    /**
     * Cierra el paso.
     * Los hilos que llamen a mirar() quedarán bloqueados.
     */
    public void cerrar() {
        cerrojo.lock();
        try {
            cerrado = true;
        } finally {
            cerrojo.unlock();
        }
    }
    
    /**
     * Indica si el paso está cerrado.
     */
    public boolean estaCerrado() {
        cerrojo.lock();
        try {
            return cerrado;
        } finally {
            cerrojo.unlock();
        }
    }
}
