package clases;

public class heroe implements Runnable {
    private control control;

    public heroe(control control) {
        this.control = control;
    }

    @Override
    public void run() {
        control.dejarEntrarHeroe();  // espera a que el jefe lo habilite
        System.out.println("Héroe entra en acción...");
        try { Thread.sleep(1500); } catch (InterruptedException e) {}

        control.desactivarBomba();
        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        control.rescatarPresidente();
    }
}
