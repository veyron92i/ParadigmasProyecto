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
public class Cocinero extends Thread {
    private final String id;
    private final Cafeteria cafeteria;
    private int cafesGenerados;
    private int rosquillasGeneradas;
    
    public Cocinero(String id, Cafeteria cafeteria) {
        super("Cocinero-" + id);
        this.id = id;
        this.cafeteria = cafeteria;
        this.cafesGenerados = 0;
        this.rosquillasGeneradas = 0;
    }
    
    @Override
    public void run() {
        try {
            // Ciclo infinito
            while (!Thread.currentThread().isInterrupted()) {
                
                // ========== 1. SALA DE DESCANSO ==========
                cafeteria.getSalaDescanso().entrarCocinero(id);
                cafeteria.verificarPausa();
                
                // Descansa (5-10 segundos)
                dormir(5000 + (int)(Math.random() * 5000));
                
                cafeteria.getSalaDescanso().salirCocinero(id);
                cafeteria.verificarPausa();
                
                // ========== 2. TRAYECTO A LA COCINA ==========
                cafeteria.getLog().registrar("Cocinero " + id + " se dirige a la cocina");
                dormir(1000 + (int)(Math.random() * 2000)); // 1-3 segundos
                cafeteria.verificarPausa();
                
                // ========== 3. COCINA ==========
                // Accede a la cocina (aforo: 100 cocineros)
                cafeteria.getCocina().entrar(id);
                cafeteria.verificarPausa();
                
                // Prepara productos
                cafesGenerados = 2 + (int)(Math.random() * 4);        // 2-5 cafés
                rosquillasGeneradas = 4 + (int)(Math.random() * 5);   // 4-8 rosquillas
                
                // Proceso de generación (5-10 segundos)
                dormir(5000 + (int)(Math.random() * 5000));
                
                cafeteria.getLog().registrar("Cocinero " + id + " genera " + 
                    cafesGenerados + " cafés y " + rosquillasGeneradas + " rosquillas");
                
                cafeteria.getCocina().salir(id);
                cafeteria.verificarPausa();
                
                // ========== 4. TRAYECTO A LA DESPENSA ==========
                cafeteria.getLog().registrar("Cocinero " + id + " se dirige a la despensa");
                dormir(2000 + (int)(Math.random() * 3000)); // 2-5 segundos
                cafeteria.verificarPausa();
                
                // ========== 5. DESPENSA ==========
                // Entra a la despensa (aforo: 50 cocineros)
                cafeteria.getDespensa().entrarCocinero(id);
                cafeteria.verificarPausa();
                
                // Deja los productos (puede ser parcialmente: primero cafés, luego rosquillas)
                cafeteria.getDespensa().dejarCafes(id, cafesGenerados);
                cafeteria.getDespensa().dejarRosquillas(id, rosquillasGeneradas);
                
                cafeteria.getLog().registrar("Cocinero " + id + " deja " + 
                    cafesGenerados + " cafés y " + rosquillasGeneradas + 
                    " rosquillas en despensa");
                
                cafeteria.getDespensa().salirCocinero(id);
                cafeteria.verificarPausa();
                
                // ========== 6. TRAYECTO A SALA DE DESCANSO ==========
                cafeteria.getLog().registrar("Cocinero " + id + " se dirige a la sala de descanso");
                dormir(2000 + (int)(Math.random() * 3000)); // 2-5 segundos
                
                // Vuelve al inicio del ciclo
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            cafeteria.getLog().registrar("Cocinero " + id + " finaliza su ejecución");
        }
    }
    
    private void dormir(int ms) throws InterruptedException {
        Thread.sleep(ms);
    }
    
    public String getIdCocinero() {
        return id;
    }
    
    public int getCafesGenerados() {
        return cafesGenerados;
    }
    
    public int getRosquillasGeneradas() {
        return rosquillasGeneradas;
    }
}
