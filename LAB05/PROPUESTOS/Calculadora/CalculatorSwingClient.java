import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CalculatorSwingClient extends JFrame {

    private JTextField display;
    private double num1 = 0;
    private String operator = "";
    private boolean startNumber = true;

    // Conexión RMI
    private Calculator rmiCalculator;

    public CalculatorSwingClient() {
        // Intentar conectar al servidor
        conectarServidorRMI();

        // Configuración de la ventana
        setTitle("Calculadora RMI Distribuida");
        setSize(320, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        // Pantalla de la calculadora
        display = new JTextField("0");
        display.setEditable(false);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setFont(new Font("Arial", Font.BOLD, 24));
        display.setPreferredSize(new Dimension(300, 60));
        add(display, BorderLayout.NORTH);

        // Panel de botones
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 4, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] buttonLabels = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "C", "0", "=", "+",
            "^", ".", "", ""
        };

        for (String text : buttonLabels) {
            if (text.isEmpty()) {
                panel.add(new JLabel("")); // Espacio vacío
                continue;
            }
            JButton btn = new JButton(text);
            btn.setFont(new Font("Arial", Font.BOLD, 18));
            btn.addActionListener(new ButtonClickListener());
            panel.add(btn);
        }

        add(panel, BorderLayout.CENTER);
        setLocationRelativeTo(null); // Centrar en pantalla
    }

    private void conectarServidorRMI() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            rmiCalculator = (Calculator) registry.lookup("CalculadoraRMI");
            System.out.println("Conectado al servidor RMI exitosamente.");
        } catch (Exception e) {
            System.err.println("No se pudo conectar al servidor RMI. Ejecuta el ServerMain primero.");
        }
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String text = ((JButton) e.getSource()).getText();

            if (text.matches("[0-9\\.]")) {
                if (startNumber) {
                    display.setText(text.equals(".") ? "0." : text);
                    startNumber = false;
                } else {
                    display.setText(display.getText() + text);
                }
            } else if (text.equals("C")) {
                display.setText("0");
                num1 = 0;
                operator = "";
                startNumber = true;
            } else if (text.equals("=") && !operator.isEmpty()) {
                double num2 = Double.parseDouble(display.getText());
                double result = 0;

                try {
                    if (rmiCalculator == null) {
                        display.setText("Error: Servidor off");
                        return;
                    }
                    switch (operator) {
                        case "+": result = rmiCalculator.add(num1, num2); break;
                        case "-": result = rmiCalculator.subtract(num1, num2); break;
                        case "*": result = rmiCalculator.multiply(num1, num2); break;
                        case "/": result = rmiCalculator.divide(num1, num2); break;
                        case "^": result = rmiCalculator.power(num1, num2); break;
                    }
                    if (result == (long) result) {
                        display.setText(String.format("%d", (long) result));
                    } else {
                        display.setText(String.valueOf(result));
                    }
                } catch (Exception ex) {
                    display.setText("Error en red");
                }
                operator = "";
                startNumber = true;
            } else if (text.matches("[+\\-*/^]")) {
                num1 = Double.parseDouble(display.getText());
                operator = text;
                startNumber = true;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CalculatorSwingClient().setVisible(true);
        });
    }
}