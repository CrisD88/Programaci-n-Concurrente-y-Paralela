package control;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ControlCentral mantiene el estado de sensores y actuadores,
 * aplica reglas automáticas y soporta overrides manuales.
 */
public class ControlCentral {

    private final ConcurrentHashMap<String, String> estadosSensores = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> estadosActuadores = new ConcurrentHashMap<>();

    // Manual override: si contiene una entrada para un actuador, la lógica automática evita
    // cambiar ese actuador hasta que se quite el override.
    private final ConcurrentHashMap<String, String> manualOverrides = new ConcurrentHashMap<>();

    public void actualizarSensor(String tipo, String valor) {
        if (tipo == null) return;
        estadosSensores.put(tipo.toUpperCase(), valor);
        System.out.println("[Control] Sensor actualizado: " + tipo.toUpperCase() + " = " + valor);
    }

    public void actualizarActuador(String tipo, String valor) {
        if (tipo == null) return;
        estadosActuadores.put(tipo.toUpperCase(), valor);
        System.out.println("[Control] Actuador actualizado: " + tipo.toUpperCase() + " = " + valor);
    }

    public String getEstadoSensor(String tipo) {
        return estadosSensores.get(tipo.toUpperCase());
    }

    public String getEstadoActuador(String tipo) {
        return estadosActuadores.get(tipo.toUpperCase());
    }

    /**
     * Establece un override manual para un actuador (por ejemplo "LUZ" -> "ON").
     * Mientras exista el override, la lógica automática no debe alterar ese actuador.
     */
    public void setManualOverride(String actuadorTipo, String accion) {
        if (actuadorTipo == null) return;
        manualOverrides.put(actuadorTipo.toUpperCase(), accion);
        System.out.println("[Control] Override manual: " + actuadorTipo.toUpperCase() + " = " + accion);
    }

    public void clearManualOverride(String actuadorTipo) {
        if (actuadorTipo == null) return;
        manualOverrides.remove(actuadorTipo.toUpperCase());
        System.out.println("[Control] Override manual removido: " + actuadorTipo.toUpperCase());
    }

    public boolean hasManualOverride(String actuadorTipo) {
        return manualOverrides.containsKey(actuadorTipo.toUpperCase());
    }

    /**
     * Procesa la lógica automática actual y devuelve un mapa de comandos
     * a ejecutar: actuadorTipo -> accion (ej. "LUZ" -> "ON").
     * Devuelve un mapa vacío si no hay comandos.
     */
    public Map<String, String> procesarLogica() {
        Map<String, String> comandos = new HashMap<>();

        String tempStr = estadosSensores.get("TEMPERATURA");
        String luxStr = estadosSensores.get("LUMINOSIDAD");
        String movStr = estadosSensores.get("MOVIMIENTO");
        String flujoStr = estadosSensores.get("FUGA"); 

        // --- CLIMA ---
        if (tempStr != null) {
            try {
                double temp = Double.parseDouble(tempStr);
                if (!hasManualOverride("CLIMA")) {
                    if (temp > 28.0) {
                        comandos.put("CLIMA", "ENCENDER");
                    } else if (temp < 20.0) {
                        // Si está muy bajo podría apagar o encender calefacción; aquí usamos DESACTIVAR/ENCENDER
                        comandos.put("CLIMA", "APAGAR");
                    } else {
                        // temperatura normal -> apagar climatizador si está encendido
                        // (podemos dejar sin comando)
                    }
                }
            } catch (NumberFormatException ignored) {}
        }

        // --- LUCES (interacción con movimiento y luminosidad) ---
        // Regla: si hay movimiento y luminosidad baja -> encender luces (si no override manual)
        if (movStr != null) {
            boolean movimiento = movStr.equals("1") || movStr.equalsIgnoreCase("true");
            int lux = -1;
            if (luxStr != null) {
                try { lux = Integer.parseInt(luxStr); } catch (NumberFormatException ignored) {}
            }
            if (movimiento) {
                // decide según luminosidad: si no hay dato de lux, asumimos oscuridad y encendemos
                if (!hasManualOverride("LUZ")) {
                    if (lux == -1 || lux < 200) {
                        comandos.put("LUZ", "ON");
                    } else {
                        comandos.put("LUZ", "OFF"); // si hay movimiento pero está muy iluminado, dejar OFF
                    }
                }
                // además, activar cámaras ante movimiento si no hay override
                if (!hasManualOverride("CAMARA")) {
                    comandos.put("CAMARA", "GRABAR");
                }
            } else {
                // no hay movimiento: detener cámaras y posiblemente apagar luces (si auto)
                if (!hasManualOverride("CAMARA")) comandos.put("CAMARA", "STOP");
                if (!hasManualOverride("LUZ")) {
                    if (lux != -1 && lux > 600) {
                        comandos.put("LUZ", "OFF");
                    }
                }
            }
        }

        // --- LUCES por luminosidad incluso sin movimiento ---
        // Si no hay movimiento, aún podemos apagar/encender por niveles de luz si no override
        if (movStr == null || movStr.equals("0")) {
            if (luxStr != null && !hasManualOverride("LUZ")) {
                try {
                    int lux = Integer.parseInt(luxStr);
                    if (lux < 200) comandos.put("LUZ", "ON");
                    else if (lux > 600) comandos.put("LUZ", "OFF");
                } catch (NumberFormatException ignored) {}
            }
        }

        // --- AGUA ---
        if (flujoStr != null) {
            if ((flujoStr.equals("1") || flujoStr.equalsIgnoreCase("true")) && !hasManualOverride("AGUA")) {
                comandos.put("AGUA", "CERRAR");
            }
        } else {
            String presion = estadosSensores.get("PRESION");
            String flujo = estadosSensores.get("FLUJO");
            if (presion != null) {
                try {
                    double p = Double.parseDouble(presion);
                    if (p < 40.0 && !hasManualOverride("AGUA")) {
                        comandos.put("AGUA", "ABRIR");
                    }
                } catch (NumberFormatException ignored) {}
            }
            if (flujo != null) {
                try {
                    double f = Double.parseDouble(flujo);
                    if (f > 70.0 && !hasManualOverride("AGUA")) {
                        comandos.put("AGUA", "CERRAR");
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        return comandos;
    }
}