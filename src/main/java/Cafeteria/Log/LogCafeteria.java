/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cafeteria.Log;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author veyron92i
 */
public class LogCafeteria {
    private final String nombreArchivo;
    private PrintWriter writer;
    private final ReentrantLock lock;
    private final DateTimeFormatter formatter;
    
    /**
     * Constructor.
     * @param nombreArchivo Nombre del archivo de log (ej: "evolucion_cafeteria.txt")
     */
    public LogCafeteria(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
        this.lock = new ReentrantLock(true); // fair=true
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        try {
            // false = sobrescribir archivo existente
            this.writer = new PrintWriter(new FileWriter(nombreArchivo, false));
            registrar("Archivo de log creado: " + nombreArchivo);
        } catch (IOException e) {
            System.err.println("Error al crear archivo de log: " + e.getMessage());
        }
    }
    
    /**
     * Registra un evento con marca de tiempo.
     * Thread-safe gracias al ReentrantLock.
     * 
     * @param evento Descripción del evento
     */
    public void registrar(String evento) {
        lock.lock();
        try {
            String timestamp = LocalDateTime.now().format(formatter);
            String linea = "[" + timestamp + "] " + evento;
            
            // Escribir en archivo
            if (writer != null) {
                writer.println(linea);
                writer.flush(); // Asegurar escritura inmediata
            }
            
            // También mostrar en consola (para depuración)
            System.out.println(linea);
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Cierra el archivo de log.
     * Debe llamarse al finalizar la aplicación.
     */
    public void cerrar() {
        lock.lock();
        try {
            if (writer != null) {
                registrar("Cerrando archivo de log");
                writer.close();
            }
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Devuelve el nombre del archivo de log.
     */
    public String getNombreArchivo() {
        return nombreArchivo;
    }
}
