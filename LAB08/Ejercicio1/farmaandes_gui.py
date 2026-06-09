import tkinter as tk
from tkinter import messagebox
import psycopg2

# ─── Design Tokens ────────────────────────────────────────────────────────────
BG_BASE    = "#0A0E17"
BG_CARD    = "#111827"
BG_SURFACE = "#0D1321"
BG_INPUT   = "#1A2233"
BORDER     = "#1F2D45"
BORDER_SUB = "#162030"

ACCENT_AQP = "#FF4757"
ACCENT_LIM = "#1E90FF"
SUCCESS    = "#00C853"
DANGER     = "#FF3B30"
WARNING    = "#FF9F0A"
ONLINE     = "#34C759"

TEXT_PRI   = "#F0F4FF"
TEXT_SEC   = "#5B7194"
TEXT_MUT   = "#3A4E6A"

LOG_GREEN  = "#00E676"
LOG_RED    = "#FF5252"
LOG_BLUE   = "#40C4FF"
LOG_MUTED  = "#4A6080"
LOG_BG     = "#07090F"

STOCK_MAX_AQP  = 100
STOCK_MAX_LIMA = 50
# ──────────────────────────────────────────────────────────────────────────────


def hex_to_rgb(h):
    h = h.lstrip("#")
    return tuple(int(h[i:i+2], 16) for i in (0, 2, 4))

def blend(c1, c2, t):
    r1,g1,b1 = hex_to_rgb(c1)
    r2,g2,b2 = hex_to_rgb(c2)
    return "#{:02x}{:02x}{:02x}".format(
        int(r1 + (r2-r1)*t),
        int(g1 + (g2-g1)*t),
        int(b1 + (b2-b1)*t),
    )


class StockBar(tk.Canvas):
    """Mini barra de progreso de stock."""
    def __init__(self, parent, color, **kw):
        super().__init__(parent, height=4, bg=BG_CARD,
                         highlightthickness=0, bd=0, **kw)
        self.color = color
        self._pct = 0

    def set_pct(self, pct):
        self._pct = max(0.0, min(1.0, pct))
        self._draw()

    def _draw(self):
        self.delete("all")
        w = self.winfo_width() or 200
        # track
        self.create_rectangle(0, 0, w, 4, fill=BORDER_SUB, outline="")
        # fill
        fill_w = int(w * self._pct)
        if fill_w > 0:
            # gradient-ish: blend color toward red when low
            c = blend(DANGER, self.color, self._pct) if self._pct < 0.3 else self.color
            self.create_rectangle(0, 0, fill_w, 4, fill=c, outline="")
        # glow cap
        if fill_w > 2:
            self.create_rectangle(fill_w-2, 0, fill_w, 4,
                                  fill=blend(self.color, "#FFFFFF", 0.5), outline="")


class NodeCard(tk.Frame):
    """Tarjeta de nodo con stock, barra de capacidad e indicador de estado."""
    def __init__(self, parent, city, code, accent, max_stock, **kw):
        super().__init__(parent, bg=BG_CARD, **kw)
        self.accent    = accent
        self.max_stock = max_stock

        # Borde superior de color
        tk.Frame(self, bg=accent, height=3).pack(fill="x")

        body = tk.Frame(self, bg=BG_CARD)
        body.pack(fill="both", padx=16, pady=14)

        # Fila superior: code badge + status dot
        top = tk.Frame(body, bg=BG_CARD)
        top.pack(fill="x")

        tk.Label(top, text=code, font=("Segoe UI", 8, "bold"),
                 bg=BG_CARD, fg=accent).pack(side="left")

        self.dot = tk.Label(top, text="● ONLINE", font=("Segoe UI", 7, "bold"),
                            bg=BG_CARD, fg=ONLINE)
        self.dot.pack(side="right")

        # Ciudad
        tk.Label(body, text=city, font=("Segoe UI", 13, "bold"),
                 bg=BG_CARD, fg=TEXT_PRI).pack(anchor="w", pady=(4, 0))

        # Número de stock
        self.lbl_stock = tk.Label(body, text="—",
                                  font=("Segoe UI", 32, "bold"),
                                  bg=BG_CARD, fg=TEXT_PRI)
        self.lbl_stock.pack(anchor="w")

        # Subtítulo + capacidad
        bottom = tk.Frame(body, bg=BG_CARD)
        bottom.pack(fill="x", pady=(2, 0))

        self.lbl_sub = tk.Label(bottom, text="— / — uds.",
                                font=("Segoe UI", 8),
                                bg=BG_CARD, fg=TEXT_SEC)
        self.lbl_sub.pack(side="left")

        self.lbl_pct = tk.Label(bottom, text="",
                                font=("Segoe UI", 8, "bold"),
                                bg=BG_CARD, fg=TEXT_SEC)
        self.lbl_pct.pack(side="right")

        # Barra de stock
        self.bar = StockBar(body, accent, width=10)
        self.bar.pack(fill="x", pady=(8, 0))
        self.bar.bind("<Configure>", lambda e: self.bar._draw())

    def update_stock(self, value):
        is_error = isinstance(value, str)
        if is_error:
            self.lbl_stock.config(text="—", fg=TEXT_SEC)
            self.lbl_sub.config(text="Sin conexión")
            self.lbl_pct.config(text="")
            self.dot.config(text="● OFFLINE", fg=DANGER)
            self.bar.set_pct(0)
        else:
            pct = value / self.max_stock if self.max_stock else 0
            color = blend(DANGER, self.accent, pct) if pct < 0.3 else TEXT_PRI
            self.lbl_stock.config(text=str(value), fg=color)
            self.lbl_sub.config(text=f"{value} / {self.max_stock} uds.")
            self.lbl_pct.config(text=f"{int(pct*100)}%")
            self.dot.config(text="● ONLINE", fg=ONLINE)
            self.bar.set_pct(pct)


