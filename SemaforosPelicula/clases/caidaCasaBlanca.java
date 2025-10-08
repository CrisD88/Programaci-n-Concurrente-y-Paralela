package clases;

public class caidaCasaBlanca {
    public static void main(String[] args) {
        control control = new control();

        Thread t1 = new Thread(new terrorista("Terrorista A", control));
        Thread t2 = new Thread(new terrorista("Terrorista B", control));
        Thread jefe = new Thread(new JefeTerrorista(control));
        Thread heroe = new Thread(new heroe(control));
        Thread equipo = new Thread(new equipoRescate(control));
        Thread presidente = new Thread(new presidente(control));
        Thread desenlace = new Thread(new desenlace(control));

        t1.start();
        t2.start();
        jefe.start();
        heroe.start();
        equipo.start();
        presidente.start();
        desenlace.start();
    }
}
