package LabSD;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface SOAPI {
    @WebMethod
    public double cToF(double c);
    @WebMethod
    public double fToC(double f);
}