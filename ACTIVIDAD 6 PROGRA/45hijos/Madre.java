import java.util.ArrayList;
import java.util.List;

public class Madre {
    public static void main(String[] args) {
        Olla olla = new Olla();
        // Usamos una lista para almacenar los hilos de cada grupo y luego llamar a join()
        List<Hijo> grupoDeHijos;
        // Crear y empezar los hilos de los hijos

        // Turno 1: Gemelos (Maxima prioridad)
        System.out.println("Turno 1: Gemelos (Maxima prioridad)");
        grupoDeHijos = new ArrayList<>();
        for(int i=0; i <12; i++){
            Hijo hijo = new Hijo("Gemelo-" + (i + 1), olla, " Gemelos");
            hijo.setPriority(Thread.MAX_PRIORITY);
            grupoDeHijos.add(hijo);
            hijo.start();
        }
        try {
            for(Hijo hijo : grupoDeHijos) {
                hijo.join();// El hilo principal (Madre) espera a que el hilo hijo termine.
        }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Turno 2: Trillizos (Prioridad 7)
        System.out.println("Turno 2: Trillizos (Prioridad 7)");
        grupoDeHijos = new ArrayList<>();
        for(int i=0; i <12; i++){
            Hijo hijo = new Hijo("Trillizo-" + (i + 1), olla, " Trillizos");
            hijo.setPriority(7);
            grupoDeHijos.add(hijo);
            hijo.start();
        }
        try {
            for(Hijo hijo : grupoDeHijos) {
                hijo.join();// El hilo principal (Madre) espera a que el hilo hijo termine.
        }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }   

        // Turno 3: Cuatrillizos (Prioridad 3)    
        System.out.println("Turno 3: Cuatrillizos (Prioridad 3)");
        grupoDeHijos = new ArrayList<>();
        for(int i=0; i <12; i++){
            Hijo hijo = new Hijo("Cuatrillizo-" + (i + 1), olla, " Cuatrillizos");
            hijo.setPriority(3);
            grupoDeHijos.add(hijo);
            hijo.start();
        }
        try {
            for(Hijo hijo : grupoDeHijos) {
                hijo.join();// El hilo principal (Madre) espera a que el hilo hijo termine.
        }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Turno 4: Hijos únicos (Mínima prioridad)
        System.out.println("Turno 4: Hijos únicos (Minima prioridad)");
        grupoDeHijos = new ArrayList<>();
        for(int i=0; i <9; i++){
            Hijo hijo = new Hijo("Hijo unico-" + (i + 1), olla, " Hijos unicos");
            hijo.setPriority(Thread.MIN_PRIORITY);
            grupoDeHijos.add(hijo);
            hijo.start();
        }
        try {
            for(Hijo hijo : grupoDeHijos) {
                hijo.join();// El hilo principal (Madre) espera a que el hilo hijo termine.
        }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
