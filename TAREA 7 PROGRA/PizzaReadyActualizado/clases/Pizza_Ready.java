package clases;

import clases.Empleado;
import clases.Cliente;
import clases.Jefe;

public class Pizza_Ready {
    public static void main(String[] args) {
        cleanSc();
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
