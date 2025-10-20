package clases;

public class Cliente extends Thread {
    private String nombre;
    private Empleado empleado;
    private Jefe jefe;
    private CajaSucursal caja;

    public Cliente(String nombre, Empleado empleado, Jefe jefe, CajaSucursal caja) {
        this.nombre = nombre;
        this.empleado = empleado;
        this.jefe = jefe;
        this.caja = caja;
    }

    public String getNombre() {
        return nombre;
    }

    public void pedirPizza() {
        System.out.println(nombre + " pide una pizza.");
    }

    public void recibirPizza() {
        System.out.println(nombre + " ha recibido su pizza.\n");
         caja.agregarGanancia(100); // Supongamos cada pizza cuesta 100
    }

    @Override
    public void run() {
        pedirPizza();

        //Supervisa la aproacion del pedido
        jefe.supervisar(this);

        // El cliente llama al empleado. No necesita wait/notify
        empleado.atenderCliente(this);

        recibirPizza();
    }
}