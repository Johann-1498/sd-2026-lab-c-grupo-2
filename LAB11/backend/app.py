# app.py
from flask import Flask, request, jsonify
from auth import login_user, token_required, role_required

app = Flask(__name__)

@app.route("/login", methods=["POST"])
def login():
    data = request.get_json()
    username = data.get("username")
    password = data.get("password")
    mfa_code = data.get("mfa_code")

    token, error = login_user(username, password, mfa_code)
    if error:
        return jsonify({"error": error}), 401

    return jsonify({"token": token})

@app.route("/perfil", methods=["GET"])
@token_required
def perfil():
    return jsonify({"usuario": request.user["sub"], "rol": request.user["role"]})

@app.route("/inventario", methods=["GET"])
@token_required
@role_required("admin", "empleado")
def inventario():
    return jsonify({"data": "Inventario de LogiMarket (simulado)"})

@app.route("/pagos", methods=["POST"])
@token_required
@role_required("admin")
def pagos():
    return jsonify({"data": "Pago procesado (simulado)"})

if __name__ == "__main__":
    app.run(debug=True, port=5000)