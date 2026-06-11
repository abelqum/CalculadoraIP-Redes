package com.redes.ui;

import com.redes.logica.ManejadorIP;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PantallaPrincipal extends JFrame {

    public PantallaPrincipal() {
        setTitle("Herramientas de Red - CIDR y VLSM");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Crear el contenedor de pestañas
        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setFont(new Font("Arial", Font.BOLD, 14));

        // Agregar las TRES pantallas
        pestanas.addTab("Calculadora CIDR", crearPanelCIDR());
        pestanas.addTab("Calculadora VLSM", crearPanelVLSM());
        pestanas.addTab("Equipo", crearPanelCreditos()); // <-- Tu nueva pestaña

        add(pestanas);
    }

    private JPanel crearPanelCIDR() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel superior para inputs
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTop.add(new JLabel("Dirección IP Base:"));
        JTextField txtIp = new JTextField("192.168.1.0", 12);
        panelTop.add(txtIp);

        panelTop.add(new JLabel("Prefijo (ej. 24):"));
        JTextField txtPrefijo = new JTextField("24", 4);
        panelTop.add(txtPrefijo);

        JButton btnCalcular = new JButton("Calcular CIDR");
        btnCalcular.setBackground(new Color(40, 167, 69));
        btnCalcular.setForeground(Color.WHITE);
        panelTop.add(btnCalcular);

        // Área de resultados
        JTextArea txtResultado = new JTextArea();
        txtResultado.setEditable(false);
        txtResultado.setFont(new Font("Monospaced", Font.PLAIN, 14)); // Fuente tipo consola para alinear el binario
        JScrollPane scroll = new JScrollPane(txtResultado);

        // Acción del botón
        btnCalcular.addActionListener(e -> {
            try {
                String ip = txtIp.getText().trim();
                int prefijo = Integer.parseInt(txtPrefijo.getText().trim());
                String resultado = ManejadorIP.calcularCIDRPasoAPaso(ip, prefijo);
                txtResultado.setText(resultado);
                txtResultado.setCaretPosition(0); // Regresar el scroll arriba
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error en los datos. Verifica el formato.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(panelTop, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelVLSM() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel superior para inputs
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTop.add(new JLabel("IP Mayor:"));
        JTextField txtIpBase = new JTextField("172.16.0.0", 10);
        panelTop.add(txtIpBase);

        panelTop.add(new JLabel("Prefijo Inicial:"));
        JTextField txtPrefijoBase = new JTextField("16", 3);
        panelTop.add(txtPrefijoBase);

        panelTop.add(new JLabel("Hosts requeridos (separados por coma):"));
        JTextField txtHosts = new JTextField("1000, 200, 50, 20", 15);
        panelTop.add(txtHosts);

        JButton btnCalcular = new JButton("Subnetear (VLSM)");
        btnCalcular.setBackground(new Color(23, 162, 184));
        btnCalcular.setForeground(Color.WHITE);
        panelTop.add(btnCalcular);

        // Área de resultados
        JTextArea txtResultado = new JTextArea();
        txtResultado.setEditable(false);
        txtResultado.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(txtResultado);

        // Acción del botón
        btnCalcular.addActionListener(e -> {
            try {
                String ip = txtIpBase.getText().trim();
                int prefijo = Integer.parseInt(txtPrefijoBase.getText().trim());

                // Parsear los hosts separados por coma
                String[] hostsStr = txtHosts.getText().split(",");
                List<Integer> hostsReq = new ArrayList<>();
                for (String h : hostsStr) {
                    hostsReq.add(Integer.parseInt(h.trim()));
                }

                String resultado = ManejadorIP.calcularVLSMPasoAPaso(ip, prefijo, hostsReq);
                txtResultado.setText(resultado);
                txtResultado.setCaretPosition(0);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Verifica el formato. Ingresa solo números enteros separados por comas para los hosts.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(panelTop, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // NUEVA PESTAÑA DE CRÉDITOS
    private JPanel crearPanelCreditos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE); // Fondo blanco para que luzca limpio

        // Estructuramos la información con HTML para centrarla y darle formato bonito
        String textoHTML = "<html><div style='text-align: center; font-family: Arial; padding: 30px;'>" +
                "<h2 style='color: #6a1b9a;'>Instituto Politécnico Nacional</h2>" +
                "<h3 style='color: #1565c0;'>Escuela Superior de Cómputo</h3><br>" +
                "<p style='font-size: 16px;'>" +
                "<b>Materia:</b> Redes de Computadoras<br>" +
                "<b>Grupo:</b> 5CV1<br>" +
                "<b>Profesor:</b> Maestro Juan Jesús Alcaraz Torres" +
                "</p><br><br>" +
                "<p style='font-size: 16px; color: #2e7d32;'><b>Integrantes del Equipo:</b></p>" +
                "<p style='font-size: 15px; line-height: 1.5;'>" +
                "Cardenas Hernández Ximena<br>" +
                "Morán Vaquero Marcos<br>" +
                "Quiroz Mora Abel Mauricio<br>" +
                "Ramírez Ramírez Gabriel<br>" +
                "Reyes Orozco Abigail Betzabé" +
                "</p></div></html>";

        JLabel lblCreditos = new JLabel(textoHTML, SwingConstants.CENTER);
        panel.add(lblCreditos, BorderLayout.CENTER);

        return panel;
    }
}