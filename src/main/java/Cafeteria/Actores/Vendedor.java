/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cafeteria.Actores;

import Cafeteria.Cafeteria;

/**
 *
 * @author Nikolay
 */
public class Vendedor extends Thread {
    private final String id;
    private final Cafeteria cafeteria;
    private int cafesTransportados;
    private int rosquillasTransportadas;
    
    public Vendedor(String id, Cafeteria cafeteria) {
        super("Vendedor-" + id);
        this.id = id;
        this.cafeteria = cafeteria;
        this.cafesTransportados = 0;
        this.rosquillasTransportadas = 0;
    }
    
    @Override
    public void run() {
        try {
            // Ciclo infinito
            while (!Thread.currentThread().isInterrupted()) {
                
                // ========== 1. SALA DE DESCANSO ==========
                cafeteria.getSalaDescanso().entrarVendedor(id);
                cafeteria.verificarPausa();
                
                // Descansa (5-10 segundos)
                dormir(5000 + (int)(Math.random() * 5000));
                
                cafeteria.getSalaDescanso().salirVendedor(id);
                cafeteria.verificarPausa();
                
                // ========== 2. TRAYECTO A LA DESPENSA ==========
                cafeteria.getLog().registrar("Vendedor " + id + " se dirige a la despensa");
                dormir(1000 + (int)(Math.random() * 2000)); // 1-3 segundos
                cafeteria.verificarPausa();
                
                // ========== 3. DESPENSA ==========
                // Entra a la despensa (aforo: 50 vendedores)
                cafeteria.getDespensa().entrarVendedor(id);
                cafeteria.verificarPausa();
                
                // Define cantidades a seleccionar
                int cafesDeseados = 3 + (int)(Math.random() * 4);       // 3-6 cafés
                int rosquillasDeseadas = 5 + (int)(Math.random() * 6);  // 5-10 rosquillas
                
                // Recoger cafés (puede ser parcial, espera si no hay)
                cafesTransportados = 0;
                while (cafesTransportados < cafesDeseados) {
                    cafeteria.verificarPausa();
                    int obtenidos = cafeteria.getDespensa().tomarCafes(id, cafesDeseados - cafesTransportados);
                    cafesTransportados += obtenidos;
                    if (cafesTransportados < cafesDeseados && obtenidos == 0) {
                        dormir(500); // Esperar a que haya más productos
                    }
                }
                
                // Recoger rosquillas (puede ser parcial, espera si no hay)
                rosquillasTransportadas = 0;
                while (rosquillasTransportadas < rosquillasDeseadas) {
                    cafeteria.verificarPausa();
                    int obtenidas = cafeteria.getDespensa().tomarRosquillas(id, rosquillasDeseadas - rosquillasTransportadas);
                    rosquillasTransportadas += obtenidas;
                    if (rosquillasTransportadas < rosquillasDeseadas && obtenidas == 0) {
                        dormir(500);
                    }
                }
                
                // Preparación antes de salir (1-3 segundos)
                dormir(1000 + (int)(Math.random() * 2000));
                
                cafeteria.getLog().registrar("Vendedor " + id + " recoge " + 
                    cafesTransportados + " cafés y " + rosquillasTransportadas + 
                    " rosquillas de despensa");
                
                cafeteria.getDespensa().salirVendedor(id);
                cafeteria.verificarPausa();
                
                // ========== 4. TRAYECTO AL MOSTRADOR ==========
                cafeteria.getLog().registrar("Vendedor " + id + " se dirige al mostrador");
                dormir(2000 + (int)(Math.random() * 3000)); // 2-5 segundos
                cafeteria.verificarPausa();
                
                // ========== 5. MOSTRADOR ==========
                // Entra al mostrador (aforo: 20 vendedores)
                cafeteria.getMostrador().entrarVendedor(id);
                cafeteria.verificarPausa();
                
                // Proceso de colocar productos (1-3 segundos)
                dormir(1000 + (int)(Math.random() * 2000));
                
                // Repone productos en las estanterías
                cafeteria.getMostrador().reponerCafes(id, cafesTransportados);
                cafeteria.getMostrador().reponerRosquillas(id, rosquillasTransportadas);
                
                cafeteria.getLog().registrar("Vendedor " + id + " repone " + 
                    cafesTransportados + " cafés y " + rosquillasTransportadas + 
                    " rosquillas en el mostrador");
                
                cafeteria.getMostrador().salirVendedor(id);
                cafeteria.verificarPausa();
                
                // ========== 6. TRAYECTO A SALA DE DESCANSO ==========
                cafeteria.getLog().registrar("Vendedor " + id + " se dirige a la sala de descanso");
                dormir(2000 + (int)(Math.random() * 3000)); // 2-5 segundos
                
                // Vuelve al inicio del ciclo
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            cafeteria.getLog().registrar("Vendedor " + id + " finaliza su ejecución");
        }
    }
    
    private void dormir(int ms) throws InterruptedException {
        Thread.sleep(ms);
    }
    
    public String getIdVendedor() {
        return id;
    }
    
    public int getCafesTransportados() {
        return cafesTransportados;
    }
    
    public int getRosquillasTransportadas() {
        return rosquillasTransportadas;
    }
}