/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cafeteria.Actores;

import Cafeteria.Cafeteria;

/**
 *
 * @author veyron92i
 */
public class Cliente extends Thread {
    private final String id;
    private final Cafeteria cafeteria;
    private int cafesDeseados;
    private int rosquillasDeseadas;
    private int cafesObtenidos;
    private int rosquillasObtenidas;
    
    public Cliente(String id, Cafeteria cafeteria) {
        super("Cliente-" + id);
        this.id = id;
        this.cafeteria = cafeteria;
        this.cafesDeseados = 0;
        this.rosquillasDeseadas = 0;
        this.cafesObtenidos = 0;
        this.rosquillasObtenidas = 0;
    }
    
    @Override
    public void run() {
        try {
            // ========== 1. PARQUE ==========
            cafeteria.getParque().entrar(id);
            cafeteria.verificarPausa();
            
            // Contempla el parque (5-10 segundos)
            dormir(5000 + (int)(Math.random() * 5000));
            
            cafeteria.getParque().salir(id);
            cafeteria.verificarPausa();
            
            // ========== 2. TRAYECTO A LA CAFETERÍA ==========
            cafeteria.getLog().registrar("Cliente " + id + " se dirige a la cafetería");
            dormir(3000 + (int)(Math.random() * 6000)); // 3-9 segundos
            cafeteria.verificarPausa();
            
            // ========== 3. ENTRADA A LA CAFETERÍA ==========
            // Si no hay aforo, espera en cola
            cafeteria.getEntradaCafeteria().entrar(id);
            cafeteria.verificarPausa();
            
            // ========== 4. MOSTRADOR ==========
            // Accede al mostrador (máximo 5 clientes)
            cafeteria.getMostrador().entrarCliente(id);
            cafeteria.getEntradaCafeteria().salir(id);
            cafeteria.verificarPausa();
            
            // Decide qué productos quiere
            cafesDeseados = 1 + (int)(Math.random() * 3);      // 1-3 cafés
            rosquillasDeseadas = (int)(Math.random() * 5);      // 0-4 rosquillas
            
            cafeteria.getLog().registrar("Cliente " + id + " desea " + 
                cafesDeseados + " cafés y " + rosquillasDeseadas + " rosquillas");
            
            // Obtener cafés (espera si no hay disponibles)
            while (cafesObtenidos < cafesDeseados) {
                cafeteria.verificarPausa();
                int obtenidos = cafeteria.getMostrador().tomarCafes(id, cafesDeseados - cafesObtenidos);
                cafesObtenidos += obtenidos;
                if (cafesObtenidos < cafesDeseados) {
                    dormir(500); // Esperar reposición
                }
            }
            
            // Obtener rosquillas (espera si no hay disponibles)
            while (rosquillasObtenidas < rosquillasDeseadas) {
                cafeteria.verificarPausa();
                int obtenidas = cafeteria.getMostrador().tomarRosquillas(id, rosquillasDeseadas - rosquillasObtenidas);
                rosquillasObtenidas += obtenidas;
                if (rosquillasObtenidas < rosquillasDeseadas) {
                    dormir(500);
                }
            }
            
            cafeteria.getLog().registrar("Cliente " + id + " obtiene " + 
                cafesObtenidos + " cafés y " + rosquillasObtenidas + " rosquillas");
            
            cafeteria.getMostrador().salirCliente(id);
            cafeteria.verificarPausa();
            
            // ========== 5. CAJA ==========
            cafeteria.getCaja().entrar(id);
            cafeteria.verificarPausa();
            
            // Calcular total a pagar
            double total = (cafesObtenidos * 1.50) + (rosquillasObtenidas * 2.50);
            
            // Proceso de pago (2-5 segundos)
            dormir(2000 + (int)(Math.random() * 3000));
            
            cafeteria.getCaja().pagar(id, total);
            cafeteria.getCaja().salir(id);
            cafeteria.verificarPausa();
            
            // ========== 6. ÁREA DE CONSUMICIÓN ==========
            cafeteria.getAreaConsumicion().entrar(id);
            cafeteria.verificarPausa();
            
            // Proceso de consumición (10-15 segundos)
            cafeteria.getLog().registrar("Cliente " + id + " consume sus productos");
            dormir(10000 + (int)(Math.random() * 5000));
            
            cafeteria.getAreaConsumicion().salir(id);
            
            // ========== 7. FIN ==========
            cafeteria.getLog().registrar("Cliente " + id + " abandona la cafetería y finaliza");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            cafeteria.getLog().registrar("Cliente " + id + " interrumpido");
        }
    }
    
    private void dormir(int ms) throws InterruptedException {
        Thread.sleep(ms);
    }
    
    public String getIdCliente() {
        return id;
    }
    
    public int getCafesObtenidos() {
        return cafesObtenidos;
    }
    
    public int getRosquillasObtenidas() {
        return rosquillasObtenidas;
    }
}
