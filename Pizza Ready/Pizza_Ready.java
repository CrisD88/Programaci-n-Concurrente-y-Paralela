// Simulación de un sistema de pedidos en una pizzería "Pizza Ready" mediante clases...
import clases.Jefe;
import clases.Empleado;
import clases.Cliente;

public class Pizza_Ready {
    public static void main(String[] args) {
        cleanSc();
        System.out.println("¡Bienvenido a Pizza Ready!\n");
        Jefe jefe = new Jefe("Cristopher");
        Empleado empleado1 = new Empleado("Mauricio");
        Empleado empleado2 = new Empleado("Diego");
        
        Cliente cliente1 = new Cliente("Erick");
        Cliente cliente2 = new Cliente("Ángel");
        Cliente cliente3 = new Cliente("Luis");

        jefe.supervisar();

        System.out.println("--- Proceso de Pedido de Pizza ---\n");
        System.out.println("--- Cliente 1 ---");
        cliente1.pedirPizza();
        empleado1.atenderCliente(cliente1);
        empleado1.prepararPizza();
        empleado1.entregarPizza(cliente1);
        cliente1.recibirPizza();

        System.out.println("\n--- Cliente 2 ---");
        cliente2.pedirPizza();
        empleado2.atenderCliente(cliente2);
        empleado2.prepararPizza();
        empleado2.entregarPizza(cliente2);
        cliente2.recibirPizza();

        System.out.println("\n--- Cliente 3 ---");
        cliente3.esperarOrden();
    }

    public static void cleanSc() { //Función para limpiar la consola
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            System.out.println("Error al limpiar la consola: " + e.getMessage());
        }
    }
}
