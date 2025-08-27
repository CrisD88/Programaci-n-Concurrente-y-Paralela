package Clases;

public class HiloNieto implements Runnable {
    private Figura figura;
    private double area;

    public HiloNieto(Figura figura) {
        this.figura = figura;
    }

    @Override
    public void run() {
        area = figura.calcularArea();
        System.out.println("   [NIETO] " + figura.getNombre() + " | Área: " + area);
    }

    public double getArea() {
        return area;
    }

    public String getNombreFigura() {
        return figura.getNombre();
    }
}


