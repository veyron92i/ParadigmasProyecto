package Cafeteria.Rmi;

import java.io.Serializable;

public class EstadoCafeteria implements Serializable {

    // DESPENSA
    private final int despensaCafe;
    private final int despensaRosquillas;
    private final int cocinerosDespensa;
    private final int vendedoresDespensa;

    // MOSTRADOR
    private final int mostradorCafe;
    private final int mostradorRosquillas;
    private final int clientesMostrador;
    private final int vendedoresMostrador;

    // COCINA
    private final int cocinerosCocina;

    // CAJA
    private final int clientesCaja;
    private final double recaudacionTotal;

    // ÁREA CONSUMICIÓN
    private final int clientesArea;

    // ENTRADA
    private final int clientesEntrada;
    private final int clientesColaEntrada;

    // PARQUE
    private final int clientesParque;

    // SALA DESCANSO
    private final int cocinerosSala;
    private final int vendedoresSala;

    // SIMULACIÓN
    private final int clientesFinalizados;

    public EstadoCafeteria(
            int despensaCafe,
            int despensaRosquillas,
            int cocinerosDespensa,
            int vendedoresDespensa,

            int mostradorCafe,
            int mostradorRosquillas,
            int clientesMostrador,
            int vendedoresMostrador,

            int cocinerosCocina,

            int clientesCaja,
            double recaudacionTotal,

            int clientesArea,

            int clientesEntrada,
            int clientesColaEntrada,

            int clientesParque,

            int cocinerosSala,
            int vendedoresSala,

            int clientesFinalizados
    ) {
        this.despensaCafe = despensaCafe;
        this.despensaRosquillas = despensaRosquillas;
        this.cocinerosDespensa = cocinerosDespensa;
        this.vendedoresDespensa = vendedoresDespensa;

        this.mostradorCafe = mostradorCafe;
        this.mostradorRosquillas = mostradorRosquillas;
        this.clientesMostrador = clientesMostrador;
        this.vendedoresMostrador = vendedoresMostrador;

        this.cocinerosCocina = cocinerosCocina;

        this.clientesCaja = clientesCaja;
        this.recaudacionTotal = recaudacionTotal;

        this.clientesArea = clientesArea;

        this.clientesEntrada = clientesEntrada;
        this.clientesColaEntrada = clientesColaEntrada;

        this.clientesParque = clientesParque;

        this.cocinerosSala = cocinerosSala;
        this.vendedoresSala = vendedoresSala;

        this.clientesFinalizados = clientesFinalizados;
    }

    // Getters
    public int getDespensaCafe() { return despensaCafe; }
    public int getDespensaRosquillas() { return despensaRosquillas; }
    public int getCocinerosDespensa() { return cocinerosDespensa; }
    public int getVendedoresDespensa() { return vendedoresDespensa; }

    public int getMostradorCafe() { return mostradorCafe; }
    public int getMostradorRosquillas() { return mostradorRosquillas; }
    public int getClientesMostrador() { return clientesMostrador; }
    public int getVendedoresMostrador() { return vendedoresMostrador; }

    public int getCocinerosCocina() { return cocinerosCocina; }

    public int getClientesCaja() { return clientesCaja; }
    public double getRecaudacionTotal() { return recaudacionTotal; }

    public int getClientesArea() { return clientesArea; }

    public int getClientesEntrada() { return clientesEntrada; }
    public int getClientesColaEntrada() { return clientesColaEntrada; }

    public int getClientesParque() { return clientesParque; }

    public int getCocinerosSala() { return cocinerosSala; }
    public int getVendedoresSala() { return vendedoresSala; }

    public int getClientesFinalizados() { return clientesFinalizados; }
}
