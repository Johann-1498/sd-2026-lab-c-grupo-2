
# Cómo Ejecutar el Proyecto

## PASO 1 — Abrir Terminal

Abrir PowerShell en la carpeta raíz del proyecto:

```text
sd-2026-lab-c-grupo-2
```

## PASO 2 — Compilar Todo

Ejecutar:

```powershell
javac LAB04/RESUELTOS/*.java
```
# Ejecutar el Servidor

## PASO 3 — Iniciar Servidor RMI

Ejecutar:

```powershell
java -cp . LAB04.RESUELTOS.CalculatorServer
```

Debe aparecer:

```text
RMI Registry iniciado
Servidor listo
```

# Ejecutar la Interfaz Gráfica

## PASO 4 — Abrir Nueva Terminal

Abrir otra terminal PowerShell.

Ubicarse nuevamente en:

```
sd-2026-lab-c-grupo-2
```

## PASO 5 — Ejecutar GUI

Ejecutar:

```powershell
java -cp . LAB04.RESUELTOS.CalculatorGUI
```

Se abrirá la calculadora gráfica.

