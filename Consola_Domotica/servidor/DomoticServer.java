package servidor;

import control.ControlCentral;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.concurrent.*;

/**
 * DomoticServer: escucha sensores y actuadores en puertos distintos,
 * actualiza el ControlCentral y envía comandos a actuadores.
 */
public class DomoticServer {

    private static int SENSOR_PORT = 5000;
    private static int ACTUATOR_PORT = 5001;

    private final ControlCentral control = new ControlCentral();

    // Map actuadorTipo -> writer para enviar comandos
    private final ConcurrentHashMap<String, PrintWriter> actuadoresConectados = new ConcurrentHashMap<>();

    // Pool para manejar clientes
    private final ExecutorService pool = Executors.newCachedThreadPool();

    public void start(int sensorPort, int actuatorPort) throws IOException {
        SENSOR_PORT = sensorPort;
        ACTUATOR_PORT = actuatorPort;

        System.out.println("Servidor iniciado.");
        System.out.println("Escuchando sensores en puerto " + SENSOR_PORT);
        System.out.println("Escuchando actuadores en puerto " + ACTUATOR_PORT);

        // Lanzar listeners en hilos separados
        pool.execute(() -> listenOnPort(SENSOR_PORT, true));
        pool.execute(() -> listenOnPort(ACTUATOR_PORT, false));

        // Hilo de consola para comandos manuales
        pool.execute(this::consoleThread);
    }

    public static void main(String[] args) {
        int p1 = 5000, p2 = 5001;
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

                if (!esSensor) {
                    // Para actuadores, el primer mensaje idealmente identifica el tipo:
                    // ACTUADOR|TIPO
                    // Si no llega identificación, intentamos leer de forma normal.
                    socket.setSoTimeout(2000); // espera corta para primer mensaje
                    try {
                        String idLine = in.readLine();
                        if (idLine != null && idLine.startsWith("ACTUADOR|")) {
                            String[] p = idLine.split("\\|");
                            if (p.length >= 2) {
                                actuadorTipo = p[1].toUpperCase();
                                actuadoresConectados.put(actuadorTipo, out);
                                System.out.println("[SERVER] Actuador registrado: " + actuadorTipo + " desde " + clientInfo);
                                out.println("SERVER|OK|REGISTERED|" + System.currentTimeMillis());
                            } else {
                                System.out.println("[SERVER] Actuador sin identificación desde " + clientInfo);
                            }
                        } else {
                            if (idLine != null) procesarMensaje(idLine, false);
                        }
                    } catch (SocketTimeoutException te) {
                    } finally {
                        socket.setSoTimeout(0); 
                    }
                }

                while ((line = in.readLine()) != null) {
                    procesarMensaje(line, esSensor);
                }

            } catch (IOException e) {
                System.err.println("Conexión perdida: " + clientInfo);
            } finally {
                if (actuadorTipo != null) {
                    actuadoresConectados.remove(actuadorTipo);
                    System.out.println("[SERVER] Actuador desconectado: " + actuadorTipo);
                }
                try { socket.close(); } catch (IOException ignored) {}
            }
        }
    }

    /**
     * Procesa mensajes entrantes tanto de sensores como de actuadores.
     * Formato esperado:
     * SENSOR|TIPO|VALOR|TIMESTAMP
     * ACTUADOR|TIPO|VALOR|TIMESTAMP  (actuadores que informan su estado)
     */
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

        // Normalizar ciertos formatos (p. ej. SENSOR|AGUA|presion=45)
        if (origen.equals("SENSOR") && tipo.equals("AGUA") && valor.contains("=")) {
            // ejemplo valor "presion=45" -> separar
            String[] kv = valor.split("=");
            if (kv.length >= 2) {
                String key = kv[0].trim().toUpperCase();
                String val = kv[1].trim();
                control.actualizarSensor(key, val);
            }
        } else {
            if (esSensor || origen.equals("SENSOR")) {
                control.actualizarSensor(tipo, valor);
            } else {
                control.actualizarActuador(tipo, valor);
            }
        }

        Map<String, String> comandos = control.procesarLogica();
        if (comandos != null && !comandos.isEmpty()) {
            for (Map.Entry<String, String> e : comandos.entrySet()) {
                String actuador = e.getKey();
                String accion = e.getValue();
                enviarComandoActuador(actuador, accion);
            }
        }
    }

    /**
     * Envía un comando al actuador registrado.
     * Formato del comando enviado al actuador:
     * CMD|TIPO|ACCION|TIMESTAMP
     */
    private void enviarComandoActuador(String tipo, String accion) {
        if (tipo == null || accion == null) return;
        PrintWriter writer = actuadoresConectados.get(tipo.toUpperCase());
        if (writer == null) {
            System.out.println("[SERVER] Actuador " + tipo + " no conectado. No se envía comando.");
            return;
        }
        String cmd = "CMD|" + tipo.toUpperCase() + "|" + accion + "|" + System.currentTimeMillis();
        writer.println(cmd);
        System.out.println("[SERVER] Enviado a " + tipo.toUpperCase() + ": " + cmd);
    }

    /**
     * Hilo de consola para aceptar comandos manuales del administrador.
     * Formato sencillo por línea:
     * TIPO ACCION
     * Ejemplo:
     * LUZ ON
     * CLIMA APAGAR
     * CAMARA GRABAR
     * AGUA CERRAR
     *
     * También soporta:
     * OVERRIDE_OFF TIPO  -> quita override manual (por ejemplo: OVERRIDE_OFF LUZ)
     * EXIT -> termina el servidor (no forzado)
     */
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

                    // Si es comando para quitar override manual:
                    if (tipo.equals("OVERRIDE_OFF") && parts.length >= 2) {
                        String target = parts[1].toUpperCase();
                        control.clearManualOverride(target);
                        continue;
                    }

                    // Establecer override manual para este actuador:
                    control.setManualOverride(tipo, accion);

                    // Enviar inmediatamente al actuador (si está conectado)
                    enviarComandoActuador(tipo, accion);
                } else {
                    System.out.println("[CONSOLE] Comando inválido. Formato: TIPO ACCION");
                }
            }
        } catch (IOException e) {
            System.err.println("[CONSOLE] Error en consola: " + e.getMessage());
        }
    }
}