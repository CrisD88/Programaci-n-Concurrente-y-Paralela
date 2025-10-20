public class Hijo extends Thread {
    private Olla olla;
    private String tipoDeParto;

    public Hijo(String nombre, Olla olla, String tipoDeParto) {
        super(nombre);
        this.olla = olla;
        this.tipoDeParto = tipoDeParto;
    }
    @Override
    public void run() {
        // Todos los hijos de un grupo competirán hasta que les toque.
        olla.servirPollo(getName(), tipoDeParto);
        Thread.yield(); // Cede el turno a otro hilo
    }
}