class ActionButton(tk.Frame):
    """Botón de acción con ícono, título y descripción secundaria."""
    def __init__(self, parent, icon, title, subtitle, command, accent, **kw):
        super().__init__(parent, bg=BG_BASE, cursor="hand2", **kw)
        self.accent  = accent
        self.command = command
        self._pressed = False

        # Contenedor con borde izquierdo
        self.inner = tk.Frame(self, bg=BG_CARD)
        self.inner.pack(fill="x")

        # Borde izquierdo de acento
        tk.Frame(self.inner, bg=accent, width=3).pack(side="left", fill="y")

        content = tk.Frame(self.inner, bg=BG_CARD)
        content.pack(side="left", fill="both", expand=True, padx=14, pady=10)

        left = tk.Frame(content, bg=BG_CARD)
        left.pack(side="left", fill="both", expand=True)

        self.lbl_title = tk.Label(left, text=f"{icon}  {title}",
                                  font=("Segoe UI", 10, "bold"),
                                  bg=BG_CARD, fg=TEXT_PRI, anchor="w")
        self.lbl_title.pack(anchor="w")

        tk.Label(left, text=subtitle, font=("Segoe UI", 8),
                 bg=BG_CARD, fg=TEXT_SEC, anchor="w").pack(anchor="w")

        # Flecha derecha
        tk.Label(self.inner, text="›", font=("Segoe UI", 16),
                 bg=BG_CARD, fg=TEXT_MUT).pack(side="right", padx=14)

        # Bindings
        for w in [self, self.inner, content, left, self.lbl_title]:
            w.bind("<Button-1>",   self._on_click)
            w.bind("<ButtonRelease-1>", self._on_release)
            w.bind("<Enter>",      self._on_enter)
            w.bind("<Leave>",      self._on_leave)

    def _set_bg(self, color):
        for w in self.inner.winfo_children():
            try: w.config(bg=color)
            except: pass
            for ww in w.winfo_children():
                try: ww.config(bg=color)
                except: pass
        self.inner.config(bg=color)

    def _on_enter(self, e):
        if not self._pressed:
            self._set_bg(blend(BG_CARD, self.accent, 0.12))

    def _on_leave(self, e):
        self._set_bg(BG_CARD)

    def _on_click(self, e):
        self._pressed = True
        self._set_bg(blend(BG_CARD, self.accent, 0.25))

    def _on_release(self, e):
        self._pressed = False
        self._set_bg(blend(BG_CARD, self.accent, 0.12))
        self.command()


