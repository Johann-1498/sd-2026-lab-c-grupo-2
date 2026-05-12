import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.rmi.Naming;
import javax.swing.*;
import javax.swing.border.*;

public class CalculatorGUI extends JFrame implements ActionListener {

    JTextField txtNum1, txtNum2;
    JLabel lblResultado;
    JButton btnSumar, btnRestar, btnMultiplicar, btnDividir;
    Calculator c;

    // ── Paleta profesional light ─────────────────────────────────────────────
    private static final Color BG_BASE    = new Color(246, 247, 250);
    private static final Color BG_CARD    = new Color(255, 255, 255);
    private static final Color BG_INPUT   = new Color(252, 252, 253);
    private static final Color ACCENT     = new Color(79,  70, 229);  // índigo
    private static final Color ACCENT_HOV = new Color(67,  56, 202);
    private static final Color ACCENT_PRE = new Color(55,  48, 163);
    private static final Color BTN_SEC    = new Color(238, 237, 255); // ghost bg
    private static final Color BTN_SEC_FG = new Color(79,  70, 229); // ghost fg
    private static final Color TEXT_H     = new Color(17,  24,  39);
    private static final Color TEXT_BODY  = new Color(55,  65,  81);
    private static final Color TEXT_MUTED = new Color(156, 163, 175);
    private static final Color BORDER_CLR = new Color(226, 232, 240);
    private static final Color BORDER_FOC = new Color(165, 180, 252);
    private static final Color SUCCESS    = new Color(22,  163,  74);
    private static final Color ERROR_CLR  = new Color(239,  68,  68);

    private JLabel lblStatus;
    private JLabel lblStatusDot;

    public CalculatorGUI() {

        setTitle("Calculadora RMI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        setContentPane(buildRoot());
        pack();
        // Forzar ancho mínimo sin colapsar el alto calculado por pack()
        setSize(420, getHeight());
        setLocationRelativeTo(null);

        // ── Conexión RMI ────────────────────────────────────────────────────
        try {
            c = (Calculator) Naming.lookup("rmi://localhost/CalculatorService");
            setStatus(true, "Conectado al servidor");
        } catch (Exception e) {
            setStatus(false, "Sin conexión al servidor");
            JOptionPane.showMessageDialog(this, "Error conectando al servidor RMI");
        }

        setVisible(true);
    }

    // ── Construcción del UI ──────────────────────────────────────────────────

    private JPanel buildRoot() {
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(BG_BASE);
        root.setBorder(new EmptyBorder(28, 28, 24, 28));


        root.add(buildHeader());
        root.add(Box.createVerticalStrut(20));

        // Card con los dos campos de entrada
        JPanel inputCard = buildCard();
        inputCard.setLayout(new BoxLayout(inputCard, BoxLayout.Y_AXIS));
        inputCard.add(buildFieldGroup("Número 1", txtNum1 = buildField()));
        inputCard.add(Box.createVerticalStrut(14));
        inputCard.add(buildFieldGroup("Número 2", txtNum2 = buildField()));
        root.add(inputCard);
        root.add(Box.createVerticalStrut(14));

        // Grid 2×2 de botones
        JPanel btnGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        btnGrid.setBackground(BG_BASE);
        btnGrid.setAlignmentX(LEFT_ALIGNMENT);
        btnGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 96));

        btnSumar       = buildButton("Sumar",       true);
        btnRestar      = buildButton("Restar",      false);
        btnMultiplicar = buildButton("Multiplicar", false);
        btnDividir     = buildButton("Dividir",     false);

        btnGrid.add(btnSumar);
        btnGrid.add(btnRestar);
        btnGrid.add(btnMultiplicar);
        btnGrid.add(btnDividir);
        root.add(btnGrid);
        root.add(Box.createVerticalStrut(14));

        root.add(buildResultCard());
        root.add(Box.createVerticalStrut(10));
        root.add(buildStatusBar());

        // Listeners
        btnSumar.addActionListener(this);
        btnRestar.addActionListener(this);
        btnMultiplicar.addActionListener(this);
        btnDividir.addActionListener(this);

