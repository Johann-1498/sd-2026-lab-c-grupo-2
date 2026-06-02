package LabSD;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.swing.*;

public class ClienteVentasGrafico extends JFrame {
    
    private JTextArea txtInventario;
    private JTextField txtIdProducto, txtCantidad;
    private JButton btnActualizar, btnComprar;
    private VentasI servicioVentas;

    public ClienteVentasGrafico() {
        // Conexión dinámica nativa del laboratorio
        try {
            URL url = new URL("http://localhost:1517/WS/Ventas?wsdl");
            QName qname = new QName("http://LabSD/", "VentasSOAPService");
            Service service = Service.create(url, qname);
            this.servicioVentas = service.getPort(VentasI.class);
        } catch (Exception e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }

        // Estilos Visuales (Estilo Web HTML)
        Font fuenteTitulo = new Font("Segoe UI", Font.BOLD, 18);
        Font fuenteNormal = new Font("Segoe UI", Font.PLAIN, 13);
        Color colorFondo = new Color(240, 242, 245);
        Color colorBotonVerde = new Color(76, 175, 80);

        setTitle("Tienda en Línea - Cliente SOAP");
        setSize(480, 480);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(colorFondo);
        setLayout(new GridBagLayout());
        setLocationRelativeTo(null);

        // Tarjeta Central Blanca
        JPanel tarjeta = new JPanel(new GridBagLayout());
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx = 0;

        // Título
        JLabel lblTitulo = new JLabel("E-Commerce SOAP Panel", SwingConstants.CENTER);
        lblTitulo.setFont(fuenteTitulo);
        gbc.gridy = 0;
        tarjeta.add(lblTitulo, gbc);

        // Lista de Productos (Consola de Inventario)
        txtInventario = new JTextArea(8, 30);
        txtInventario.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtInventario.setEditable(false);
        JScrollPane scroll = new JScrollPane(txtInventario);
        gbc.gridy = 1;
        tarjeta.add(scroll, gbc);

        // Botón Actualizar Stock
        btnActualizar = new JButton("Ver / Actualizar Catálogo");
        btnActualizar.setFont(fuenteNormal);
        gbc.gridy = 2;
        tarjeta.add(btnActualizar, gbc);

        // Panel de Formulario de Compra
        JPanel form = new JPanel(new GridLayout(2, 2, 5, 5));
        form.setBackground(Color.WHITE);
        form.add(new JLabel("ID Producto:"));
        txtIdProducto = new JTextField();
        form.add(txtIdProducto);
        form.add(new JLabel("Cantidad:"));
        txtCantidad = new JTextField();
        form.add(txtCantidad);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(15, 0, 5, 0);
        tarjeta.add(form, gbc);

        // Botón Comprar
        btnComprar = new JButton("Confirmar Compra 🛒");
        btnComprar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnComprar.setBackground(colorBotonVerde);
        btnComprar.setForeground(Color.WHITE);
        btnComprar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 0, 5, 0);
        tarjeta.add(btnComprar, gbc);

        add(tarjeta);

        // --- ACCIONES DE LOS BOTONES ---
        
        // Acción de listar/actualizar productos
        btnActualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(servicioVentas != null) {
                    txtInventario.setText(servicioVentas.listarProductos());
                }
            }
        });

        // Acción de realizar compra
        btnComprar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int id = Integer.parseInt(txtIdProducto.getText());
                    int cant = Integer.parseInt(txtCantidad.getText());
                    
                    // Llamamos al servidor SOAP
                    String respuesta = servicioVentas.realizarVenta(id, cant);
                    
                    // Mostrar ticket de venta en una alerta emergente
                    JOptionPane.showMessageDialog(null, respuesta, "Ticket de Compra", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Auto-actualizar la lista del inventario
                    txtInventario.setText(servicioVentas.listarProductos());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Por favor ingresa datos numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClienteVentasGrafico().setVisible(true));
    }
}