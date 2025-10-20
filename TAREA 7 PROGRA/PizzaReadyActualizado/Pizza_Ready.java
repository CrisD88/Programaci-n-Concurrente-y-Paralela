import clases.Empleado;
import clases.Cliente;
import clases.Jefe;

public class Pizza_Ready {
    public static void main(String[] args) {
        Empleado empleado1 = new Empleado("Mauricio");
        Empleado empleado2 = new Empleado("Diego");
        Empleado empleado3 = new Empleado("Ana");
        Jefe jefe1 = new Jefe("Gael");

        Cliente cliente1 = new Cliente("Erick", empleado1, jefe1);
        Cliente cliente2 = new Cliente("Angel", empleado2, jefe1);
        Cliente cliente3 = new Cliente("Luis", empleado3, jefe1);

        cliente1.start();
        cliente2.start();
        cliente3.start();

        try {
            cliente1.join();
            cliente2.join();
            cliente3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(">>> Todos los pedidos fueron entregados. Fin de la simulación <<<");
    }
}
