package clases;

public class CajaSucursal {
    private int ganancias = 0;

    // Agregar venta
    public synchronized void agregarGanancia(int monto) {
        ganancias += monto;
    }

    // Obtener monto actual
    public synchronized int getGanancias() {
        return ganancias;
    }
}
