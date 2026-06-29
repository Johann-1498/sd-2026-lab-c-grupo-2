#  Laboratorio 11: Seguridad Informática en Sistemas Distribuidos

**Caso de Estudio:** LogiMarket Perú S.A.C.
**Curso:** Sistemas Distribuidos

Este repositorio contiene la implementación práctica y documental de una arquitectura segura basada en microservicios, aplicando controles de ciberseguridad, criptografía y auditoría según los marcos **NIST CSF 2.0, MITRE ATT&CK y CISA**.

##  Equipo de Trabajo y División

| Integrante | Rol / Responsabilidad | Estado |
| :--- | :--- | :--- |
| **Integrante 1 Alexandra Quispe Arratea )** | Análisis de amenazas, MITRE ATT&CK y Matriz de Riesgos. |  Completado |
| **Integrante 2 (Nombre)** | Autenticación segura (Login, MFA, JWT, RBAC). |  Pendiente |
| **Integrante 3 (Nombre)** | Seguridad de comunicaciones (OpenSSL, HTTPS, Wireshark). |  Pendiente |
| **Integrante 4 (Nombre)** | Interfaz (Frontend), APIs protegidas, Rate Limiting y Auditoría. |  Pendiente |

---

##  Estructura del Repositorio

* `/docs`: Contiene la documentación técnica, matrices de riesgo y diagramas de arquitectura.
* `/backend`: Código fuente en Python (Flask) con los endpoints, validación JWT y auditoría.
* `/frontend`: Interfaz web (HTML/JS) para consumir las APIs seguras.
* `/certs`: Certificados autofirmados generados con OpenSSL para habilitar HTTPS.
* `/postman`: Colecciones para probar los endpoints y evidenciar el cifrado.

---

##  Instrucciones de Ejecución
*(Esta sección será llenada por los Integrantes 2, 3 y 4 cuando suban su código)*

1. Instalar dependencias: `pip install -r backend/requirements.txt`
2. Generar certificados SSL... (Pendiente)
3. Ejecutar el servidor... (Pendiente)