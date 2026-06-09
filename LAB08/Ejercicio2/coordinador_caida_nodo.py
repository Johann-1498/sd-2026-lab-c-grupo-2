import psycopg2
import subprocess
import time

def transferencia_con_caida_nodo():
    print("="*60)
    print("SIMULACIÓN: CAÍDA DE NODO DURANTE 2PC")
    print("="*60)
    
    conn_aqp = None
    conn_cus = None
    
    try:
        conn_aqp = psycopg2.connect(
            dbname="banco_arequipa", user="postgres", password="admin", 
            host="127.0.0.1", port="6432"
        )
        conn_cus = psycopg2.connect(
            dbname="banco_cusco", user="postgres", password="admin", 
            host="127.0.0.1", port="6433"
        )
        
        conn_aqp.autocommit = False
        conn_cus.autocommit = False
        
        cur_aqp = conn_aqp.cursor()
        cur_cus = conn_cus.cursor()
        
        trx_id = 'trx_caida_nodo'
        monto = 25000

        print("\n[1] Ejecutando operaciones locales...")
        cur_aqp.execute(f"UPDATE cuenta SET saldo = saldo - {monto} WHERE id = 1;")
        cur_cus.execute(f"UPDATE cuenta SET saldo = saldo + {monto} WHERE id = 1;")
        print("    ✅ Débito en Arequipa: -25,000")
        print("    ✅ Crédito en Cusco: +25,000")

        print("\n[2] FASE 1 - PREPARE TRANSACTION...")
        cur_aqp.execute(f"PREPARE TRANSACTION '{trx_id}';")
        cur_cus.execute(f"PREPARE TRANSACTION '{trx_id}';")
        print("    ✅ Ambos nodos respondieron 'READY'")

        # 🔴 PUNTO DE FALLO
        print("\n" + "="*60)
        print("🔴 SIMULANDO CAÍDA DEL NODO CUSCO")
        print("="*60)
        print("ABRE OTRA TERMINAL y ejecuta: docker stop db_cusco")
        input("⚠️  Presiona ENTER después de detener el contenedor...")
        
        print("\n[3] FASE 2 - Intentando COMMIT PREPARED...")
        
        # COMMIT en Arequipa (éxito)
        cur_aqp.execute(f"COMMIT PREPARED '{trx_id}';")
        conn_aqp.commit()
        print("    ✅ Arequipa: COMMIT exitoso")
        
        # Esto fallará (Cusco está caído)
        cur_cus.execute(f"COMMIT PREPARED '{trx_id}';")
        conn_cus.commit()

    except psycopg2.OperationalError:
        print("\n    ❌ ERROR: Nodo Cusco no responde (está caído)")
        print("\n" + "="*60)
        print("📊 ESTADO DESPUÉS DEL FALLO:")
        print("="*60)
        print("🔸 Arequipa: Transacción CONFIRMADA (perdió 25,000)")
        print("🔸 Cusco: NODO CAÍDO - Transacción PREPARED PERDIDA")
        print("\n⚠️  INCONSISTENCIA CRÍTICA: Los fondos desaparecieron")
        
    finally:
        if conn_aqp:
            conn_aqp.close()

def recuperar_despues_caida():
    """Recuperar consistencia después de caída de nodo"""
    print("\n" + "="*60)
    print("🔄 RECUPERACIÓN DESPUÉS DE CAÍDA DE NODO")
    print("="*60)
    
    print("\n[1] Reiniciando nodo Cusco...")
    subprocess.run(["docker", "start", "db_cusco"], capture_output=True)
    time.sleep(5)
    print("    ✅ Nodo Cusco reiniciado")
    
    try:
        conn_aqp = psycopg2.connect(
            dbname="banco_arequipa", user="postgres", password="admin", 
            host="127.0.0.1", port="6432"
        )
        conn_aqp.autocommit = True
        cur_aqp = conn_aqp.cursor()
        
        print("\n[2] Verificando transacción pendiente en Arequipa...")
        cur_aqp.execute("SELECT gid FROM pg_prepared_xacts WHERE gid = 'trx_caida_nodo';")
        if cur_aqp.fetchone():
            print("    ✅ Transacción PREPARED encontrada en Arequipa")
            print("    Ejecutando ROLLBACK PREPARED...")
            cur_aqp.execute("ROLLBACK PREPARED 'trx_caida_nodo';")
            print("    ✅ ROLLBACK ejecutado")
            
            # Devolver fondos
            cur_aqp.execute("UPDATE cuenta SET saldo = saldo + 25000 WHERE id = 1;")
            print("    ✅ Fondos devueltos a Arequipa")
            
            # Verificar saldo
            cur_aqp.execute("SELECT saldo FROM cuenta WHERE id = 1;")
            saldo = cur_aqp.fetchone()[0]
            print(f"\n📊 Saldo final Arequipa: S/ {saldo}")
            print("✅ SISTEMA CONSISTENTE")
        else:
            print("    ⚠️  No hay transacciones pendientes")
            
    except Exception as e:
        print(f"❌ Error: {e}")
    finally:
        if conn_aqp:
            conn_aqp.close()

if __name__ == "__main__":
    print("⚠️  Asegúrate de haber restaurado los saldos iniciales")
    transferencia_con_caida_nodo()
    
    respuesta = input("\n¿Deseas recuperar la consistencia? (S/N): ")
    if respuesta.upper() == 'S':
        recuperar_despues_caida()