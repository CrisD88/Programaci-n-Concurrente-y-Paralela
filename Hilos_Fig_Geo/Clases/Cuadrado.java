package Clases;

public class Cuadrado extends Figura {
    double lado;

    public Cuadrado(double lado) {
        this.lado = lado;
    }

    @Override
    public double calcularArea() {
        return lado * lado;
    }

    @Override
    public double calcularPerimetro() {
        return 4 * lado;
    }

    @Override
    public String getNombre() {
        return "Cuadrado";
    }
    
}
