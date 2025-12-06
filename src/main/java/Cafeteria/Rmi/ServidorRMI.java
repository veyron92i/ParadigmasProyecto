package Cafeteria.Rmi;

import Cafeteria.Cafeteria;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServidorRMI implements IRemotaCafeteria {

    private final Cafeteria cafeteria;

    public ServidorRMI(Cafeteria cafeteria) {
        this.cafeteria = cafeteria;
    }

    @Override
    public EstadoCafeteria obtenerEstado() throws RemoteException {
        return cafeteria.generarEstado();
    }

    @Override
    public void pausar() throws RemoteException {
        // Pausa la simulación real
        cafeteria.getPaso().cerrar();
        System.out.println("RMI: Solicitud de PAUSA recibida.");
    }

    @Override
    public void reanudar() throws RemoteException {
        // Reanuda la simulación real
        cafeteria.getPaso().abrir();
        System.out.println("RMI: Solicitud de REANUDAR recibida.");
    }

    /**
     * Método estático para iniciar el servicio.
     * Se llama desde Cafeteria.java -> iniciar()
     */
    public static void iniciar(Cafeteria cafeteria) {
        try {
            // 1. Crear la instancia del servidor
            ServidorRMI servidor = new ServidorRMI(cafeteria);

            // 2. Exportar el objeto (Stub)
            IRemotaCafeteria stub = (IRemotaCafeteria) UnicastRemoteObject.exportObject(servidor, 0);

            // 3. Crear el registro en el puerto 1099
            Registry registry = LocateRegistry.createRegistry(1099);

            // 4. Publicar el objeto con el nombre CORRECTO
            // DEBE SER "CafeteriaRMI" para coincidir con el cliente
            registry.rebind("CafeteriaRMI", stub);

            cafeteria.getLog().registrar("Servidor RMI iniciado en puerto 1099 con nombre 'CafeteriaRMI'");
            System.out.println("Servidor RMI listo esperando clientes...");

        } catch (Exception e) {
            System.err.println("Error iniciando Servidor RMI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
