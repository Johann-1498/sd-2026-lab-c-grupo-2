from flask import Flask, request, jsonify

app = Flask(__name__)

# Base de datos simulada en memoria con los estudiantes solicitados
estudiantes = [
    {"id": 1, "codigo": "20231452", "nombre": "Alexandra", "apellido": "Quispe", "correo": "aquispeq@unsa.edu.pe"},
    {"id": 2, "codigo": "20240981", "nombre": "Paul", "apellido": "Cari", "correo": "pcari@unsa.edu.pe"},
    {"id": 3, "codigo": "20223512", "nombre": "Johan", "apellido": "Caceres", "correo": "jcaceresca@unsa.edu.pe"},
    {"id": 4, "codigo": "20230744", "nombre": "Juan", "apellido": "Zeballos", "correo": "jzeballos@unsa.edu.pe"}
]

# Cabeceras CORS para conectar con el HTML de forma segura
@app.after_request
def after_request(response):
    response.headers.add('Access-Control-Allow-Origin', '*')
    response.headers.add('Access-Control-Allow-Headers', 'Content-Type,Authorization')
    response.headers.add('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE,OPTIONS')
    return response

# GET /estudiantes -> Consultar estudiantes
@app.route('/estudiantes', methods=['GET'])
def listar():
    return jsonify(estudiantes), 200

# GET /estudiantes/<id> -> Consultar un estudiante por ID
@app.route('/estudiantes/<int:id>', methods=['GET'])
def obtener_uno(id):
    estudiante = next((e for e in estudiantes if e["id"] == id), None)
    if estudiante is None:
        return jsonify({"error": "Estudiante no encontrado"}), 404
    return jsonify(estudiante), 200

# POST /estudiantes -> Registrar estudiante
@app.route('/estudiantes', methods=['POST'])
def agregar():
    data = request.json
    if not data or not all(k in data for k in ("codigo", "nombre", "apellido")):
        return jsonify({"error": "Datos incompletos"}), 400
    
    nuevo_id = max([e["id"] for e in estudiantes], default=0) + 1
    nuevo_estudiante = {
        "id": nuevo_id,
        "codigo": data["codigo"],
        "nombre": data["nombre"],
        "apellido": data["apellido"],
        "correo": data.get("correo", "")
    }
    estudiantes.append(nuevo_estudiante)
    return jsonify({"ok": True, "estudiante": nuevo_estudiante}), 201

# PUT /estudiantes/<id> -> Actualizar estudiante
@app.route('/estudiantes/<int:id>', methods=['PUT'])
def actualizar(id):
    data = request.json
    estudiante = next((e for e in estudiantes if e["id"] == id), None)
    
    if estudiante is None:
        return jsonify({"error": "Estudiante no encontrado"}), 404
        
    estudiante["codigo"] = data.get("codigo", estudiante["codigo"])
    estudiante["nombre"] = data.get("nombre", estudiante["nombre"])
    estudiante["apellido"] = data.get("apellido", estudiante["apellido"])
    estudiante["correo"] = data.get("correo", estudiante["correo"])
    
    return jsonify({"actualizado": True, "estudiante": estudiante}), 200

# DELETE /estudiantes/<id> -> Eliminar estudiante
@app.route('/estudiantes/<int:id>', methods=['DELETE'])
def eliminar(id):
    global estudiantes
    estudiante = next((e for e in estudiantes if e["id"] == id), None)
    if estudiante is None:
        return jsonify({"error": "Estudiante no encontrado"}), 404
        
    estudiantes = [e for e in estudiantes if e["id"] != id]
    return jsonify({"eliminado": True})

if __name__ == '__main__':
    app.run(debug=True, port=5001)