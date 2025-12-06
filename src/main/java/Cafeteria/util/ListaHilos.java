package Cafeteria.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase auxiliar para mostrar listas de hilos en la GUI.
 * Es thread-safe y muy simple.
 */
public class ListaHilos {

    private final List<String> lista = new ArrayList<>();
    private final String nombre;

    public ListaHilos(String nombre) {
        this.nombre = nombre;
    }

    public synchronized void meter(String id) {
        lista.add(id);
    }

    public synchronized void sacar(String id) {
        lista.remove(id);
    }

    public synchronized int size() {
        return lista.size();
    }

    public synchronized List<String> getLista() {
        return new ArrayList<>(lista); // copia segura
    }

    @Override
    public synchronized String toString() {
        return nombre + ": " + lista.toString();
    }
}
