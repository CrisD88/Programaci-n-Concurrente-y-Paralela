package actuadores;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ActuatorClient {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 5001;

    private final String actuatorType; 

    public ActuatorClient(String actuatorType) {
        this.actuatorType = actuatorType.trim().toUpperCase();
    }

    public void start() {
        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("[ACTUADOR " + actuatorType + "] Conectando a " + HOST + ":" + PORT);

            // IDENTIFICACIÓN: enviar una sola línea que el servidor espera
            out.println("ACTUADOR|" + actuatorType);
            System.out.println("[ACTUADOR " + actuatorType + "] Registrado (enviado: ACTUADOR|" + actuatorType + ")");

            // Leer posible ACK del servidor 
            socket.setSoTimeout(2000);
            try {
                String ack = in.readLine();
                if (ack != null) System.out.println("[ACTUADOR " + actuatorType + "] ACK servidor: " + ack);
            } catch (IOException ignore) {
                // no llegó ACK en tiempo, seguimos de todas formas
            } finally {
                socket.setSoTimeout(0);
            }

            // Quedarse escuchando comandos del servidor
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("[ACTUADOR " + actuatorType + "] Recibido: " + line);
                handleServerCommand(line);
            }

            System.out.println("[ACTUADOR " + actuatorType + "] Conexión cerrada por servidor.");

        } catch (IOException e) {
            System.err.println("[ACTUADOR " + actuatorType + "] Error: " + e.getMessage());
        }
    }

    private void handleServerCommand(String cmdLine) {
        if (cmdLine == null || cmdLine.trim().isEmpty()) return;

        // El servidor envía: CMD|TIPO|ACCION|TIMESTAMP
        String[] parts = cmdLine.split("\\|");
        if (parts.length < 3) {
            System.out.println("[ACTUADOR " + actuatorType + "] Comando inválido: " + cmdLine);
            return;
        }

        String prefix = parts[0].toUpperCase(); 
        String tipo = parts[1].toUpperCase();
        String accion = parts[2].toUpperCase();

        if (!"CMD".equals(prefix)) {
            System.out.println("[ACTUADOR " + actuatorType + "] Mensaje no-CMD recibido: " + cmdLine);
            return;
        }

        // Solo procesar comandos dirigidos a este actuador
        if (!tipo.equals(actuatorType)) return;

        // Procesar acciones acordes con ControlCentral
        switch (actuatorType) {
            case "LUZ":
                if (accion.equals("ON")) System.out.println("LUZ: encendida");
                else if (accion.equals("OFF")) System.out.println("LUZ: apagada");
                else System.out.println("LUZ: acción desconocida -> " + accion);
                break;

            case "CLIMA":
                if (accion.equals("ENCENDER")) System.out.println("CLIMA: encendido");
                else if (accion.equals("APAGAR")) System.out.println("CLIMA: apagado");
                else System.out.println("CLIMA: acción desconocida -> " + accion);
                break;

            case "CAMARA":
            case "CAMARAS":
                if (accion.equals("GRABAR")) System.out.println("CAMARA: grabando");
                else if (accion.equals("STOP")) System.out.println("CAMARA: detenido");
                else System.out.println("CAMARA: acción desconocida -> " + accion);
                break;

            case "AGUA":
                if (accion.equals("ABRIR")) System.out.println("AGUA: válvula abierta");
                else if (accion.equals("CERRAR")) System.out.println("AGUA: válvula cerrada");
                else System.out.println("AGUA: acción desconocida -> " + accion);
                break;

            case "PERSIANA":
            case "PERSIANAS":
                if (accion.equals("UP") || accion.equals("SUBIR")) System.out.println("PERSIANA: subiendo");
                else if (accion.equals("DOWN") || accion.equals("BAJAR")) System.out.println("PERSIANA: bajando");
                else System.out.println("PERSIANA: acción desconocida -> " + accion);
                break;

            case "CERRADURA":
                if (accion.equals("LOCK") || accion.equals("BLOQUEAR")) System.out.println("CERRADURA: bloqueada");
                else if (accion.equals("UNLOCK") || accion.equals("DESBLOQUEAR")) System.out.println("CERRADURA: desbloqueada");
                else System.out.println("CERRADURA: acción desconocida -> " + accion);
                break;

            default:
                System.out.println("[ACTUADOR " + actuatorType + "] No implementado para acciones: " + accion);
        }
    }

    public static void main(String[] args) {
        String tipo = args.length > 0 ? args[0] : "CLIMA";
        ActuatorClient client = new ActuatorClient(tipo);
        client.start();
    }
}