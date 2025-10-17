import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;

public class ServidorTiempo {
    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket(5000);
            System.out.println("\n\n-----SERVIDOR ESCENA FINAL IN TIME-----\n");
            System.out.print("Servidor (Sylvia Weis) iniciando");
            Thread.sleep(1000);
            System.out.print(".");
            Thread.sleep(1000);
            System.out.print(".");
            Thread.sleep(1000);
            System.out.println(".");
            System.out.println("Sylvia Weis esperando mensajes de Will...\n");

            byte[] buffer = new byte[1024];
            boolean continuar = true;

            // 🔹 Map que asocia mensaje -> respuesta
            Map<String, String> respuestas = new HashMap<>();
            respuestas.put("hay que llegar a livingstone", "Sylvia: ...");
            respuestas.put("si llegamos", "Sylvia: ¿Tu crees?.");
            respuestas.put("estas bien?", "Sylvia: ...");
            respuestas.put("no es suficiente", "Sylvia: ...");
            respuestas.put("uno puede llegar, ten el mio", "Sylvia: No, tu el mio...");
            respuestas.put("no te dejare, no, hay tiempo", "Sylvia: ¿Que?.");
            respuestas.put("el del guardian, corre!, corre!", "Sylvia: ¿Cuanto nos queda?.");
            respuestas.put("1 dia, se hace mucho en 1 dia", "Sylvia: ...");
            respuestas.put("...", "Sylvia: Hay bancos mas grandes.");
            respuestas.put("tu vocacion es muy clara", "Sylvia: ...");

            while (continuar) {
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);

                String mensaje = new String(paquete.getData(), 0, paquete.getLength(), java.nio.charset.StandardCharsets.UTF_8).trim();
                System.out.println("Will: " + mensaje);

                // 🔹 Busca la respuesta en el Map
                String respuesta = respuestas.getOrDefault(mensaje, "Sylvia: Will no te entiendo, repite por favor.");

                // 🔹 Si el mensaje fue "adiós sistema", termina el bucle
                if (mensaje.equalsIgnoreCase("Tu vocacion es muy clara")) {
                    continuar = false;
                }

                // Envía la respuesta
                byte[] respuestaBytes = respuesta.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                DatagramPacket paqueteRespuesta = new DatagramPacket(
                        respuestaBytes,
                        respuestaBytes.length,
                        paquete.getAddress(),
                        paquete.getPort()
                );

                socket.send(paqueteRespuesta);
                System.out.println("Sylvia envió: " + respuesta + "\n");
            }

            socket.close();
            System.out.print("Escena finalizada. Sylvia recarga su arma mientras procede a robar el banco junto a Will");
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
