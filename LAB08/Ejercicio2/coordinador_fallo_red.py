import psycopg2
import time
import sys

def transferencia_con_fallo_red():
    print("="*60)
    print("SIMULACIÓN: FALLA DE RED DURANTE 2PC")
    print("="*60)
    
    conn_aqp = None
    conn_cus = None
    
    try:
        # Conexiones a los nodos
        conn_aqp = psycopg2.connect(
            dbname="banco_arequipa", user="postgres", password="admin", host="127.0.0.1", port="6432"
        )
        conn_cus = psycopg2.connect(
            dbname="banco_cusco", user="postgres", password="admin", host="127.0.0.1", port="6433"
        )
        
        # IMPORTANTE: Auto-commit OFF para manejar transacciones manualmente
        conn_aqp.autocommit = False
        conn_cus.autocommit = False
        
        cur_aqp = conn_aqp.cursor()
        cur_cus = conn_cus.cursor()
        
        trx_id = 'trx_fallo_red_25k'
        monto = 25000

        print("\n[1] Ejecutando operaciones locales...")
        cur_aqp.execute(f"UPDATE cuenta SET saldo = saldo - {monto} WHERE id = 1;")
        cur_cus.execute(f"UPDATE cuenta SET saldo = saldo + {monto} WHERE id = 1;")
        print("    - Arequipa: Débito de S/ 25,000")
        print("    - Cusco: Crédito de S/ 25,000")

        # FASE 1: PREPARE
        print("\n[2] FASE 1 - PREPARE TRANSACTION...")
        cur_aqp.execute(f"PREPARE TRANSACTION '{trx_id}';")
        cur_cus.execute(f"PREPARE TRANSACTION '{trx_id}';")
        print("     Ambos nodos respondieron 'READY'")
        print("     Transacciones en estado PREPARED en ambos nodos")

        # 🔴🔴🔴 PUNTO DE FALLO SIMULADO 🔴🔴🔴
        print("\n" + "="*60)
        print(" SIMULANDO FALLA DE RED")
        print("="*60)
        print("El nodo Cusco ha dejado de responder (simular desconexión de red)")
        print("El coordinador no puede completar la Fase 2")
        input("\n⚠️  Presiona ENTER para simular que el coordinador INTENTA COMMIT...")
        
        # Intentar FASE 2: COMMIT (esto fallará si Cusco no responde)
        print("\n[3] FASE 2 - Intentando COMMIT PREPARED...")
        
        # Intentar COMMIT en Arequipa (éxito)
        cur_aqp.execute(f"COMMIT PREPARED '{trx_id}';")
        conn_aqp.commit()
        print("    ✅ Arequipa: COMMIT exitoso")
        
        # Intentar COMMIT en Cusco (fallará - simula nodo inalcanzable)
        cur_cus.execute(f"COMMIT PREPARED '{trx_id}';")
        conn_cus.commit()
        print("    ✅ Cusco: COMMIT exitoso")
        
        print("\n🎉 ¡TRANSFERENCIA COMPLETADA!")

    except psycopg2.OperationalError as e:
        print(f"\n    ❌ ERROR DE COMUNICACIÓN: {e}")
        print("\n" + "="*60)
        print(" ESTADO DEL SISTEMA DESPUÉS DEL FALLO:")
        print("="*60)
        print("🔸 Arequipa: Transacción CONFIRMADA (COMMIT realizado)")
        print("🔸 Cusco: Transacción en estado PREPARED (sin COMMIT)")
        print("\n⚠️  INCONSISTENCIA DETECTADA:")
        print("   - Los S/ 25,000 fueron debitados de Arequipa")
        print("   - No fueron acreditados en Cusco")
        
        # Verificar transacciones PREPARED
        print("\n Transacciones PREPARED pendientes:")
        try:
            if conn_aqp:
                cur_temp = conn_aqp.cursor()
                cur_temp.execute("SELECT gid, prepared FROM pg_prepared_xacts;")
                prepared = cur_temp.fetchall()
                if prepared:
                    for p in prepared:
                        print(f"   - {p[0]} (preparada en: {p[1]})")
                else:
                    print("   - No hay transacciones PREPARED")
        except:
            pass
            
    except Exception as e:
        print(f"\n❌ Error inesperado: {e}")
        import traceback
        traceback.print_exc()
        
    finally:
        # Cerrar conexiones
        if conn_aqp:
            conn_aqp.close()
        if conn_cus:
            conn_cus.close()
        print("\n" + "="*60)

def recuperar_transaccion():
    """Función para recuperar la transacción después del fallo"""
    print("\n" + "="*60)
    print("RECUPERACIÓN DE TRANSACCIÓN")
    print("="*60)
    
    try:
        conn_cus = psycopg2.connect(
            dbname="banco_cusco", user="postgres", password="admin", host="127.0.0.1", port="6433"
        )
        conn_cus.autocommit = True
        cur = conn_cus.cursor()
        
        trx_id = 'trx_fallo_red_25k'
        
        # Verificar si existe la transacción PREPARED
        cur.execute("SELECT gid FROM pg_prepared_xacts WHERE gid = %s;", (trx_id,))
        if cur.fetchone():
            print(f"✅ Transacción '{trx_id}' encontrada en estado PREPARED")
            respuesta = input("¿Deseas hacer COMMIT o ROLLBACK? (C/R): ")
            if respuesta.upper() == 'C':
                cur.execute(f"COMMIT PREPARED '{trx_id}';")
                print("✅ COMMIT ejecutado - Fondos acreditados en Cusco")
            else:
                cur.execute(f"ROLLBACK PREPARED '{trx_id}';")
                print("✅ ROLLBACK ejecutado - Transacción cancelada")
        else:
            print("⚠️  No se encontró la transacción PREPARED")
            
    except Exception as e:
        print(f"❌ Error en recuperación: {e}")
    finally:
        if conn_cus:
            conn_cus.close()

if __name__ == "__main__":
    transferencia_con_fallo_red()
    
    # Preguntar si quiere recuperar
    respuesta = input("\n¿Deseas recuperar la transacción? (S/N): ")
    if respuesta.upper() == 'S':
        recuperar_transaccion()