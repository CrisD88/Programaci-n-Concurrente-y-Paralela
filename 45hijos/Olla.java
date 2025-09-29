public class Olla {
    private int piezaDePollo = 45; 
    
    public void servirPollo(String nombre, String tipoParto){
        if (piezaDePollo > 0) {
            piezaDePollo--;
            String consome = "Consome ilimitado";
            System.out.println("Sirviendo una pieza de pollo a " + nombre + " (Tipo de parto: " + tipoParto + "). Piezas restantes: " + piezaDePollo + ". Tambien tiene " + consome);
        } else {
            System.out.println("No hay mas piezas de pollo disponibles para servir a " + nombre + "Tipo de parto: " + tipoParto);
        }
    try {
        Thread.sleep(100); // Simula el tiempo que tarda en servir
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}
}

