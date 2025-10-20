package clases;

public class Heroe implements Runnable {
    private Control control;

    public Heroe(Control control) {
        this.control = control;
    }

    @Override
    public void run() {
        control.dejarEntrarHeroe();  // espera a que el jefe lo habilite
        System.out.println("Heroe entra en accion...");
        try { Thread.sleep(1500); } catch (InterruptedException e) {}

        control.desactivarBomba();
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        control.rescatarPresidente();
    }
}