package Clases;

public class HiloAbuelo implements Runnable {
    private Figura[] hijo1Figuras;
    private Figura[] hijo2Figuras;

    public HiloAbuelo(Figura[] hijo1Figuras, Figura[] hijo2Figuras) {
        this.hijo1Figuras = hijo1Figuras;
        this.hijo2Figuras = hijo2Figuras;
    }

    @Override
public void run() {
    System.out.println("[ABUELO] Iniciando hijos...");

    hijo h1 = new hijo(hijo1Figuras, 1);
    hijo h2 = new hijo(hijo2Figuras, 2);

    Thread t1 = new Thread(h1);
    Thread t2 = new Thread(h2);

    // Ejecutar los dos HIJOS para que sea concurrente 
    t1.start();
    t2.start();                  
    try {
        t1.join();
        t2.join();
    } catch (InterruptedException e) {
        e.printStackTrace();
    } 

    // Comparar áreas
    double areaMayor;
    String nombreMayor;
    if (h1.getAreaMayor() > h2.getAreaMayor()) {
        areaMayor = h1.getAreaMayor();
        nombreMayor = h1.getNombreMayor();
    } else {
        areaMayor = h2.getAreaMayor();
        nombreMayor = h2.getNombreMayor();
    }

    System.out.println("[ABUELO] La figura con mayor area de todas es: " +
            nombreMayor + " = " + areaMayor);

    System.out.println("[ABUELO] Termino");
}

    
}
