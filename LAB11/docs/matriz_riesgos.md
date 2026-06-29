# Matriz de Riesgos - LogiMarket Perú S.A.C.

| Activo Crítico | Incidente | Vulnerabilidad | Táctica MITRE ATT&CK | Recomendación (CISA / NIST CSF 2.0) | Nivel de Riesgo |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Cuentas de Clientes** | Acceso no autorizado | Falta de controles robustos de login. | **TA0006 (Credential Access)** | Implementar MFA y JWT (NIST: PROTECT). | **CRÍTICO** |
| **Comunicaciones API** | Interceptación de tráfico | Ausencia de cifrado SSL/TLS. | **TA0009 (Collection / MitM)** | "Secure by Design": Cifrado local con OpenSSL. | **CRÍTICO** |
| **Base de Datos (PII)** | Exposición de datos | Falta de validación de roles en endpoints. | **TA0010 (Exfiltration)** | Aplicar RBAC / Zero Trust Architecture. | **ALTO** |
| **Identidad Empleados** | Credenciales compartidas | Mala gestión de identidades corporativas. | **TA0004 (Privilege Escalation)** | Cuentas individuales ligadas a un rol específico. | **ALTO** |
| **Sistema General** | Cero visibilidad de ataques | Ausencia total de un sistema de Logs. | **TA0005 (Defense Evasion)** | Crear archivo `audit.log` centralizado (NIST: DETECT). | **ALTO** |

## Conclusión Técnica y Roadmap
Según el análisis de riesgos alineado a **NIST CSF 2.0**, LogiMarket presenta un riesgo CRÍTICO en sus capacidades de protección y detección. Para solucionar esto en el presente laboratorio, nuestro equipo aplicará medidas de **higiene cibernética (CISA)**: 
1. El Integrante 2 mitigará el *Credential Access* implementando MFA y JWT.
2. El Integrante 3 mitigará el *Adversary-in-the-Middle* generando certificados HTTPS.
3. El Integrante 4 habilitará la función *DETECT* del NIST mediante la creación de un sistema de auditoría (logs) y Rate Limiting para evitar ataques de denegación de servicio.