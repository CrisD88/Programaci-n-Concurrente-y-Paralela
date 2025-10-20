package clases;

public class Desenlace implements Runnable {
    private Control control;

    public Desenlace(Control control) {
        this.control = control;
    }

    @Override
    public void run() {
        control.esperarPresidente(); // desenlace solo cuando todo está resuelto
        System.out.println("\n--- DESENLACE ---");
        System.out.println("Terroristas capturados.");
        System.out.println("Heroe ovacionado.");
        System.out.println("Casa Blanca recupera la calma.");
    }
}