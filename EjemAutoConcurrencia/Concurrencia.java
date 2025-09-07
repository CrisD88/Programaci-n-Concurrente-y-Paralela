
/**
 *
 * @author Camau
 */
public class Concurrencia {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Crear hilos para cada tarea de la conducción
        Thread controlarVehiculo = new Thread(new manejarVehiculo());
        Thread atencionEntorno = new Thread(new atencionEntorno());
        Thread mirarGPS = new Thread(new mirarGPS());
        Thread comunicacion = new Thread(new comunicacion());

        // Iniciar los hilos
        controlarVehiculo.start();
        atencionEntorno.start();
        mirarGPS.start();
        comunicacion.start();
        
         try {
            controlarVehiculo.join();
            atencionEntorno.join();
            mirarGPS.join();
            comunicacion.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(">>> Todas las tareas de conducir han terminado <<<");

    }
}

// Tarea: Controlar el vehículo
class manejarVehiculo implements Runnable {
    @Override
    public void run() {
        for (int i = 0; i < 4; i++) {
            System.out.println(">>>>>>><<<<<<<<");
            System.out.println("Manejando el vehiculo: cambio de velocidad, manos al volante, pedales...");
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
        }
    }
}

// Tarea: Atención al entorno
class atencionEntorno implements Runnable {
    @Override
    public void run() {
        for (int i = 0; i < 4; i++) {
            System.out.println("Atendiendo al entorno: revisando espejos, senales y peatones...");
            try { Thread.sleep(1200); } catch (InterruptedException e) {}
        }
    }
}

// Tarea: Mirar el GPS
class mirarGPS implements Runnable {
    @Override
    public void run() {
        for (int i = 0; i < 4; i++) {
            System.out.println("Mirando el GPS: revisando la ruta...");
            try { Thread.sleep(1500); } catch (InterruptedException e) {}
        }
    }
}

// Tarea: Comunicación
class comunicacion implements Runnable {
    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println("Hablando con el copiloto...");
            try { Thread.sleep(2000); } catch (InterruptedException e) {}
        }
    }
}