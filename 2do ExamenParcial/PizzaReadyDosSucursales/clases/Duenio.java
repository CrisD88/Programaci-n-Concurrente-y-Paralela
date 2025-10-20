package clases;

public class Duenio {
    private String nombre;

    public Duenio(String nombre) {
        this.nombre = nombre;
    }

    public synchronized void fusionarCajas(CajaSucursal c1, CajaSucursal c2) {
        int total = c1.getGanancias() + c2.getGanancias();
        System.out.println("\n" + nombre + " revisa las ganancias del dia:");
        System.out.println("Sucursal 1: $" + c1.getGanancias());
        System.out.println("Sucursal 2: $" + c2.getGanancias());
        System.out.println("Ganancia total: $" + total + "\n");
    }
}
