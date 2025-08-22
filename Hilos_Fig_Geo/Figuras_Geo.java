/*Programa en el cual un hilo padre asigna a sus 4 hijos una figura geométrica (triangulo, cuadrado, trapecio y rectángulo) 
para que cada uno calcule su área y perímetro.*/

import java.util.Scanner;
import Clases.Triangulo;
import Clases.Cuadrado;
import Clases.HiloFigura;
import Clases.Rectangulo;
import Clases.Trapecio;
import Clases.Figura;

public class Figuras_Geo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        //Creación de figuras
        Figura triangulo = new Triangulo(5, 10);
        Figura cuadrado = new Cuadrado(4);
        Figura rectangulo = new Rectangulo(6, 3);
        Figura trapecio = new Trapecio(5, 3, 4, 6, 2);

        //Creación de hilos para cada figura
        Thread hilo1 = new Thread(new HiloFigura(triangulo));
        Thread hilo2 = new Thread(new HiloFigura(cuadrado));
        Thread hilo3 = new Thread(new HiloFigura(rectangulo));
        Thread hilo4 = new Thread(new HiloFigura(trapecio));

        System.out.println("\n\tFIGURAS GEOMETRICAS\n");

        //Iniciar los hilos
        hilo1.start();
        hilo2.start();  
        hilo3.start();
        hilo4.start();

        //Esperar a que los hilos terminen
        try {
            hilo1.join();
            hilo2.join();
            hilo3.join();
            hilo4.join();
        } catch (InterruptedException e) {
            System.out.println("Error al esperar a los hilos: " + e.getMessage());
        }
    }
    
}
