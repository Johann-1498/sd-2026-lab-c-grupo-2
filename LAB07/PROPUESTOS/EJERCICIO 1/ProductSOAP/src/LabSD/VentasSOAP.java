package LabSD;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.List;

@WebService(endpointInterface = "LabSD.VentasI")
public class VentasSOAP implements VentasI {
    
    private List<Producto> inventario;

    public VentasSOAP() {
        inventario = new ArrayList<>();
        // Productos iniciales de la tienda en línea
        inventario.add(new Producto(1, "Laptop Gamer", 1200.00, 10));
        inventario.add(new Producto(2, "Mouse Inalámbrico", 45.50, 50));
        inventario.add(new Producto(3, "Teclado Mecánico", 89.90, 25));
    }

    @Override
    public String listarProductos() {
        StringBuilder sb = new StringBuilder("--- INVENTARIO DISPONIBLE ---\n");
        for (Producto p : inventario) {
            sb.append(String.format("ID: %d | %s | Precio: $%.2f | Stock: %d uds\n", 
                    p.getId(), p.getNombre(), p.getPrecio(), p.getStock()));
        }
        return sb.toString();
    }

    @Override
    public String realizarVenta(int idProducto, int cantidad) {
        for (Producto p : inventario) {
            if (p.getId() == idProducto) {
                if (p.getStock() >= cantidad) {
                    p.setStock(p.getStock() - cantidad); // Restar del inventario
                    double total = p.getPrecio() * cantidad;
                    return String.format("¡VENTA EXITOSA!\nProducto: %s\nCantidad: %d\nTotal pagado: $%.2f\nStock restante: %d", 
                            p.getNombre(), cantidad, total, p.getStock());
                } else {
                    return "Error: No hay suficiente stock disponible.";
                }
            }
        }
        return "Error: Producto no encontrado.";
    }
}