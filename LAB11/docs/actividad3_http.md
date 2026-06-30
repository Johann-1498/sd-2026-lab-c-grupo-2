# Actividad 3: Seguridad de Comunicaciones e Implementación HTTPS

## 1. Fundamentos de Cifrado y SSL/TLS
Para mitigar de forma definitiva la vulnerabilidad de interceptación de tráfico de red en tránsito (Táctica MITRE ATT&CK: **TA0009 - Collection / Sniffing**) detectada en la arquitectura de LogiMarket Perú S.A.C., se implementó un canal de comunicación criptográfico robusto basado en el protocolo **TLS (Transport Layer Security)**. 

Este esquema asegura que datos de alta confidencialidad (como contraseñas en texto plano e identificadores JWT de sesión) viajen de manera indescifrable para agentes maliciosos intermedios, cumpliendo con las directrices de diseño seguro de CISA y la función "PROTECT" de NIST CSF 2.0.

## 2. Generación de Certificados con OpenSSL
Se ha configurado una Infraestructura de Clave Pública (PKI) local simulada. Mediante la suite criptográfica de OpenSSL se generó una clave privada RSA de 4096 bits y un certificado digital de clave pública estándar X.509 válido por un período de 365 días. 

El comando utilizado en la consola del sistema fue el siguiente:
```bash
openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 365 -nodes