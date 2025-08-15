public class Empleado {
    private String nombre;

    public Empleado(String nombre) {
        this.nombre = nombre;
    }

    public void atenderCliente(Cliente cliente) {
        System.out.println("El empleado " + nombre + " atiende a " + cliente.getNombre() + "\n");
    }

    public void prepararPizza() {
        System.out.println("El empleado " + nombre + " prepara una pizza.\n");
    }

    public void entregarPizza(Cliente cliente) {
        System.out.println("El empleado " + nombre + " entrega la pizza a nombre de " + cliente.getNombre() + "\n");
    }
}
