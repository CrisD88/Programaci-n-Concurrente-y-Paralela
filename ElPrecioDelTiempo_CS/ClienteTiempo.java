import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ClienteTiempo {
    public static void main(String[] args) {
        try {
            // El cliente representa a Sylvia Weis.
            DatagramSocket socket = new DatagramSocket();
            InetAddress servidor = InetAddress.getByName("localhost");
            int puerto = 5000;

            Scanner sc = new Scanner(System.in);
            boolean continuar = true;

            System.out.println("\n\n-----CLIENTE ESCENA FINAL IN TIME-----\n");
            System.out.print("Escena iniciando");
            Thread.sleep(1000);
            System.out.print(".");
            Thread.sleep(1000);
            System.out.print(".");
            Thread.sleep(1000);
            System.out.println(".");

            System.out.println("\nLetrero de 'OUT OF TIME' se apaga dejando encendido solo la palabra 'TIME'.\n");
            Thread.sleep(2500);
            System.out.println("Multitud: ¡Hay tiempo!. (Mientras empujan y distraen a Raymond Leon).\n");
            Thread.sleep(2500);
            System.out.println("Will y Sylvia corren hacia un lugar seguro, dejando atrás el caos y la persecución.\n");
            Thread.sleep(2500);
            System.out.println("Raymond Leon consigue un auto y comienza a perseguirlos.\n");
            Thread.sleep(2500);

            System.out.print("Cliente (Will Salas) iniciando");
            Thread.sleep(1000);
            System.out.print(".");
            Thread.sleep(1000);
            System.out.print(".");
            Thread.sleep(1000);
            System.out.println(".");
            System.out.println("Escribe tus mensajes para Sylvia (termina con 'Tu vocacion es muy clara'):\n");

            while (continuar) {
                System.out.print("Will: ");
                String mensaje = sc.nextLine();
                byte[] buffer = mensaje.getBytes(java.nio.charset.StandardCharsets.UTF_8);

                // Envía mensaje a Will
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, servidor, puerto);
                socket.send(paquete);

                // Espera la respuesta
                byte[] bufferRespuesta = new byte[1024];
                DatagramPacket paqueteRespuesta = new DatagramPacket(bufferRespuesta, bufferRespuesta.length);
                socket.receive(paqueteRespuesta);

                String respuesta = new String(paqueteRespuesta.getData(), 0, paqueteRespuesta.getLength(), java.nio.charset.StandardCharsets.UTF_8);
                System.out.println(respuesta + "\n");

                if (mensaje.equalsIgnoreCase("Tu vocacion es muy clara")) {
                    continuar = false;
                }
            }

            socket.close();
            System.out.println("Escena finalizada. Will recarga su arma mientras procede a robar el banco junto a Sylvia");
            Thread.sleep(1000);
            System.out.print(".");
            Thread.sleep(1000);
            System.out.print(".");
            Thread.sleep(1000);
            System.out.print(".\n\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
