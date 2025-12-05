package sensores;
import java.io.*;
import java.net.*;
import java.util.*;

public class SensorClient {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 5000;
    private static final Random rand = new Random();
    private final String sensorType;
    private final int intervalMs;

    public SensorClient(String sensorType, int intervalMs) {
        this.sensorType = sensorType;
        this.intervalMs = intervalMs;
    }

    public void start() {
        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            System.out.println("Sensor " + sensorType + " conectado al servidor.");

            while (true) {
                String payload = generatePayload();
                out.println(payload);
                System.out.println("Enviado: " + payload);
                Thread.sleep(intervalMs);
            }
        } catch (Exception e) {
            System.err.println("Error en sensor: " + e.getMessage());
        }
    }

    private String generatePayload() {
        long ts = System.currentTimeMillis();
        switch (sensorType.toLowerCase()) {
            case "temp":
            case "temperatura":
                double temp = 18 + rand.nextDouble() * 15; // 18..33
                return "SENSOR|TEMPERATURA|" + String.format("%.2f", temp) + "|" + ts;
            case "mov":
            case "movimiento":
                boolean mv = rand.nextDouble() < 0.2; // 20% de probabilidad
                return "SENSOR|MOVIMIENTO|" + (mv ? "1" : "0") + "|" + ts;
            case "lux":
            case "luminosidad":
                int lux = rand.nextInt(1000); // 0..999 lx
                return "SENSOR|LUMINOSIDAD|" + lux + "|" + ts;
            case "agua":
                int agua = 0 + rand.nextInt(81);  // 0..100
                return "SENSOR|AGUA|" + agua + "|" + ts;
            default:
                return "SENSOR|GEN|0|" + ts;
        }
    }

    public static void main(String[] args) {
        String tipo = args.length > 0 ? args[0] : "temperatura";
        int intervalo = args.length > 1 ? Integer.parseInt(args[1]) : 2000;
        SensorClient client = new SensorClient(tipo, intervalo);
        client.start();
    }
}