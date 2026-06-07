import psycopg2

def transferencia_distribuida():
    print("--- INICIANDO TRANSACCIÓN BANCARIA DISTRIBUIDA ---")
    
    # 1. Configuración de conexiones a los nodos participantes
    try:
        conn_aqp = psycopg2.connect(
            dbname="banco_arequipa", user="postgres", password="admin", host="127.0.0.1", port="6432"
        )
        # Conexión a Cusco (Ahora en el puerto 6433)
        conn_cus = psycopg2.connect(
            dbname="banco_cusco", user="postgres", password="admin", host="127.0.0.1", port="6433"
        )
        # Generar cursores para ejecutar comandos SQL
        cur_aqp = conn_aqp.cursor()
        cur_cus = conn_cus.cursor()
        
        trx_id = 'trx_transfer_25k'

        print("Conexiones establecidas a los nodos de Arequipa y Cusco.\n")

        print("Ejecutando operaciones locales en los nodos...")
        # Nodo Arequipa: Se debitan los S/ 25,000
        cur_aqp.execute("UPDATE cuenta SET saldo = saldo - 25000 WHERE id = 1;")
        
        # Nodo Cusco: Se acreditan los S/ 25,000
        cur_cus.execute("UPDATE cuenta SET saldo = saldo + 25000 WHERE id = 1;")

        # PROTOCOLO 2PC - FASE 1: PREPARE 
        print("Iniciando Fase 1: PREPARE TRANSACTION...")
        cur_aqp.execute(f"PREPARE TRANSACTION '{trx_id}';")
        cur_cus.execute(f"PREPARE TRANSACTION '{trx_id}';")
        print("--> Fase 1 Completada: Ambos nodos respondieron afirmativamente.\n")

        # PROTOCOLO 2PC - FASE 2: COMMIT 
        print("Iniciando Fase 2: COMMIT PREPARED...")
        cur_aqp = conn_aqp.cursor() 
        cur_cus = conn_cus.cursor()
        
        cur_aqp.execute(f"COMMIT PREPARED '{trx_id}';")
        cur_cus.execute(f"COMMIT PREPARED '{trx_id}';")
        print("--> Fase 2 Completada: Transacción confirmada en todos los nodos.\n")

        conn_aqp.commit()
        conn_cus.commit()

        print("¡TRANSFERENCIA DE S/ 25,000 EXITOSA!")

        cur_aqp.execute("SELECT saldo FROM cuenta WHERE id = 1;")
        cur_cus.execute("SELECT saldo FROM cuenta WHERE id = 1;")
        print(f"Nuevo Saldo Arequipa: S/ {cur_aqp.fetchone()[0]}")
        print(f"Nuevo Saldo Cusco: S/ {cur_cus.fetchone()[0]}")

    except Exception as e:
        import traceback
        print("\n❌ Error durante la transacción distribuida:")
        traceback.print_exc()
        
    finally:
        # Cerrar conexiones para evitar bloqueos y liberar recursos
        if 'conn_aqp' in locals() and conn_aqp:
            conn_aqp.close()
        if 'conn_cus' in locals() and conn_cus:
            conn_cus.close()

if __name__ == "__main__":
    transferencia_distribuida()