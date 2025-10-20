package clases;
public class Jefe {
    private String nombre;
    private boolean ocupado = false; // indica si está atendiendo a alguien


    public Jefe(String nombre) {
        this.nombre = nombre;
        }

    public synchronized void supervisar(Cliente cliente){
        while (ocupado) {
            try{
                wait(); //Esperamos si el jefe esta ocupado
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        ocupado = true;
        System.out.println("El jefe " + nombre + " supervisa el pedido de " + cliente.getNombre());
        try{
            Thread.sleep(500);
        } catch (InterruptedException e){
            e.printStackTrace();
        }

    System.out.println("El jefe " + nombre + " termino de supervisar el pedido de " + cliente.getNombre());
    ocupado = false;       // libera al jefe
    notify();              // despierta al siguiente empleaod que espera
   

    }
}