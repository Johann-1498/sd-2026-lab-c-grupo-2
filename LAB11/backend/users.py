# users.py
USERS = {
    "admin1": {
        "password": "admin123",
        "mfa_code": "111111",
        "role": "admin"
    },
    "empleado1": {
        "password": "emp123",
        "mfa_code": "222222",
        "role": "empleado"
    },
    "cliente1": {
        "password": "cli123",
        "mfa_code": "333333",
        "role": "cliente"
    }
}

def get_user(username):
    return USERS.get(username)