package LabSD;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface VentasI {
    @WebMethod
    public String listarProductos();
    
    @WebMethod
    public String realizarVenta(int idProducto, int cantidad);
}