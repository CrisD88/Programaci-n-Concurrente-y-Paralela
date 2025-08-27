
package Clases;

public class hijo implements Runnable {
    private Figura[] figuras;
    private int numeroHijo;
    private double areaMayor;
    private String nombreMayor;

    public hijo(Figura[] figuras, int numeroHijo) {
        this.figuras = figuras;
        this.numeroHijo = numeroHijo;
    }

    @Override
    public void run() {
        System.out.println("[HIJO " + numeroHijo + "] Iniciando...");

        HiloNieto[] nietos = new HiloNieto[figuras.length];
        Thread[] hilosNietos = new Thread[figuras.length];

        for (int i = 0; i < figuras.length; i++) {
            nietos[i] = new HiloNieto(figuras[i]);
            hilosNietos[i] = new Thread(nietos[i]);
            hilosNietos[i].start();
        }

        // Esperar a que terminen los nietos
        for (int i = 0; i < hilosNietos.length; i++) {
            try {
                hilosNietos[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Calcular el área mayor entre sus nietos
        if (nietos[0].getArea() > nietos[1].getArea()) {
            areaMayor = nietos[0].getArea();
            nombreMayor = nietos[0].getNombreFigura();
        } else {
            areaMayor = nietos[1].getArea();
            nombreMayor = nietos[1].getNombreFigura();
        }

        System.out.println("[HIJO " + numeroHijo + "] Área mayor: " +
                nombreMayor + " = " + areaMayor);

        System.out.println("[HIJO " + numeroHijo + "] Terminó");
    }

    public double getAreaMayor() {
        return areaMayor;
    }

    public String getNombreMayor() {
        return nombreMayor;
    }
}
