package control;

import java.util.*;
import java.util.concurrent.*;

/**
 * ControlCentral.
 * - Soporta sensores y actuadores por HABITACIÓN.
 * - Simulación de sensores por (tipo,room) bajo demanda.
 * - Notifica listeners de sensor y comandos.
 */
public class ControlCentral {

    public interface SensorUpdateListener { void onSensorUpdate(String tipoRoom, String valor); }
    public interface CommandListener { void onCommand(String actuadorTipo, String accion); }

    private final ConcurrentHashMap<String,String> estadosSensores = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String,String> estadosActuadores = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String,String> manualOverrides = new ConcurrentHashMap<>();

    private final CopyOnWriteArrayList<SensorUpdateListener> sensorListeners = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<CommandListener> commandListeners = new CopyOnWriteArrayList<>();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(8);
    private final ConcurrentHashMap<String, ScheduledFuture<?>> sensorFutures = new ConcurrentHashMap<>();
    private final Random rand = new Random();

    // Habitaciones conocidas 
    public static final String[] ROOMS = {"SALA","COCINA","BAÑO","DORMITORIO1","DORMITORIO2"};

    // Intervalo por sensor (ms)
    private long sensorIntervalMs = 2000;

    public ControlCentral() { }

    public ControlCentral(long sensorIntervalMs) { this.sensorIntervalMs = sensorIntervalMs; }

    // ---- listeners ----
    public void addSensorListener(SensorUpdateListener l) { if (l!=null) sensorListeners.add(l); }
    public void removeSensorListener(SensorUpdateListener l) { sensorListeners.remove(l); }
    public void addCommandListener(CommandListener l) { if (l!=null) commandListeners.add(l); }
    public void removeCommandListener(CommandListener l) { commandListeners.remove(l); }

    // ---- sensor key helpers ----
    private String key(String tipo, String room) {
        if (room == null || room.isEmpty()) return tipo.toUpperCase();
        return tipo.toUpperCase() + "_" + room.toUpperCase();
    }
    private String actuatorKey(String tipo, String room) {
        if (room == null || room.isEmpty()) return tipo.toUpperCase();
        return tipo.toUpperCase() + "_" + room.toUpperCase();
    }

    // ---- start/stop sensors  ----
    /**
     * startSensor("MOVIMIENTO","COCINA")
     */
    public void startSensor(String tipo, String room) {
        if (tipo==null) return;
        String sk = key(tipo, room);
        if (sensorFutures.containsKey(sk)) return;

        Runnable task = () -> {
            try {
                switch (tipo.toUpperCase()) {
                    case "TEMPERATURA":
                        double temp = 18.0 + rand.nextDouble() * 15.0; //18..33
                        actualizarSensor(sk, String.format(Locale.US,"%.2f", temp));
                        break;
                    case "MOVIMIENTO":
                        boolean mv = rand.nextDouble() < 0.25;
                        actualizarSensor(sk, mv ? "1" : "0");
                        break;
                    case "LUMINOSIDAD":
                        int lux = rand.nextInt(1000);
                        actualizarSensor(sk, String.valueOf(lux));
                        break;
                    case "AGUA":
                        int agua = 10 + rand.nextInt(91);
                        actualizarSensor("AGUA_GLOBAL", String.valueOf(agua));
                        break;
                    default:
                        actualizarSensor(sk, "0");
                }
            } catch (Exception ex) {
                System.err.println("[Control] Err sim sensor " + sk + ": " + ex.getMessage());
            }
        };

        ScheduledFuture<?> f = scheduler.scheduleAtFixedRate(task, 0, sensorIntervalMs, TimeUnit.MILLISECONDS);
        sensorFutures.put(sk, f);
        System.out.println("[Control] Sensor START " + sk);
    }

