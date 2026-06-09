import psycopg2
from psycopg2.extensions import ISOLATION_LEVEL_AUTOCOMMIT

def preparar_entorno():
    # Conectarse a la BD por defecto del docker con la nueva contraseña 'admin'
    conn = psycopg2.connect(dbname="postgres", user="postgres", password="admin", host="localhost", port="5432")
    conn.set_isolation_level(ISOLATION_LEVEL_AUTOCOMMIT)
    cur = conn.cursor()

    # 1. Crear las Bases de Datos
    print("Creando bases de datos...")
    try: cur.execute("CREATE DATABASE almacen_arequipa;")
    except: print("almacen_arequipa ya existe")
    
    try: cur.execute("CREATE DATABASE almacen_lima;")
    except: print("almacen_lima ya existe")
    
    cur.close()
    conn.close()

    # 2. Crear tablas e insertar datos en Arequipa
    print("Configurando Nodo Arequipa...")
    conn_aqp = psycopg2.connect(dbname="almacen_arequipa", user="postgres", password="admin", host="localhost", port="5432")
    cur_aqp = conn_aqp.cursor()
    cur_aqp.execute("""
        CREATE TABLE IF NOT EXISTS inventario(
            id SERIAL PRIMARY KEY, producto VARCHAR(100), stock INTEGER
        );
        TRUNCATE TABLE inventario;
        INSERT INTO inventario(producto, stock) VALUES('Paracetamol', 100);
    """)
    conn_aqp.commit()
    conn_aqp.close()

    # 3. Crear tablas e insertar datos en Lima
    print("Configurando Nodo Lima...")
    conn_lima = psycopg2.connect(dbname="almacen_lima", user="postgres", password="admin", host="localhost", port="5432")
    cur_lima = conn_lima.cursor()
    cur_lima.execute("""
        CREATE TABLE IF NOT EXISTS inventario(
            id SERIAL PRIMARY KEY, producto VARCHAR(100), stock INTEGER
        );
        TRUNCATE TABLE inventario;
        INSERT INTO inventario(producto, stock) VALUES('Paracetamol', 50);
    """)
    conn_lima.commit()
    conn_lima.close()
    
    print("¡Bases de datos y tablas creadas con éxito! Ya puedes correr los Ejercicios 1 y 2.")

if __name__ == "__main__":
    preparar_entorno()