package clases;

public class EquipoRescate implements Runnable {
    private Control control;

    public EquipoRescate(Control control) {
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