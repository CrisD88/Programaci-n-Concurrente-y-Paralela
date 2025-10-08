package clases;

public class presidente implements Runnable {
    private control control;

    public presidente(control control) {
        this.control = control;
    }

    @Override
    public void run() {
        System.out.println("Presidente: detenido, esperando ayuda...");
        control.esperarPresidente();
        System.out.println("Presidente: ¡Gracias héroe!");
    }
}
