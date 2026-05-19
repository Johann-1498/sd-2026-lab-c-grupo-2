package com.unsa.grpc.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.text.DecimalFormat;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatLightLaf;
import com.unsa.grpc.converter.ConversionType;
import com.unsa.grpc.converter.ConvertRequest;
import com.unsa.grpc.converter.ConvertResponse;
import com.unsa.grpc.converter.ConverterGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ConverterClientGUI extends JFrame {

    private JComboBox<String> comboConversiones;
    private JTextField txtInput;
    private JLabel lblResult;
    private ManagedChannel channel;
    private ConverterGrpc.ConverterBlockingStub stub;

    public ConverterClientGUI() {
        initGrpcClient();
        initUI();
    }

    private void initGrpcClient() {
        channel = ManagedChannelBuilder.forAddress("localhost", 50055)
                .usePlaintext()
                .build();
        stub = ConverterGrpc.newBlockingStub(channel);
    }

    private void initUI() {
        setTitle("gRPC Multi-Converter Pro");
        setSize(420, 420); // Ventana más alta y cuadrada (más elegante)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // Panel Principal todo blanco con márgenes grandes
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Título Principal
        JLabel lblTitle = new JLabel("Conversor gRPC");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(33, 37, 41)); // Gris muy oscuro
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- SECCIÓN DE FORMULARIO ---
        // Etiqueta Selector
        JLabel lblSelect = new JLabel("Seleccione el tipo de conversión:");
        lblSelect.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSelect.setForeground(new Color(108, 117, 125)); // Gris suave
        lblSelect.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Opciones sin emojis
        String[] opciones = {
            "Celsius a Fahrenheit", 
            "Fahrenheit a Celsius", 
            "Soles a Dólares", 
            "Dólares a Soles", 
            "Kilómetros a Millas", 
            "Millas a Kilómetros",
            "Kilogramos a Libras"
        };
        comboConversiones = new JComboBox<>(opciones);
        comboConversiones.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        comboConversiones.setMaximumSize(new Dimension(300, 35));
        comboConversiones.setAlignmentX(Component.CENTER_ALIGNMENT);
        comboConversiones.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Etiqueta Input
        JLabel lblInput = new JLabel("Ingrese el valor a convertir:");
        lblInput.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblInput.setForeground(new Color(108, 117, 125));
        lblInput.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Input de texto centrado
        txtInput = new JTextField();
        txtInput.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtInput.setMaximumSize(new Dimension(300, 40));
        txtInput.setHorizontalAlignment(JTextField.CENTER);
        txtInput.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Botón Moderno (Azul estilo Bootstrap)
        JButton btnConvertir = new JButton("Calcular Resultado");
        btnConvertir.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnConvertir.setForeground(Color.WHITE);
        btnConvertir.setBackground(new Color(13, 110, 253)); // Azul brillante
        btnConvertir.setFocusPainted(false);
        btnConvertir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConvertir.setMaximumSize(new Dimension(300, 45));
        btnConvertir.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnConvertir.addActionListener(e -> realizarConversion());

        // Etiqueta de Resultado
        lblResult = new JLabel("Resultado: ---", SwingConstants.CENTER);
        lblResult.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblResult.setForeground(new Color(25, 135, 84)); // Verde éxito
        lblResult.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- ENSAMBLAJE DE COMPONENTES CON ESPACIOS ---
        mainPanel.add(lblTitle);
        mainPanel.add(Box.createVerticalStrut(25)); // Espacio vertical
        mainPanel.add(lblSelect);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(comboConversiones);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(lblInput);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(txtInput);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(btnConvertir);
        mainPanel.add(Box.createVerticalStrut(25));
        mainPanel.add(lblResult);

        add(mainPanel);
    }

    private void realizarConversion() {
        try {
            double valorInput = Double.parseDouble(txtInput.getText());
            int index = comboConversiones.getSelectedIndex();
            ConversionType type = ConversionType.forNumber(index);

            ConvertRequest request = ConvertRequest.newBuilder()
                    .setValue(valorInput)
                    .setType(type)
                    .build();

            ConvertResponse response = stub.convert(request);

            if (response.getSuccess()) {
                DecimalFormat df = new DecimalFormat("#,##0.00");
                lblResult.setForeground(new Color(25, 135, 84)); // Verde éxito
                lblResult.setText("Resultado: " + df.format(response.getResult()));
            } else {
                lblResult.setForeground(new Color(220, 53, 69)); // Rojo error
                lblResult.setText("Error en la validación");
                JOptionPane.showMessageDialog(this, response.getMessage(), "Alerta del Servidor", JOptionPane.WARNING_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un número válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar al Servidor gRPC.\n¿Aseguró que está encendido?", "Error Crítico", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // ACTIVAR LA INTERFAZ MODERNA FLATLAF
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Error iniciando FlatLaf");
        }

        SwingUtilities.invokeLater(() -> {
            ConverterClientGUI client = new ConverterClientGUI();
            client.setVisible(true);
        });
    }
}