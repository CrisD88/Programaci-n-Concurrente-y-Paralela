

import clases.Empleado;
import clases.Cliente;
import clases.Jefe;
import clases.CajaSucursal;

public class Pizza_Ready {
    public static void main(String[] args) {;

        // Sucursal 1
        Empleado empleado11 = new Empleado("Mauricio");
        Empleado empleado12 = new Empleado("Diego");
        Empleado empleado13 = new Empleado("Ana");
        Jefe jefe1 = new Jefe("Gael");
        CajaSucursal caja1 = new CajaSucursal();

        Cliente cliente11 = new Cliente("Erick", empleado11, jefe1, caja1);
        Cliente cliente12 = new Cliente("Angel", empleado12, jefe1, caja1);
        Cliente cliente13 = new Cliente("Luis", empleado13, jefe1, caja1);

        //Sucursal 2
        Empleado empleado21 = new Empleado("Sofia");
        Empleado empleado22 = new Empleado("Camila");
        Empleado empleado23 = new Empleado("Valentina");
        Jefe jefe2 = new Jefe("Mateo");
        CajaSucursal caja2 = new CajaSucursal();
    
        Cliente cliente21 = new Cliente("Carlos", empleado21, jefe2, caja2);
        Cliente cliente22 = new Cliente("Ana", empleado22, jefe2, caja2);
        Cliente cliente23 = new Cliente("Marta", empleado23, jefe2, caja2);

        cliente11.start();
        cliente12.start();
        cliente13.start();
        cliente21.start();
        cliente22.start();
        cliente23.start();

        try {
            cliente11.join();
            cliente12.join();
            cliente13.join();
            cliente21.join();
            cliente22.join();
            cliente23.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Dueño revisa las ganancias
        clases.Duenio duenio = new clases.Duenio("Roberto");
        duenio.fusionarCajas(caja1, caja2);
        System.out.println(">>> Todos los pedidos fueron entregados. Fin de la simulacion <<<");
    }
}
