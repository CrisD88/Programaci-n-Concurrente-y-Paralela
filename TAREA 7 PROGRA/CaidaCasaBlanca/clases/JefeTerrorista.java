package clases;

public class JefeTerrorista extends Thread {
    private Control control;

    public JefeTerrorista(Control control) {
        this.control = control;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        control.iniciarAtaque(); // notifica a todos los que esperan
        control.dejarEntrarHeroe(); // da paso al héroe
    }
}