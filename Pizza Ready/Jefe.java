public class Jefe {
    private String nombre;

    public Jefe(String nombre) {
        this.nombre = nombre;
        }

    public void supervisar() {
        System.out.println("El jefe " + nombre + " supervisa la preparación de las pizzas.\n");
    }
}