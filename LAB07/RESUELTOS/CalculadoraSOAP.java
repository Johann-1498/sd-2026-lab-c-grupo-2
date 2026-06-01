import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public class CalculadoraSOAP {

  @WebMethod
  public int sumar(int a, int b) {
    return a + b;
  }
}
