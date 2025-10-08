package clases;

public class control {
    private boolean ataqueListo = false;
    private boolean heroeEntrando = false;
    private boolean bombaDesactivada = false;
    private boolean presidenteRescatado = false;

    // --- Ataque ---
    public synchronized void esperarAtaque() {
        while (!ataqueListo) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void iniciarAtaque() {
        ataqueListo = true;
        System.out.println("Jefe Terrorista: ¡Ataquen ahora!");
        notifyAll();
    }

    // --- Héroe ---
    public synchronized void esperarHeroe() {
        while (!heroeEntrando) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void dejarEntrarHeroe() {
        heroeEntrando = true;
        System.out.println("El héroe puede entrar en acción.");
        notifyAll();
    }

    // --- Bomba ---
    public synchronized void esperarBomba() {
        while (!bombaDesactivada) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void desactivarBomba() {
        bombaDesactivada = true;
        System.out.println("Héroe: ¡bomba desactivada!");
        notifyAll();
    }

    // --- Presidente ---
    public synchronized void esperarPresidente() {
        while (!presidenteRescatado) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void rescatarPresidente() {
        presidenteRescatado = true;
        System.out.println("Héroe: ¡presidente rescatado!");
        notifyAll();
    }
}
