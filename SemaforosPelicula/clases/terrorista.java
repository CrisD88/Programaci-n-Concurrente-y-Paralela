package clases;

public class terrorista extends Thread {
    private String nombre;
    private control control;

    public terrorista(String nombre, control control) {
        this.nombre = nombre;
        this.control = control;
    }

    @Override
    public void run() {
        System.out.println(nombre + " se prepara para atacar...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(nombre + " listo.");
    }
}
