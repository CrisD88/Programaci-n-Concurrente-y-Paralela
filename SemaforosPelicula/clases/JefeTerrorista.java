package clases;

public class JefeTerrorista extends Thread {
    private control control;

    public JefeTerrorista(control control) {
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
