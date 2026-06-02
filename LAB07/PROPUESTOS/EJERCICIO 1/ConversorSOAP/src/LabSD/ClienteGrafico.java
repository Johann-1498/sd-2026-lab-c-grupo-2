package LabSD;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class ClienteGrafico extends JFrame {
    
    private JTextField txtCantidad;
    private JComboBox<String> comboConversor;
    private JButton btnCalcular;
    private JLabel lblResultado;
    private SOAPI servicioConversor;

    public ClienteGrafico() {
        try {
            URL url = new URL("http://localhost:1516/WS/Conversor?wsdl");
            QName qname = new QName("http://LabSD/", "ConversorSOAPService");
            Service service = Service.create(url, qname);
            this.servicioConversor = service.getPort(SOAPI.class);
        } catch (Exception e) {
            System.out.println("Error de conexión remota: " + e.getMessage());
        }
        Font fuenteTitulo = new Font("Segoe UI", Font.BOLD, 18);
        Font fuenteEtiquetas = new Font("Segoe UI", Font.BOLD, 12);
        Font fuenteCampos = new Font("Segoe UI", Font.PLAIN, 13);
        Color colorFondo = new Color(240, 242, 245);       // Gris claro estilo web
        Color colorContenedor = Color.WHITE;               // Tarjeta blanca
        Color colorTexto = new Color(51, 51, 51);          // Gris oscuro elegante
        Color colorBotonVerde = new Color(76, 175, 80);    // #4CAF50
        Color colorBotonHover = new Color(69, 160, 73);    // #45a049
        Color colorCajaResultado = new Color(232, 245, 233);// Verde muy claro
        setTitle("Cliente Laboratorio SOAP");
        setSize(400, 360);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(colorFondo);
        setLayout(new GridBagLayout());
        setLocationRelativeTo(null);
        JPanel panelTarjeta = new JPanel();
        panelTarjeta.setBackground(colorContenedor);
        panelTarjeta.setLayout(new GridBagLayout());
        panelTarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridx = 0;
        JLabel lblTitulo = new JLabel("Conversor Web SOAP", SwingConstants.CENTER);
        lblTitulo.setFont(fuenteTitulo);
        lblTitulo.setForeground(colorTexto);
        gbc.gridy = 0;
        panelTarjeta.add(lblTitulo, gbc);
        JLabel lblCant = new JLabel("Ingresa el valor numérico:");
        lblCant.setFont(fuenteEtiquetas);
        lblCant.setForeground(new Color(85, 85, 85));
        gbc.gridy = 1;
        gbc.insets = new Insets(12, 0, 2, 0);
        panelTarjeta.add(lblCant, gbc);
        txtCantidad = new JTextField();
        txtCantidad.setFont(fuenteCampos);
        txtCantidad.setPreferredSize(new Dimension(0, 35));
        txtCantidad.setBorder(BorderFactory.createLineBorder(new Color(204, 204, 204), 1));
        gbc.gridy = 2;
        gbc.insets = new Insets(2, 0, 6, 0);
        panelTarjeta.add(txtCantidad, gbc);
        JLabel lblOp = new JLabel("Selecciona la conversión:");
        lblOp.setFont(fuenteEtiquetas);
        lblOp.setForeground(new Color(85, 85, 85));
        gbc.gridy = 3;
        panelTarjeta.add(lblOp, gbc);
        String[] opciones = {"Celsius a Fahrenheit", "Fahrenheit a Celsius"};
        comboConversor = new JComboBox<>(opciones);
        comboConversor.setFont(fuenteCampos);
        comboConversor.setBackground(Color.WHITE);
        comboConversor.setPreferredSize(new Dimension(0, 35));
        gbc.gridy = 4;
        panelTarjeta.add(comboConversor, gbc);
        btnCalcular = new JButton("Calcular en el Servidor");
        btnCalcular.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCalcular.setBackground(colorBotonVerde);
        btnCalcular.setForeground(Color.WHITE);
        btnCalcular.setFocusPainted(false);
        btnCalcular.setBorderPainted(false);
        btnCalcular.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCalcular.setPreferredSize(new Dimension(0, 40));
        gbc.gridy = 5;
        gbc.insets = new Insets(15, 0, 10, 0);
        panelTarjeta.add(btnCalcular, gbc);
        btnCalcular.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnCalcular.setBackground(colorBotonHover);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnCalcular.setBackground(colorBotonVerde);
            }
        });
        lblResultado = new JLabel("Resultado: --", SwingConstants.CENTER);
        lblResultado.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblResultado.setForeground(new Color(46, 125, 50)); // Verde oscuro
        lblResultado.setOpaque(true);
        lblResultado.setBackground(colorCajaResultado);
        lblResultado.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gbc.gridy = 6;
        gbc.insets = new Insets(5, 0, 5, 0);
        panelTarjeta.add(lblResultado, gbc);
        add(panelTarjeta);
        btnCalcular.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double cantidad = Double.parseDouble(txtCantidad.getText());
                    double resultado = 0;

                    if (comboConversor.getSelectedIndex() == 0) {
                        resultado = servicioConversor.cToF(cantidad);
                        lblResultado.setText(String.format("Resultado: %.2f °F", resultado));
                    } else {
                        resultado = servicioConversor.fToC(cantidad);
                        lblResultado.setText(String.format("Resultado: %.2f °C", resultado));
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Escribe una cantidad numérica válida.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "No hay respuesta del Servidor SOAP.", "Error de Comunicación", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClienteGrafico().setVisible(true);
            }
        });
    }
}