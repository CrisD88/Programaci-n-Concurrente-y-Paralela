package clases;

public class Empleado {
    private String nombre;
    private boolean ocupado = false; // indica si está atendiendo a alguien

    public Empleado(String nombre) {
        this.nombre = nombre;
    }

    // Método sincronizado: solo un cliente puede entrar a la vez
    public synchronized void atenderCliente(Cliente cliente) {
        while (ocupado) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ocupado = true;

        System.out.println("El empleado " + nombre + " atiende a " + cliente.getNombre());

        try {
            System.out.println("El empleado " + nombre + " prepara la pizza de " + cliente.getNombre());
            Thread.sleep(2000); // simulación preparación
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("El empleado " + nombre + " entregó la pizza a " + cliente.getNombre());

        // Liberamos el empleado y notificamos a otro cliente esperando
        ocupado = false;
        notify();
    }
}