from zeep import Client
from zeep.exceptions import Fault

def cliente_calculadora_avanzado():
    print("==================================================")
    print("      CLIENTE SOAP - CALCULADORA         ")
    print("==================================================")
    
    # 1. URL del WSDL
    wsdl_url = 'http://www.dneonline.com/calculator.asmx?WSDL'
    
    try:
        # 2. Conexión al servicio
        print("[*] Conectando al servicio web SOAP...")
        cliente = Client(wsdl=wsdl_url)
        print("[OK] ¡Conexión establecida con éxito!\n")
        
        while True:
            print("--- MENÚ DE OPERACIONES (SOAP) ---")
            print("1. Sumar (Add)")
            print("2. Restar (Subtract)")
            print("3. Multiplicar (Multiply)")
            print("4. Dividir (Divide)")
            print("5. Salir")
            
            opcion = input("Elige una opción (1-5): ")
            
            if opcion == '5':
                print("Cerrando el cliente SOAP. ¡Hasta luego!")
                break
                
            if opcion in ['1', '2', '3', '4']:
                num1 = int(input("Ingresa el primer número: "))
                num2 = int(input("Ingresa el segundo número: "))
                
                print("\n[+] Enviando petición XML al servidor...")
                
                # 3. Consumo dinámico de los métodos del WSDL
                if opcion == '1':
                    resultado = cliente.service.Add(num1, num2)
                    print(f"[*] RESULTADO DEL SERVIDOR: {num1} + {num2} = {resultado}\n")
                elif opcion == '2':
                    resultado = cliente.service.Subtract(num1, num2)
                    print(f"[*] RESULTADO DEL SERVIDOR: {num1} - {num2} = {resultado}\n")
                elif opcion == '3':
                    resultado = cliente.service.Multiply(num1, num2)
                    print(f"[*] RESULTADO DEL SERVIDOR: {num1} * {num2} = {resultado}\n")
                elif opcion == '4':
                    if num2 == 0:
                        print("[ERROR] El servidor no puede dividir entre cero.\n")
                    else:
                        resultado = cliente.service.Divide(num1, num2)
                        print(f"[*] RESULTADO DEL SERVIDOR: {num1} / {num2} = {resultado}\n")
            else:
                print("[!] Opción no válida. Intenta de nuevo.\n")
                
    except Fault as f:
        print(f"[ERROR SOAP] El servidor devolvió un error: {f}")
    except Exception as e:
        print(f"[ERROR DE RED] No se pudo conectar al servicio: {e}")

if __name__ == "__main__":
    cliente_calculadora_avanzado()