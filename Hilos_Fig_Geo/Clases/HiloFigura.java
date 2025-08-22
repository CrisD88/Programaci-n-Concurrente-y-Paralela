package Clases;

public class HiloFigura implements Runnable {
    private Figura figura;

    public HiloFigura(Figura figura) {
        this.figura = figura;
    }   

    @Override
    public void run() {
        synchronized(System.out) {
            System.out.println("Figura: " + figura.getNombre());
            System.out.println("Área: " + figura.calcularArea());
            System.out.println("Perímetro: " + figura.calcularPerimetro());
            System.out.println("-----------------------------");
        }
    }

}
