

import clases.Control;
import clases.Desenlace;
import clases.EquipoRescate;
import clases.Heroe;
import clases.JefeTerrorista;
import clases.Presidente;
import clases.Terrorista;

public class CaidaCasaBlanca {
    public static void main(String[] args) {
        Control control = new Control();

        Thread t1 = new Thread(new Terrorista("Terrorista A", control));
        Thread t2 = new Thread(new Terrorista("Terrorista B", control));
        Thread jefe = new Thread(new JefeTerrorista(control));
        Thread heroe = new Thread(new Heroe(control));
        Thread equipo = new Thread(new EquipoRescate(control));
        Thread presidente = new Thread(new Presidente(control));
        Thread desenlace = new Thread(new Desenlace(control));

        t1.start();
        t2.start();
        jefe.start();
        heroe.start();
        equipo.start();
        presidente.start();
        desenlace.start();
    }
}