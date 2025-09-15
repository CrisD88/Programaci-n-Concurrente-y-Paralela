/*Programa en el cual un hilo padre asigna a sus 4 hijos una figura geométrica (triangulo, cuadrado, trapecio y rectángulo) 
para que cada uno calcule su área y perímetro.*/

//import java.util.Scanner;
import Clases.Triangulo;
import Clases.Cuadrado;
//import Clases.HiloFigura;
import Clases.Rectangulo;
import Clases.Trapecio;
import Clases.Figura;
import Clases.HiloAbuelo;
//import Clases.hijo;
//import Clases.HiloNieto;

public class Figuras_Geo {
    public static void main(String[] args) {

        // Crear figuras
        Figura triangulo = new Triangulo(5, 10);
        Figura cuadrado = new Cuadrado(4);
        Figura rectangulo = new Rectangulo(6, 3);
        Figura trapecio = new Trapecio(5, 3, 4, 6, 2);

        // Arreglos de figuras
        Figura[] hijo1 = {triangulo, trapecio};
        Figura[] hijo2 = {cuadrado, rectangulo};


        // Crear abuelo
        Thread abuelo = new Thread(new HiloAbuelo(hijo1, hijo2));
        abuelo.start();

        try {
            abuelo.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n\tFIN DEL PROGRAMA");
    }
}