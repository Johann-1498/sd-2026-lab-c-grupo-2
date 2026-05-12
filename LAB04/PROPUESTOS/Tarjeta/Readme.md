# Compilar y Ejecutar Proyecto RMI - Tarjeta

## 1. Ir a la carpeta raíz del proyecto

```powershell
cd "C:\CuartoAño\Sistemas Distribuidos\Laboratorio\sd-2026-lab-c-grupo-2\LAB04\PROPUESTOS"

2. Compilar
javac Tarjeta\*.java

3. Iniciar RMI Registry
$env:CLASSPATH="."
rmiregistry 1099

4. Ejecutar el servidor
java -cp . Tarjeta.ServidorTarjeta

5. Ejecutar el cliente
java -cp . Tarjeta.ClienteTarjeta