        return root;
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(BG_BASE);
        h.setAlignmentX(LEFT_ALIGNMENT);
        h.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel title = new JLabel("Calculadora RMI");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_H);

        JLabel badge = new JLabel(" SD ");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(BTN_SEC_FG);
        badge.setBackground(BTN_SEC);
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(2, 6, 2, 6));

        h.add(title, BorderLayout.WEST);
        h.add(badge, BorderLayout.EAST);
        return h;
    }

    private JPanel buildCard() {
        JPanel card = new JPanel();
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(16, 16, 16, 16)
        ));
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        return card;
    }

    private JPanel buildFieldGroup(String labelText, JTextField field) {
        JPanel g = new JPanel(new BorderLayout(0, 5));
        g.setBackground(BG_CARD);
        g.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(TEXT_BODY);
        g.add(lbl,   BorderLayout.NORTH);
        g.add(field, BorderLayout.CENTER);
        return g;
    }

    private JTextField buildField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        f.setBackground(BG_INPUT);
        f.setForeground(TEXT_H);
        f.setCaretColor(ACCENT);
        f.setPreferredSize(new Dimension(0, 36));
        f.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(4, 10, 4, 10)
        ));
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                f.setBorder(new CompoundBorder(
                    new LineBorder(BORDER_FOC, 2, true),
                    new EmptyBorder(3, 9, 3, 9)
                ));
            }
            @Override public void focusLost(FocusEvent e) {
                f.setBorder(new CompoundBorder(
                    new LineBorder(BORDER_CLR, 1, true),
                    new EmptyBorder(4, 10, 4, 10)
                ));
            }
        });
        return f;
    }

    /**
     * primary=true  → botón relleno índigo (acción principal)
     * primary=false → botón ghost índigo (acciones secundarias)
     */
    private JButton buildButton(String text, boolean primary) {
        Color bgNormal = primary ? ACCENT     : BTN_SEC;
        Color bgHover  = primary ? ACCENT_HOV : new Color(224, 222, 255);
        Color bgPress  = primary ? ACCENT_PRE : new Color(209, 207, 255);
        Color fg       = primary ? Color.WHITE : BTN_SEC_FG;

        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed()  ? bgPress
                         : getModel().isRollover() ? bgHover
                         : bgNormal;
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        b.setForeground(fg);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(0, 40));
        b.setBorder(new EmptyBorder(0, 16, 0, 16));
        return b;
    }

    private JPanel buildResultCard() {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(12, 16, 12, 16)
        ));
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel tag = new JLabel("RESULTADO");
        tag.setFont(new Font("Segoe UI", Font.BOLD, 10));
        tag.setForeground(TEXT_MUTED);

        lblResultado = new JLabel("—");
        lblResultado.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblResultado.setForeground(TEXT_H);

        card.add(tag,          BorderLayout.NORTH);
        card.add(lblResultado, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        bar.setBackground(BG_BASE);
        bar.setAlignmentX(LEFT_ALIGNMENT);
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));

        lblStatusDot = new JLabel("●");
        lblStatusDot.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        lblStatusDot.setForeground(TEXT_MUTED);

        lblStatus = new JLabel("Conectando...");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(TEXT_MUTED);

        bar.add(lblStatusDot);
        bar.add(lblStatus);
        return bar;
    }

    private void setStatus(boolean ok, String msg) {
        Color c = ok ? SUCCESS : ERROR_CLR;
        lblStatusDot.setForeground(c);
        lblStatus.setForeground(c);
        lblStatus.setText(msg);
        if (ok) lblResultado.setText("—");
    }

    // ── Lógica de operaciones (sin cambios) ──────────────────────────────────

    @Override
    public void actionPerformed(ActionEvent e) {

        try {

            double num1 = Double.parseDouble(txtNum1.getText().trim());
            double num2 = Double.parseDouble(txtNum2.getText().trim());

            double resultado = 0;

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

            lblResultado.setText(String.format("%.2f", resultado));
            lblResultado.setForeground(TEXT_H);

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