class FarmaAndesApp:
    def __init__(self, root):
        self.root = root
        self.root.title("FarmaAndes S.A. — Gestor de Transacciones")
        self.root.geometry("520x800")
        self.root.configure(bg=BG_BASE)
        self.root.resizable(False, False)
        self._build_ui()
        self.actualizar_pantalla()

    # ── UI ────────────────────────────────────────────────────────────────────

    def _build_ui(self):
        # ── Header ──
        header = tk.Frame(self.root, bg=BG_BASE)
        header.pack(fill="x", padx=24, pady=(28, 0))

        tk.Label(header, text="FarmaAndes S.A.",
                 font=("Segoe UI", 22, "bold"),
                 bg=BG_BASE, fg=TEXT_PRI).pack(anchor="w")

        tk.Label(header, text="Gestor de Transacciones Distribuidas",
                 font=("Segoe UI", 9), bg=BG_BASE, fg=TEXT_SEC).pack(anchor="w")

        # Divider
        self._divider()

        # ── Nodos ──
        self._section_label("NODOS")

        nodes_frame = tk.Frame(self.root, bg=BG_BASE)
        nodes_frame.pack(padx=24, fill="x")
        nodes_frame.columnconfigure(0, weight=1, uniform="col")
        nodes_frame.columnconfigure(1, weight=1, uniform="col")

        self.card_aqp = NodeCard(nodes_frame, "Arequipa", "AQP",
                                 ACCENT_AQP, STOCK_MAX_AQP)
        self.card_aqp.grid(row=0, column=0, sticky="nsew", padx=(0, 6))

        self.card_lima = NodeCard(nodes_frame, "Lima", "LIM",
                                  ACCENT_LIM, STOCK_MAX_LIMA)
        self.card_lima.grid(row=0, column=1, sticky="nsew", padx=(6, 0))

        # ── Acciones ──
        self._divider()
        self._section_label("ACCIONES")

        btn_frame = tk.Frame(self.root, bg=BG_BASE)
        btn_frame.pack(padx=24, fill="x")

        ActionButton(
            btn_frame,
            icon="▶",
            title="Transferir 20 unidades",
            subtitle="Ejercicio 1 — Transacción exitosa entre nodos",
            command=self.transferencia_exitosa,
            accent=SUCCESS,
        ).pack(fill="x", pady=(0, 8))

        ActionButton(
            btn_frame,
            icon="⚡",
            title="Simular caída de Lima",
            subtitle="Ejercicio 2 — Rollback por timeout de nodo remoto",
            command=self.simular_fallo,
            accent=DANGER,
        ).pack(fill="x", pady=(0, 8))

        ActionButton(
            btn_frame,
            icon="↺",
            title="Resetear valores",
            subtitle="Restaurar stock inicial  ·  AQP: 100  ·  LIMA: 50",
            command=self.resetear_bd,
            accent=WARNING,
        ).pack(fill="x")

        # ── Consola ──
        self._divider()
        self._section_label("CONSOLA DE TRANSACCIONES")

        outer = tk.Frame(self.root, bg=BORDER, bd=0)
        outer.pack(padx=24, fill="both", expand=True, pady=(0, 24))

        console_wrap = tk.Frame(outer, bg=LOG_BG)
        console_wrap.pack(fill="both", expand=True, padx=1, pady=1)

        # Placeholder
        self.placeholder = tk.Label(
            console_wrap,
            text="Esperando transacciones…",
            font=("Segoe UI", 9),
            bg=LOG_BG, fg=TEXT_MUT,
        )
        self.placeholder.place(relx=0.5, rely=0.5, anchor="center")

        self.txt_log = tk.Text(
            console_wrap,
            font=("Cascadia Code", 9),
            bg=LOG_BG, fg=LOG_GREEN,
            insertbackground=TEXT_PRI,
            relief="flat", bd=10,
            wrap="word", state="normal",
        )
        self.txt_log.pack(fill="both", expand=True)

        self.txt_log.tag_config("info",    foreground=LOG_GREEN)
        self.txt_log.tag_config("error",   foreground=LOG_RED)
        self.txt_log.tag_config("section", foreground=LOG_BLUE)
        self.txt_log.tag_config("muted",   foreground=LOG_MUTED)

    def _divider(self):
        tk.Frame(self.root, bg=BORDER, height=1).pack(fill="x", padx=24, pady=14)

    def _section_label(self, text):
        # Simular letter-spacing insertando espacio entre caracteres
        spaced = " ".join(text)
        tk.Label(self.root, text=spaced,
                 font=("Segoe UI", 7, "bold"),
                 bg=BG_BASE, fg=TEXT_SEC).pack(anchor="w", padx=24, pady=(0, 10))

    # ── helpers ───────────────────────────────────────────────────────────────

    def log(self, mensaje, tag="info"):
        # Ocultar placeholder al primer mensaje
        self.placeholder.place_forget()
        self.txt_log.insert(tk.END, mensaje + "\n", tag)
        self.txt_log.see(tk.END)

    def obtener_stock(self, dbname):
        try:
            conn = psycopg2.connect(
                dbname=dbname, user="postgres", password="admin",
                host="localhost", port="5432"
            )
            cur = conn.cursor()
            cur.execute("SELECT stock FROM inventario WHERE producto = 'Paracetamol'")
            stock = cur.fetchone()[0]
            conn.close()
            return stock
        except Exception:
            return "—"

    def actualizar_pantalla(self):
        aqp  = self.obtener_stock("almacen_arequipa")
        lima = self.obtener_stock("almacen_lima")
        self.card_aqp.update_stock(aqp)
        self.card_lima.update_stock(lima)

    # ── lógica de negocio (idéntica al original) ──────────────────────────────

    def resetear_bd(self):
        try:
            conn_aqp  = psycopg2.connect(dbname="almacen_arequipa",  user="postgres", password="admin", host="localhost", port="5432")
            conn_lima = psycopg2.connect(dbname="almacen_lima", user="postgres", password="admin", host="localhost", port="5432")
            conn_aqp.cursor().execute("UPDATE inventario SET stock = 100 WHERE producto = 'Paracetamol'")
            conn_lima.cursor().execute("UPDATE inventario SET stock = 50  WHERE producto = 'Paracetamol'")
            conn_aqp.commit(); conn_lima.commit()
            conn_aqp.close();  conn_lima.close()
            self.txt_log.delete(1.0, tk.END)
            self.placeholder.place(relx=0.5, rely=0.5, anchor="center")
            self.actualizar_pantalla()
        except Exception as e:
            messagebox.showerror("Error", f"Asegúrate de haber ejecutado setup_bd.py primero.\n{e}")

    def transferencia_exitosa(self):
        self.log("\n─── Transacción Exitosa " + "─"*20, "section")
        try:
            conn_aqp  = psycopg2.connect(dbname="almacen_arequipa",  user="postgres", password="admin", host="localhost", port="5432")
            conn_lima = psycopg2.connect(dbname="almacen_lima", user="postgres", password="admin", host="localhost", port="5432")
            cur_aqp  = conn_aqp.cursor()
            cur_lima = conn_lima.cursor()

            self.log("  ↓ Descontando 20 de Arequipa…", "muted")
            cur_aqp.execute("UPDATE inventario SET stock = stock - 20 WHERE producto = 'Paracetamol'")

            self.log("  ↑ Incrementando 20 en Lima…", "muted")
            cur_lima.execute("UPDATE inventario SET stock = stock + 20 WHERE producto = 'Paracetamol'")

            self.log("  ✔ COMMIT — cambios confirmados", "muted")
            conn_aqp.commit(); conn_lima.commit()
            self.log("✔ Transacción completada con éxito.", "info")

        except Exception as e:
            if 'conn_aqp'  in locals(): conn_aqp.rollback()
            if 'conn_lima' in locals(): conn_lima.rollback()
            self.log(f"✕ Error: {e}", "error")
        finally:
            if 'conn_aqp'  in locals(): conn_aqp.close()
            if 'conn_lima' in locals(): conn_lima.close()
            self.actualizar_pantalla()

    def simular_fallo(self):
        self.log("\n─── Simulación de Fallo " + "─"*21, "section")
        try:
            conn_aqp  = psycopg2.connect(dbname="almacen_arequipa",  user="postgres", password="admin", host="localhost", port="5432")
            conn_lima = psycopg2.connect(dbname="almacen_lima", user="postgres", password="admin", host="localhost", port="5432")
            cur_aqp  = conn_aqp.cursor()

            self.log("  ↓ Descontando 20 de Arequipa…", "muted")
            cur_aqp.execute("UPDATE inventario SET stock = stock - 20 WHERE producto = 'Paracetamol'")

            self.log("  … Intentando contactar a Lima…", "muted")
            raise Exception("TIMEOUT: Nodo Lima desconectado.")

        except Exception as e:
            self.log(f"✕ {e}", "error")
            self.log("  ↺ Ejecutando ROLLBACK en todos los nodos…", "muted")
            if 'conn_aqp'  in locals(): conn_aqp.rollback()
            if 'conn_lima' in locals(): conn_lima.rollback()
            self.log("↺ Rollback completado. Estado intacto.", "muted")
        finally:
            if 'conn_aqp'  in locals(): conn_aqp.close()
            if 'conn_lima' in locals(): conn_lima.close()
            self.actualizar_pantalla()


if __name__ == "__main__":
    root = tk.Tk()
    app = FarmaAndesApp(root)
    root.mainloop()