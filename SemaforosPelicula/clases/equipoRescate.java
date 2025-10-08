package clases;

public class equipoRescate implements Runnable {
    private control control;

    public equipoRescate(control control) {
        this.control = control;
    }

    @Override
    public void run() {
        control.esperarBomba();
        System.out.println("Equipo de Rescate: bomba asegurada.");
        control.esperarPresidente();
        System.out.println("Equipo de Rescate: presidente protegido. Zona asegurada.");
    }
}
