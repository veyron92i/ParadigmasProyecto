package Cafeteria.Rmi;

import Cafeteria.Cafeteria;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServidorRMI implements IRemotaCafeteria {

    private final Cafeteria cafeteria;

    public ServidorRMI(Cafeteria cafeteria) {
        this.cafeteria = cafeteria;
    }

    @Override
    public EstadoCafeteria obtenerEstado() {
        return cafeteria.generarEstado();
    }

    @Override
    public void pausar() {
        cafeteria.getPaso().cerrar();   // ✔ MÉTODO REAL DE Paso
    }

    @Override
    public void reanudar() {
        cafeteria.getPaso().abrir();    // ✔ MÉTODO REAL DE Paso
    }

    public static void iniciar(Cafeteria cafeteria) throws Exception {
        ServidorRMI servidor = new ServidorRMI(cafeteria);
        IRemotaCafeteria stub =
                (IRemotaCafeteria) UnicastRemoteObject.exportObject(servidor, 0);

        Registry registry = LocateRegistry.createRegistry(1099);
        registry.rebind("CafeteriaRMI", stub);

        System.out.println("Servidor RMI listo.");
    }
}
