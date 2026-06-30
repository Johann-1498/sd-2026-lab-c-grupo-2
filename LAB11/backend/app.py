# app.py
import os
from flask import Flask, request, jsonify
from flask_cors import CORS
from auth import login_user, token_required, role_required
from audit import write_log, read_logs
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address

app = Flask(__name__)
CORS(app)

limiter = Limiter(
    get_remote_address,
    app=app,
    default_limits=["20 per minute"]
)
@app.errorhandler(429)
def ratelimit_handler(e):
    write_log(
        "RATE_LIMIT_EXCEEDED",
        f"Se excedió el límite de solicitudes en el endpoint {request.path}",
        "anonymous",
        "ALERT"
    )
    return jsonify({
        "error": "Demasiadas solicitudes. Intente nuevamente más tarde.",
        "detalle": str(e.description)
    }), 429

@app.route("/login", methods=["POST"])
@limiter.limit("5 per minute")
def login():
    data = request.get_json()
    username = data.get("username")
    password = data.get("password")
    mfa_code = data.get("mfa_code")

    token, error = login_user(username, password, mfa_code)

    if error:
        write_log("LOGIN_FAILED", error, username, "WARNING")
        return jsonify({"error": error}), 401

    write_log("LOGIN_SUCCESS", "Inicio de sesión exitoso", username, "INFO")
    return jsonify({"token": token})

@app.route("/perfil", methods=["GET"])
@token_required
def perfil():
    write_log("ACCESS_PROFILE", "Consulta de perfil", request.user["sub"], "INFO")
    return jsonify({"usuario": request.user["sub"], "rol": request.user["role"]})


@app.route("/inventario", methods=["GET"])
@token_required
@role_required("admin", "empleado")
def inventario():
    write_log("ACCESS_INVENTORY", "Consulta de inventario", request.user["sub"], "INFO")
    return jsonify({"data": "Inventario de LogiMarket (simulado)"})


@app.route("/pagos", methods=["POST"])
@token_required
@role_required("admin")
def pagos():
    write_log("ACCESS_PAYMENTS", "Procesamiento de pago simulado", request.user["sub"], "INFO")
    return jsonify({"data": "Pago procesado (simulado)"})


@app.route("/logistica", methods=["GET"])
@token_required
@role_required("admin", "empleado")
def logistica():
    write_log("ACCESS_LOGISTICS", "Consulta de logística", request.user["sub"], "INFO")
    return jsonify({"data": "Servicio de logística de LogiMarket (simulado)"})

@app.route("/auditoria", methods=["GET"])
@token_required
@role_required("admin")
def auditoria():
    write_log("ACCESS_AUDIT", "Consulta de registros de auditoría", request.user["sub"], "INFO")
    logs = read_logs()
    return jsonify({"logs": logs})

if __name__ == "__main__":
    BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    cert_path = os.path.join(BASE_DIR, "certs", "cert.pem")
    key_path = os.path.join(BASE_DIR, "certs", "key.pem")

    print("\n=======================================================")
    print(" INICIANDO SERVIDOR SEGURO FLASK (HTTPS) - LOGIMARKET ")
    print(f" Certificado: {cert_path}")
    print(f" Llave Privada: {key_path}")
    print("=======================================================\n")

    app.run(
        host="127.0.0.1",
        port=5000,
        debug=False,
        ssl_context=(cert_path, key_path)
    )