    public void stopSensor(String tipo, String room) {
        if (tipo==null) return;
        String sk = key(tipo, room);
        ScheduledFuture<?> f = sensorFutures.remove(sk);
        if (f != null) {
            f.cancel(true);
            estadosSensores.remove(sk);
            notifySensorListenersNA(sk);
            System.out.println("[Control] Sensor STOP " + sk);
        }
    }

    public boolean isSensorRunning(String tipo, String room) {
        return sensorFutures.containsKey(key(tipo, room));
    }

    public void startAllSensorsAllRooms() {
        for (String r : ROOMS) {
            startSensor("MOVIMIENTO", r);
            startSensor("LUMINOSIDAD", r);
            startSensor("TEMPERATURA", r);
        }
        // agua global
        startSensor("AGUA", "GLOBAL");
    }

    public void stopAllSensorsAllRooms() {
        for (String r : ROOMS) {
            stopSensor("MOVIMIENTO", r);
            stopSensor("LUMINOSIDAD", r);
            stopSensor("TEMPERATURA", r);
        }
        stopSensor("AGUA", "GLOBAL");
    }

    private void notifySensorListenersNA(String tipoRoom) {
        for (SensorUpdateListener l : sensorListeners) {
            try { l.onSensorUpdate(tipoRoom, "N/A"); } catch (Exception ignored) {}
        }
    }

    // ---- actualizar sensores / actuadores ----
    /**
     * tipoRoom: formato TEMPERATURA_DORMITORIO1 o MOVIMIENTO_COCINA o AGUA_GLOBAL
     */
    public void actualizarSensor(String tipoRoom, String valor) {
        if (tipoRoom == null) return;
        estadosSensores.put(tipoRoom.toUpperCase(), valor);
        // notify GUI
        for (SensorUpdateListener l : sensorListeners) {
            try { l.onSensorUpdate(tipoRoom.toUpperCase(), valor); } catch (Exception ignored) {}
        }
        // evaluar reglas inmediatas
        Map<String,String> cmds = procesarLogicaParaTipo(tipoRoom.toUpperCase(), valor);
        if (cmds != null && !cmds.isEmpty()) {
            for (Map.Entry<String,String> e : cmds.entrySet()) {
                String act = e.getKey();
                String acc = e.getValue();
                estadosActuadores.put(act.toUpperCase(), acc);
                for (CommandListener cl : commandListeners) {
                    try { cl.onCommand(act, acc); } catch (Exception ignored) {}
                }
            }
        }
    }

    public void actualizarActuador(String tipoRoom, String valor) {
        if (tipoRoom == null) return;
        estadosActuadores.put(tipoRoom.toUpperCase(), valor);
    }

    public String getEstadoSensor(String tipoRoom) { if (tipoRoom==null) return null; return estadosSensores.get(tipoRoom.toUpperCase()); }
    public String getEstadoActuador(String tipoRoom) { if (tipoRoom==null) return null; return estadosActuadores.get(tipoRoom.toUpperCase()); }

    public void setManualOverride(String actuadorTipoRoom, String accion) {
        if (actuadorTipoRoom==null) return;
        manualOverrides.put(actuadorTipoRoom.toUpperCase(), accion);
        estadosActuadores.put(actuadorTipoRoom.toUpperCase(), accion);
        for (CommandListener cl : commandListeners) {
            try { cl.onCommand(actuadorTipoRoom.toUpperCase(), accion); } catch (Exception ignored) {}
        }
        System.out.println("[Control] Override " + actuadorTipoRoom + " = " + accion);
    }
    public void clearManualOverride(String actuadorTipoRoom) { if (actuadorTipoRoom==null) return; manualOverrides.remove(actuadorTipoRoom.toUpperCase()); }

    public boolean hasManualOverride(String actuadorTipoRoom) { if (actuadorTipoRoom==null) return false; return manualOverrides.containsKey(actuadorTipoRoom.toUpperCase()); }

