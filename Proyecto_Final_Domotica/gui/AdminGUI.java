package gui;

import control.ControlCentral;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * AdminGUI:
 * - BAÑO solo tiene LUZ y PERSIANA
 * - Otras habitaciones tienen LUZ, PERSIANA y CLIMA
 * - Panel de Actuadores Adicionales: AGUA, CERRADURA, CAMARA
 */
public class AdminGUI extends JFrame {

    private final ControlCentral control;

    private JLabel clockLabel;

    private final Map<String,JLabel> sensorValueLabels = new HashMap<>();
    private final Map<String,JLabel> actuatorLights = new HashMap<>();
    private final Map<String,JButton> sensorToggleButtons = new HashMap<>();

    public AdminGUI(ControlCentral control) {
        this.control = control;
        initUI();
        hookupListeners();
    }

    private void initUI() {
        setTitle("Admin Domótica - Zonas");
        setSize(1200, 760);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(34,34,34));
        setLayout(new BorderLayout());

        // Top panel: reloj y botones globales
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(45,45,45));
        top.setBorder(new EmptyBorder(8,8,8,8));

        clockLabel = new JLabel();
        clockLabel.setForeground(Color.WHITE);
        clockLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        JLabel hora = new JLabel("HORA:");
        hora.setForeground(Color.WHITE);
        hora.setFont(new Font("SansSerif", Font.BOLD, 20));
        JPanel leftTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftTop.setOpaque(false);
        leftTop.add(hora);
        leftTop.add(clockLabel);
        top.add(leftTop, BorderLayout.WEST);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8,0));
        btns.setOpaque(false);
        JButton connectAll = new JButton("Conectar todo (sensores)");
        JButton disconnectAll = new JButton("Desconectar todo (sensores)");
        JButton connectActu = new JButton("Encender todo (actuadores)");
        JButton disconnectActu = new JButton("Apagar todo (actuadores)");
        JButton exit = new JButton("Salir");

        connectAll.addActionListener(e -> {
            control.startAllSensorsAllRooms();
            refreshSensorButtons();
        });
        disconnectAll.addActionListener(e -> {
            control.stopAllSensorsAllRooms();
            refreshSensorButtons();
        });
        connectActu.addActionListener(e -> {
            control.sendCommandToAllActuators("LUZ","ON");
            control.sendCommandToAllActuators("CLIMA","ENCENDER");
            control.sendCommandToAllActuators("PERSIANA","SUBIR");
            control.setManualOverride("AGUA_PRINCIPAL","ABRIR");
        });
        disconnectActu.addActionListener(e -> {
            control.sendCommandToAllActuators("LUZ","OFF");
            control.sendCommandToAllActuators("CLIMA","APAGAR");
            control.sendCommandToAllActuators("PERSIANA","BAJAR");
            control.setManualOverride("AGUA_PRINCIPAL","CERRAR");
        });
        exit.addActionListener(e -> System.exit(0));

        btns.add(connectAll);
        btns.add(disconnectAll);
        btns.add(connectActu);
        btns.add(disconnectActu);
        btns.add(exit);
        top.add(btns, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        // Center panel: habitaciones + actuadores adicionales
        JPanel center = new JPanel(new GridLayout(2,3,12,12));
        center.setBorder(new EmptyBorder(12,12,12,12));
        center.setOpaque(false);

        // Paneles de habitaciones
        for (String room : ControlCentral.ROOMS) {
            center.add(createRoomPanel(room));
        }

        // Panel actuadores adicionales
        center.add(createActuadoresAdicionalesPanel());

        add(center, BorderLayout.CENTER);

        // Timer para reloj y refresco de sensores
        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
            clockLabel.setText(java.time.LocalTime.now().withNano(0).toString());
            refreshSensorDisplays();
        });
        timer.start();

        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private JPanel createRoomPanel(String roomRaw) {
        String room = roomRaw;
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(50,50,50));
        p.setBorder(new TitledBorder(new LineBorder(Color.WHITE,2), room, TitledBorder.CENTER, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 16), Color.WHITE));

        // Sensores
        JPanel sensors = new JPanel(new GridLayout(3,1));
        sensors.setOpaque(false);
        sensors.add(createSensorRow("MOVIMIENTO", room));
        sensors.add(createSensorRow("LUMINOSIDAD", room));
        sensors.add(createSensorRow("TEMPERATURA", room));
        p.add(sensors, BorderLayout.NORTH);

        // Actuadores
        JPanel actu = new JPanel();
        if(room.equalsIgnoreCase("BAÑO")) {
            actu.setLayout(new GridLayout(2,1));
            actu.add(createActuatorRow("LUZ", room));
            actu.add(createActuatorRow("PERSIANA", room));
        } else {
            actu.setLayout(new GridLayout(3,1));
            actu.add(createActuatorRow("LUZ", room));
            actu.add(createActuatorRow("PERSIANA", room));
            actu.add(createActuatorRow("CLIMA", room));
        }
        actu.setOpaque(false);
        p.add(actu, BorderLayout.CENTER);

        return p;
    }

    // Panel actuadores adicionales
    private JPanel createActuadoresAdicionalesPanel() {
        JPanel p = new JPanel(new GridLayout(4,1)); // +1 fila para mostrar sensor AGUA
        p.setBackground(new Color(50,50,50));
        p.setBorder(new TitledBorder(new LineBorder(Color.WHITE,2),
                "ACTUADORES ADICIONALES", TitledBorder.CENTER, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 16), Color.WHITE));

        // Sensor de Agua
        JPanel sensorAguaRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sensorAguaRow.setOpaque(false);
        JLabel lblAgua = new JLabel("AGUA: ");
        lblAgua.setForeground(Color.WHITE);
        lblAgua.setPreferredSize(new Dimension(110,24));
        sensorAguaRow.add(lblAgua);

        JLabel valueAgua = new JLabel("N/A");
        valueAgua.setForeground(Color.GREEN);
        valueAgua.setPreferredSize(new Dimension(110,24));
        sensorValueLabels.put("AGUA_GLOBAL", valueAgua); // clave para actualizar desde ControlCentral
        sensorAguaRow.add(valueAgua);

        p.add(sensorAguaRow);

        // Actuadores
        p.add(createActuatorRow("AGUA", "GLOBAL"));
        p.add(createActuatorRow("CERRADURA", "GLOBAL"));
        p.add(createActuatorRow("CAMARA", "GLOBAL"));

        return p;
    }

    private JPanel createSensorRow(String tipo, String room) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setOpaque(false);
        JLabel lbl = new JLabel(tipo + ": ");
        lbl.setForeground(Color.WHITE);
        lbl.setPreferredSize(new Dimension(110,24));
        row.add(lbl);

        String sensorKey = tipo + "_" + room;
        JLabel value = new JLabel("N/A");
        value.setForeground(Color.GREEN);
        value.setPreferredSize(new Dimension(110,24));
        sensorValueLabels.put(sensorKey, value);
        row.add(value);

        JButton toggle = new JButton("Encender");
        toggle.setPreferredSize(new Dimension(100,26));
        toggle.addActionListener(e -> {
            boolean running = control.isSensorRunning(tipo, room);
            if (!running) {
                control.startSensor(tipo, room);
                toggle.setText("Apagar");
            } else {
                control.stopSensor(tipo, room);
                toggle.setText("Encender");
            }
        });
        sensorToggleButtons.put(sensorKey, toggle);
        row.add(toggle);

        JButton once = new JButton("Leer ahora");
        once.addActionListener(ev -> {
            if (control.isSensorRunning(tipo, room)) {
                switch (tipo.toUpperCase()) {
                    case "MOVIMIENTO":
                        boolean mv = new Random().nextDouble() < 0.25;
                        control.actualizarSensor(sensorKey, mv ? "1" : "0");
                        break;
                    case "LUMINOSIDAD":
                        int lux = new Random().nextInt(1000);
                        control.actualizarSensor(sensorKey, String.valueOf(lux));
                        break;
                    case "TEMPERATURA":
                        double t = 18 + new Random().nextDouble() * 15;
                        control.actualizarSensor(sensorKey, String.format(Locale.US,"%.2f", t));
                        break;
                }
            } else {
                JOptionPane.showMessageDialog(this, "Sensor apagado. Enciéndelo primero.");
            }
        });
        row.add(once);

        return row;
    }

    private JPanel createActuatorRow(String tipoBase, String room) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setOpaque(false);
        JLabel lbl = new JLabel(tipoBase + ": ");
        lbl.setForeground(Color.WHITE);
        lbl.setPreferredSize(new Dimension(110,24));
        row.add(lbl);

        String actuKey = tipoBase + "_" + room;
        JLabel light = new JLabel("  ");
        light.setOpaque(true);
        light.setBackground(Color.RED);
        light.setPreferredSize(new Dimension(20,20));
        actuatorLights.put(actuKey, light);
        row.add(light);

        JButton on = new JButton("Encender");
        JButton off = new JButton("Apagar");

        on.addActionListener(e -> control.setManualOverride(actuKey,
                tipoBase.equalsIgnoreCase("CLIMA") ? "ENCENDER" :
                (tipoBase.equalsIgnoreCase("PERSIANA") ? "SUBIR" :
                (tipoBase.equalsIgnoreCase("CAMARA") ? "GRABAR" : "ON"))
        ));
        off.addActionListener(e -> control.setManualOverride(actuKey,
                tipoBase.equalsIgnoreCase("CLIMA") ? "APAGAR" :
                (tipoBase.equalsIgnoreCase("PERSIANA") ? "BAJAR" :
                (tipoBase.equalsIgnoreCase("CAMARA") ? "STOP" : "OFF"))
        ));

        row.add(on);
        row.add(off);

        return row;
    }

    private void hookupListeners() {
        control.addSensorListener((tipoRoom, valor) -> {
            SwingUtilities.invokeLater(() -> {
                String key = tipoRoom.toUpperCase();
                JLabel lab = sensorValueLabels.get(key);
                if (lab != null) {
                    if ("N/A".equals(valor)) lab.setText("N/A");
                    else if (key.startsWith("TEMPERATURA_")) lab.setText(valor + " °C");
                    else if (key.startsWith("LUMINOSIDAD_")) lab.setText(valor + " lx");
                    else lab.setText(valor);
                }
                if (sensorToggleButtons.containsKey(key)) {
                    JButton b = sensorToggleButtons.get(key);
                    if (control.isSensorRunning(key.split("_")[0], key.split("_")[1])) b.setText("Apagar");
                    else b.setText("Encender");
                }
            });
        });

        control.addCommandListener((actuadorTipo, accion) -> {
            SwingUtilities.invokeLater(() -> {
                String ak = actuadorTipo.toUpperCase();
                JLabel light = actuatorLights.get(ak);
                boolean on = accion != null && (accion.equalsIgnoreCase("ON") || accion.equalsIgnoreCase("ENCENDER") || accion.equalsIgnoreCase("GRABAR") || accion.equalsIgnoreCase("ABRIR") || accion.equalsIgnoreCase("SUBIR"));
                if (light != null) light.setBackground(on ? Color.GREEN : Color.RED);
            });
        });
    }

    private void refreshSensorDisplays() {
        for (String room : ControlCentral.ROOMS) {
            String m = control.getEstadoSensor("MOVIMIENTO_" + room);
            sensorValueLabels.getOrDefault("MOVIMIENTO_" + room, new JLabel()).setText(m == null ? "N/A" : m);
            String lx = control.getEstadoSensor("LUMINOSIDAD_" + room);
            sensorValueLabels.getOrDefault("LUMINOSIDAD_" + room, new JLabel()).setText(lx == null ? "N/A" : (lx + " lx"));
            String t = control.getEstadoSensor("TEMPERATURA_" + room);
            sensorValueLabels.getOrDefault("TEMPERATURA_" + room, new JLabel()).setText(t == null ? "N/A" : (t + " °C"));
        }
        String a = control.getEstadoSensor("AGUA_GLOBAL");
        sensorValueLabels.getOrDefault("AGUA_GLOBAL", new JLabel()).setText(a == null ? "N/A" : (a + " %"));
        for (Map.Entry<String,JButton> e : sensorToggleButtons.entrySet()) {
            String key = e.getKey();
            String[] p = key.split("_");
            String tipo = p[0], room = p[1];
            JButton b = e.getValue();
            b.setText(control.isSensorRunning(tipo, room) ? "Apagar" : "Encender");
        }
    }

    private void refreshSensorButtons() {
        for (Map.Entry<String, JButton> e : sensorToggleButtons.entrySet()) {
            String key = e.getKey();
            String[] parts = key.split("_");
            if (parts.length < 2) continue;
            String tipo = parts[0];
            String room = parts[1];
            JButton b = e.getValue();
            if (b != null) {
                b.setText(control.isSensorRunning(tipo, room) ? "Apagar" : "Encender");
            }
        }
    }
}
