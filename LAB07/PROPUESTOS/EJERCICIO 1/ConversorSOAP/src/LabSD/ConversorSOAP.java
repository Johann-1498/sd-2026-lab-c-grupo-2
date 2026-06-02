package LabSD;

import javax.jws.WebService;

@WebService(endpointInterface = "LabSD.SOAPI")
public class ConversorSOAP implements SOAPI {
    @Override
    public double cToF(double c) {
        return (c * 9 / 5) + 32;
    }

    @Override
    public double fToC(double f) {
        return (f - 32) * 5 / 9;
    }
}