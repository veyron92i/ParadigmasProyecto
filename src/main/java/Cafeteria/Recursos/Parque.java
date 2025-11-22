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
public class Parque {
    private final LogCafeteria log;
    private int clientesEnParque;
    private final Object lockContador = new Object();
    
    public Parque(LogCafeteria log) {
        this.log = log;
        this.clientesEnParque = 0;
    }
    
    /**
     * Cliente entra al parque.
     * No hay límite de aforo.
     */
    public void entrar(String id) {
        synchronized (lockContador) {
            clientesEnParque++;
        }
        log.registrar("Cliente " + id + " entra al parque");
    }
    
    /**
     * Cliente sale del parque para dirigirse a la cafetería.
     */
    public void salir(String id) {
        synchronized (lockContador) {
            clientesEnParque--;
        }
        log.registrar("Cliente " + id + " sale del parque");
    }
    
    /**
     * Devuelve el número de clientes actualmente en el parque.
     */
    public int getClientesEnParque() {
        synchronized (lockContador) {
            return clientesEnParque;
        }
    }
}
