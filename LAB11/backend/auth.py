# auth.py
from functools import wraps
from flask import request, jsonify
from users import get_user
from security import generate_token, decode_token

def login_user(username, password, mfa_code):
    user = get_user(username)
    if not user:
        return None, "Usuario no encontrado"
    if user["password"] != password:
        return None, "Contraseña incorrecta"
    if user["mfa_code"] != mfa_code:
        return None, "Código MFA incorrecto"

    token = generate_token(username, user["role"])
    return token, None

def token_required(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        auth_header = request.headers.get("Authorization", "")
        if not auth_header.startswith("Bearer "):
            return jsonify({"error": "Token no proporcionado"}), 401

        token = auth_header.split(" ")[1]
        payload = decode_token(token)
        if not payload:
            return jsonify({"error": "Token inválido o expirado"}), 401

        request.user = payload
        return f(*args, **kwargs)
    return decorated

def role_required(*roles):
    def decorator(f):
        @wraps(f)
        def decorated(*args, **kwargs):
            if request.user["role"] not in roles:
                return jsonify({"error": "Acceso denegado por rol insuficiente"}), 403
            return f(*args, **kwargs)
        return decorated
    return decorator