package LAB04.RESUELTOS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.Naming;

public class CalculatorGUI extends JFrame implements ActionListener {

    JTextField txtNum1, txtNum2;
    JLabel lblResultado;
    JButton btnSumar, btnRestar, btnMultiplicar, btnDividir;
    Calculator c;

    public CalculatorGUI() {

        setTitle("Calculadora RMI");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2, 10, 10));
        add(new JLabel("Número 1:"));
        txtNum1 = new JTextField();
        add(txtNum1);

        add(new JLabel("Número 2:"));
        txtNum2 = new JTextField();
        add(txtNum2);

        btnSumar = new JButton("SUMAR");
        btnRestar = new JButton("RESTAR");
        btnMultiplicar = new JButton("MULTIPLICAR");
        btnDividir = new JButton("DIVIDIR");

        add(btnSumar);
        add(btnRestar);
        add(btnMultiplicar);
        add(btnDividir);

        add(new JLabel("Resultado:"));
        lblResultado = new JLabel("...");
        add(lblResultado);

        btnSumar.addActionListener(this);
        btnRestar.addActionListener(this);
        btnMultiplicar.addActionListener(this);
        btnDividir.addActionListener(this);

        try {
            c = (Calculator) Naming.lookup(
                    "rmi://localhost/CalculatorService"
            );

            lblResultado.setText("Conectado al servidor");

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error conectando al servidor RMI"
            );
        }
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        try {

            // Obtener números
            int num1 = Integer.parseInt(txtNum1.getText());
            int num2 = Integer.parseInt(txtNum2.getText());

            int resultado = 0;

            if (e.getSource() == btnSumar) {

                resultado = c.add(num1, num2);

            }

            else if (e.getSource() == btnRestar) {

                resultado = c.sub(num1, num2);

            }

            else if (e.getSource() == btnMultiplicar) {

                resultado = c.mul(num1, num2);

            }

            else if (e.getSource() == btnDividir) {

                resultado = c.div(num1, num2);

            }

            lblResultado.setText(String.valueOf(resultado));

        }

        catch (ArithmeticException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se puede dividir entre cero"
            );
        }

        catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese números válidos"
            );
        }

        catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + ex.getMessage()
            );
        }
    }

    public static void main(String[] args) {

        new CalculatorGUI();
    }
}