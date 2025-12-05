package sensores;

import java.io.*;
import java.net.*;
import java.util.*;

public class SensorClient {
    private final String host;
    private final int port;
    private final String tipo;
    private final String room;
    private final int intervalo;
    private final Random rand = new Random();

    public SensorClient(String host, int port, String tipo, String room, int intervalo) {
        this.host = host;
        this.port = port;
        this.tipo = tipo;
        this.room = room;
        this.intervalo = intervalo;
    }

    public void start() {
        try (Socket s = new Socket(host, port);
             PrintWriter out = new PrintWriter(s.getOutputStream(), true)) {
            System.out.println("Sensor " + tipo + "@" + room + " conectado a " + host + ":" + port);
            while (true) {
                String payload = generatePayload();
                out.println(payload);
                System.out.println("Enviado: " + payload);
                Thread.sleep(intervalo);
            }
        } catch (Exception e) {
            System.err.println("Sensor error: " + e.getMessage());
        }
    }

    private String generatePayload() {
        long ts = System.currentTimeMillis();
        switch (tipo.toLowerCase()) {
            case "temperatura":
                double t = 18 + rand.nextDouble() * 15;
                return "SENSOR|TEMPERATURA|" + room + "|" + String.format(Locale.US,"%.2f", t) + "|" + ts;
            case "movimiento":
                boolean mv = rand.nextDouble() < 0.25;
                return "SENSOR|MOVIMIENTO|" + room + "|" + (mv ? "1" : "0") + "|" + ts;
            case "luminosidad":
                int lux = rand.nextInt(1000);
                return "SENSOR|LUMINOSIDAD|" + room + "|" + lux + "|" + ts;
            case "agua":
                int a = 10 + rand.nextInt(91);
                return "SENSOR|AGUA|GLOBAL|" + a + "|" + ts;
            default:
                return "SENSOR|GEN|" + room + "|0|" + ts;
        }
    }

    public static void main(String[] args) {
        String host = args.length>0 ? args[0] : "127.0.0.1";
        int port = args.length>1 ? Integer.parseInt(args[1]) : 5000;
        String tipo = args.length>2 ? args[2] : "temperatura";
        String room = args.length>3 ? args[3] : "SALA";
        int intervalo = args.length>4 ? Integer.parseInt(args[4]) : 2000;
        new SensorClient(host, port, tipo, room, intervalo).start();
    }
}