    // ---- lógica por zona: devuelve mapa actuador->accion
    private Map<String,String> procesarLogicaParaTipo(String tipoRoom, String valor) {
        // Para eficiencia, podríamos analizar solo zona afectada.
        // Implementaremos: parse tipoRoom -> tipo, room
        String[] parts = tipoRoom.split("_");
        String tipo = parts[0]; // TEMPERATURA/MOVIMIENTO/LUMINOSIDAD/AGUA
        String room = parts.length >=2 ? tipoRoom.substring(tipo.length()+1) : "GLOBAL";

        Map<String,String> comandos = new HashMap<>();

        try {
            if (tipo.equals("TEMPERATURA")) {
                double temp = Double.parseDouble(valor);
                String climaKey = "CLIMA_" + room;
                if (!hasManualOverride(climaKey)) {
                    if (temp > 28.0) comandos.put(climaKey, "ENCENDER");
                    else if (temp < 22.0) comandos.put(climaKey, "ENCENDER"); // ejemplo: calentar sala/encender
                    else comandos.put(climaKey, "APAGAR");
                }
            } else if (tipo.equals("MOVIMIENTO")) {
                boolean mv = "1".equals(valor);
                String luzKey = "LUZ_" + room;
                String camKey = "CAMARA_GLOBAL"; // <-- cámara centralizada
                String alarmaKey = "ALARMA_" + room;
                if (mv) {
                    if (!hasManualOverride(luzKey)) comandos.put(luzKey, "ON");
                    if (!hasManualOverride(camKey)) comandos.put(camKey, "GRABAR");
                    if (!hasManualOverride(alarmaKey)) comandos.put(alarmaKey, "ON");
                } else {
                    if (!hasManualOverride(luzKey)) {
                        String luxKey = "LUMINOSIDAD_" + room;
                        String luxVal = estadosSensores.get(luxKey);
                        if (luxVal != null) {
                            try {
                                int lux = Integer.parseInt(luxVal);
                                if (lux > 600) comandos.put(luzKey, "OFF");
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                    if (!hasManualOverride(camKey)) comandos.put(camKey, "STOP");
                    if (!hasManualOverride(alarmaKey)) comandos.put(alarmaKey, "OFF");
                }
            } else if (tipo.equals("LUMINOSIDAD")) {
                String luzKey = "LUZ_" + room;
                if (!hasManualOverride(luzKey)) {
                    int lux = Integer.parseInt(valor);
                    if (lux < 250) comandos.put(luzKey, "ON");
                    else if (lux > 600) comandos.put(luzKey, "OFF");
                }
                // Persianas: si mucha luz, subir; si poca, bajar
                String persKey = "PERSIANA_" + room;
                if (!hasManualOverride(persKey)) {
                    int lux = Integer.parseInt(valor);
                    if (lux > 700) comandos.put(persKey, "SUBIR");
                    else if (lux < 200) comandos.put(persKey, "BAJAR");
                }
            } else if (tipo.equals("AGUA")) {
                String aguaKey = "AGUA_GLOBAL";
                double a = Double.parseDouble(valor);
                if (!hasManualOverride(aguaKey)) {
                    if (a < 40.0) comandos.put(aguaKey, "ABRIR");
                    else if (a > 70.0) comandos.put(aguaKey, "CERRAR");
                }
            }
        } catch (Exception ex) {
            
        }
        return comandos;
    }

    // ---- util: enviar comando manual a todos actuadores (iterando por rooms) ----
    public void sendCommandToAllActuators(String tipoBase, String accion) {
        // e.g., tipoBase = "LUZ" -> envia LUZ_SALA, LUZ_COCHINA...
        if (tipoBase == null) return;
        for (String r : ROOMS) {
            String key = actuatorKey(tipoBase, r);
            setManualOverride(key, accion);
        }
        if ("AGUA".equalsIgnoreCase(tipoBase)) {
            setManualOverride("AGUA_GLOBAL", accion);
        }
    }
}
