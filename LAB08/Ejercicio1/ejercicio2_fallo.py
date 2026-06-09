import psycopg2

def simulacion_de_fallo():
    print("--- INICIANDO EJERCICIO 2: Simulación de Fallo en Nodo Destino ---")
    
    try:
        conn_arequipa = psycopg2.connect(dbname="almacen_arequipa", user="postgres", password="admin", host="localhost", port="5432")
        conn_lima = psycopg2.connect(dbname="almacen_lima", user="postgres", password="admin", host="localhost", port="5432")
        
        cur_arequipa = conn_arequipa.cursor()
        cur_lima = conn_lima.cursor()
        
        cantidad_transferir = 20
        producto = 'Paracetamol'
        
        print("1. Iniciando transacción...")
        
        print("2. Descontando stock en Arequipa...")
        cur_arequipa.execute("UPDATE inventario SET stock = stock - %s WHERE producto = %s", (cantidad_transferir, producto))
        
        # 3. Simulamos que Lima se cayó antes de ejecutar su consulta
        print("3. Intentando conectar con Lima... (Simulando caída del Nodo Lima)")
        # Lanzamos intencionalmente una excepción para simular un Timeout o caída de nodo
        raise Exception("TIMEOUT: El nodo Lima ha dejado de responder. Conexión perdida.")
        
        # La siguiente línea nunca se ejecutará
        cur_lima.execute("UPDATE inventario SET stock = stock + %s WHERE producto = %s", (cantidad_transferir, producto))
        
        conn_arequipa.commit()
        conn_lima.commit()

    except Exception as e:
        print(f"\n[!] ERROR DETECTADO DURANTE LA TRANSACCIÓN: {e}")
        print("4. Ejecutando ROLLBACK distribuido en todos los nodos participantes...")
        
        # Deshacemos cualquier cambio para garantizar Atomicidad y Consistencia
        if 'conn_arequipa' in locals(): conn_arequipa.rollback()
        if 'conn_lima' in locals(): conn_lima.rollback()
        print("-> Rollback ejecutado correctamente. Cambios revertidos.\n")
    
    finally:
        # --- VERIFICACIÓN DE RESULTADOS TRAS EL FALLO ---
        if 'conn_arequipa' in locals() and not conn_arequipa.closed:
            c_ar = conn_arequipa.cursor()
            c_ar.execute("SELECT stock FROM inventario WHERE producto = 'Paracetamol'")
            stock_ar = c_ar.fetchone()[0]
            print(f"[RESULTADO ESPERADO Y OBTENIDO]")
            print(f"Nodo Arequipa: {stock_ar} (El descuento no se aplicó)")
            c_ar.close()
            
        if 'conn_lima' in locals() and not conn_lima.closed:
            c_li = conn_lima.cursor()
            c_li.execute("SELECT stock FROM inventario WHERE producto = 'Paracetamol'")
            stock_li = c_li.fetchone()[0]
            print(f"Nodo Lima: {stock_li}")
            c_li.close()

if __name__ == "__main__":
    simulacion_de_fallo()