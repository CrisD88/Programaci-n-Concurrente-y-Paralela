package clases;

public class Terrorista extends Thread {
    private Control control;
    private String nombre;
    

    public Terrorista(String nombre, Control control) {
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