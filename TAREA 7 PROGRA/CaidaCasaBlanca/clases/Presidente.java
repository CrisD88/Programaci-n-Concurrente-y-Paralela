package clases;

public class Presidente implements Runnable {
    private Control control;

    public Presidente(Control control) {
        this.control = control;
    }

    @Override
    public void run() {
        System.out.println("Presidente: detenido, esperando ayuda...");
        control.esperarPresidente();
        System.out.println("Presidente: ¡Gracias héroe!");
    }
}