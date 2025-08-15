public class Cliente {
    private String nombre;

    public Cliente(String nombre) {
        this.nombre = nombre;
    }  

    public String getNombre() {
        return nombre;
    }
    
    public void esperarOrden() {
        System.out.println(nombre + " está esperando su orden.\n");
    }

    public void pedirPizza() {
        System.out.println(nombre + " ha pedido una pizza.\n");
    }
    
    public void recibirPizza() {
        System.out.println(nombre + " ha recibido su pizza.\n");
    }
}
