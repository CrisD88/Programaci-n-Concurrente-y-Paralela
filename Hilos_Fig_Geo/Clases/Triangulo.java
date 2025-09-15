
package Clases;

public class Triangulo extends Figura {
    double base, altura;
    
    public Triangulo(double base, double altura) {
        this.base = base;
        this.altura = altura;
    }

    @Override
    public double calcularArea() {
        return (base * altura) / 2;
    }

    @Override
    public double calcularPerimetro() {    
        return 3 * base;
    }

    @Override
    public String getNombre() {   
        return "Triangulo";
    }
}
