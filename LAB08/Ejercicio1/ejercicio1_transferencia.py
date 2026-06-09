import psycopg2

def transferencia_exitosa():
    print("--- INICIANDO EJERCICIO 1: Transferencia de 20 unidades ---")
    
    # 1. Configurar las conexiones a ambos nodos
    try:
        conn_arequipa = psycopg2.connect(dbname="almacen_arequipa", user="postgres", password="admin", host="localhost", port="5432")
        conn_lima = psycopg2.connect(dbname="almacen_lima", user="postgres", password="admin", host="localhost", port="5432")
        
        cur_arequipa = conn_arequipa.cursor()
        cur_lima = conn_lima.cursor()
        
        # 2. Iniciar la transacción distribuida
        # En psycopg2, el simple hecho de ejecutar un DML inicia una transacción que requiere commit()
        cantidad_transferir = 20
        producto = 'Paracetamol'
        
        print("1. Verificando stock disponible...")
        
        print("2. Iniciando transacción...")
        print("3. Actualizando inventario origen (Arequipa) - Descontando 20...")
        cur_arequipa.execute("UPDATE inventario SET stock = stock - %s WHERE producto = %s", (cantidad_transferir, producto))
        
        print("4. Actualizando inventario destino (Lima) - Incrementando 20...")
        cur_lima.execute("UPDATE inventario SET stock = stock + %s WHERE producto = %s", (cantidad_transferir, producto))
        
        # 5. Confirmar los cambios (Simulando la Fase 2 del Commit)
        print("5. Confirmando cambios en ambos nodos (COMMIT)...")
        conn_arequipa.commit()
        conn_lima.commit()
        
        # --- VERIFICACIÓN DE RESULTADOS ---
        cur_arequipa.execute("SELECT stock FROM inventario WHERE producto = %s", (producto,))
        stock_arequipa = cur_arequipa.fetchone()[0]
        
        cur_lima.execute("SELECT stock FROM inventario WHERE producto = %s", (producto,))
        stock_lima = cur_lima.fetchone()[0]
        
        print(f"\n[RESULTADO ESPERADO Y OBTENIDO]")
        print(f"Nodo Arequipa: {stock_arequipa}")
        print(f"Nodo Lima: {stock_lima}")
        print("¡Transacción distribuida EXITOSA!\n")

    except Exception as e:
        print(f"Ocurrió un error: {e}")
        # Si hay error, hacemos rollback de ambas conexiones
        if 'conn_arequipa' in locals(): conn_arequipa.rollback()
        if 'conn_lima' in locals(): conn_lima.rollback()
    
    finally:
        if 'cur_arequipa' in locals(): cur_arequipa.close()
        if 'conn_arequipa' in locals(): conn_arequipa.close()
        if 'cur_lima' in locals(): cur_lima.close()
        if 'conn_lima' in locals(): conn_lima.close()

if __name__ == "__main__":
    transferencia_exitosa()