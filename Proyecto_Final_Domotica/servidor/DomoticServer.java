package servidor;

import control.ControlCentral;
import gui.AdminGUI;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class DomoticServer {

    private static int SENSOR_PORT = 5000;
    private static int ACTUATOR_PORT = 5001;

    private final ControlCentral control = new ControlCentral();

    // Map actuadorTipo -> writer para enviar comandos 
    private final ConcurrentHashMap<String, PrintWriter> actuadoresConectados = new ConcurrentHashMap<>();

    private final ExecutorService pool = Executors.newCachedThreadPool();

    public void start(int sensorPort, int actuatorPort) throws IOException {
        SENSOR_PORT = sensorPort;
        ACTUATOR_PORT = actuatorPort;

        System.out.println("Servidor iniciado (modo integrado).");
        System.out.println("Escuchando actuadores en puerto " + ACTUATOR_PORT + " (sensores simulados en GUI).");

        // Si un CommandListener de ControlCentral genera comando, lo enviamos a actuadores conectados por red
        control.addCommandListener((tipo, accion) -> {
            // Normalizar key para búsqueda
            String lookup = tipo.toUpperCase();
            if (lookup.contains("_")) lookup = lookup.split("_")[0];
            PrintWriter w = actuadoresConectados.get(lookup);
            if (w != null) {
                String cmd = "CMD|" + tipo + "|" + accion + "|" + System.currentTimeMillis();
                w.println(cmd);
                System.out.println("[SERVER] Enviado por red a " + lookup + ": " + cmd);
            } else {
                // si no hay actuador real, lo notificamos en log para depuración
                System.out.println("[SERVER] No conectado (por red) el actuador: " + lookup + " -> " + accion);
            }
        });

        // Iniciar listener de actuadores reales (opcional)
        pool.execute(() -> listenOnPort(ACTUATOR_PORT, false));

        // Lanzar GUI en EDT, pasándole la instancia de control
        javax.swing.SwingUtilities.invokeLater(() -> {
            AdminGUI gui = new AdminGUI(control);
            gui.setVisible(true);
        });

        // Hilo de consola para comandos manuales (opcional)
        pool.execute(this::consoleThread);
    }

    public static void main(String[] args) {
        int p2 = 5001;
        int p1 = 5000; // no usado para sensores en esta versión integrada
        if (args.length >= 2) {
            try {
                p1 = Integer.parseInt(args[0]);
                p2 = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {}
        }

        DomoticServer server = new DomoticServer();
        try {
            server.start(p1, p2);
        } catch (IOException e) {
            System.err.println("Error iniciando servidor: " + e.getMessage());
        }
    }

    private void listenOnPort(int port, boolean esSensor) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket client = serverSocket.accept();
                pool.execute(new ClientHandler(client, esSensor));
            }
        } catch (IOException e) {
            System.err.println("Error en puerto " + port + ": " + e.getMessage());
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;
        private final boolean esSensor;
        private String actuadorTipo = null;

        ClientHandler(Socket socket, boolean esSensor) {
            this.socket = socket;
            this.esSensor = esSensor;
        }

        @Override
        public void run() {
            String clientInfo = socket.getRemoteSocketAddress().toString();
            System.out.println((esSensor ? "[SENSOR]" : "[ACTUADOR]") + " Conectado: " + clientInfo);

            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ) {
                String line;
                socket.setSoTimeout(2000);
                try {
                    String idLine = in.readLine();
                    if (idLine != null && idLine.startsWith("ACTUADOR|")) {
                        String[] p = idLine.split("\\|");
                        if (p.length >= 2) {
                            actuadorTipo = p[1].toUpperCase();
                            actuadoresConectados.put(actuadorTipo, out);
                            System.out.println("[SERVER] Actuador registrado por red: " + actuadorTipo);
                            out.println("SERVER|OK|REGISTERED|" + System.currentTimeMillis());
                        }
                    } else if (idLine != null) {
                        // procesar si fuera mensaje normal
                        procesarMensaje(idLine, esSensor);
                    }
                } catch (Exception ignored) {
                } finally {
                    try { socket.setSoTimeout(0); } catch (Exception ignored) {}
                }

                while ((line = in.readLine()) != null) {
                    procesarMensaje(line, esSensor);
                }
            } catch (IOException e) {
                System.err.println("Conexión perdida: " + clientInfo);
            } finally {
                if (actuadorTipo != null) {
                    actuadoresConectados.remove(actuadorTipo);
                    System.out.println("[SERVER] Actuador desconectado por red: " + actuadorTipo);
                }
                try { socket.close(); } catch (IOException ignored) {}
            }
        }
    }

    private void procesarMensaje(String mensaje, boolean esSensor) {
        if (mensaje == null || mensaje.trim().isEmpty()) return;
        String[] partes = mensaje.split("\\|");
        if (partes.length < 3) {
            System.err.println("Mensaje inválido: " + mensaje);
            return;
        }

        String origen = partes[0].toUpperCase();
        String tipo = partes[1].toUpperCase();
        String valor = partes.length >= 3 ? partes[2] : "";

        if (esSensor || origen.equals("SENSOR")) {
            // reenviamos al control 
            control.actualizarSensor(tipo, valor);
        } else {
            // mensajes desde actuador informando su estado
            control.actualizarActuador(tipo, valor);
        }
    }

    private void consoleThread() {
        try (BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            System.out.println("[CONSOLE] Ingrese comandos (ej. LUZ ON). Escriba EXIT para salir.");
            while ((line = console.readLine()) != null) {
                line = line.trim();
                if (line.equalsIgnoreCase("EXIT")) {
                    System.out.println("[CONSOLE] Saliendo...");
                    System.exit(0);
                }
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");
                if (parts.length >= 2) {
                    String tipo = parts[0].toUpperCase();
                    String accion = parts[1].toUpperCase();

                    if (tipo.equals("OVERRIDE_OFF") && parts.length >= 2) {
                        String target = parts[1].toUpperCase();
                        control.clearManualOverride(target);
                        continue;
                    }

                    control.setManualOverride(tipo, accion);
                } else {
                    System.out.println("[CONSOLE] Comando inválido. Formato: TIPO ACCION");
                }
            }
        } catch (IOException e) {
            System.err.println("[CONSOLE] Error en consola: " + e.getMessage());
        }
    }
}
