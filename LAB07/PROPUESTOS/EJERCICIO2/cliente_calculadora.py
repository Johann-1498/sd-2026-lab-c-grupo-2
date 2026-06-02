from zeep import Client

def consumir_calculadora():
    # URL pública solicitada
    wsdl_url = 'http://www.dneonline.com/calculator.asmx?WSDL'
    
    try:
        # Nos conectamos al Web Service
        cliente = Client(wsdl=wsdl_url)
        print("[OK] Conectado al servicio de la calculadora web.\n")
        
        # Invocamos la función Add con los parámetros 5 y 8
        resultado = cliente.service.Add(5, 8)
        
        print(f"Resultado de la operación (5 + 8) = {resultado}")
        print("-> Resultado esperado: 13")
        
    except Exception as e:
        print(f"Error al conectar con el servicio: {e}")

if __name__ == "__main__":
    consumir_calculadora()