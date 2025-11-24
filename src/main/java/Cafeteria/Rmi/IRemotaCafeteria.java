package Cafeteria.Rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemotaCafeteria extends Remote {
    EstadoCafeteria obtenerEstado() throws RemoteException;
    void pausar() throws RemoteException;
    void reanudar() throws RemoteException;
}
