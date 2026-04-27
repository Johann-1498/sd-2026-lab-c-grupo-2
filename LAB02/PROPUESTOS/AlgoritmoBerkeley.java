import java.util.ArrayList;
import java.util.List;

class NodoBerkeley {
    private String nombre;
    private int tiempoLocal;

    public NodoBerkeley(String nombre, int tiempoInicial) {
        this.nombre = nombre;
        this.tiempoLocal = tiempoInicial;
    }

    public int getTiempo() {
        return tiempoLocal;
    }

    public String getNombre() {
        return nombre;
    }

    // Método para ajustar el reloj según la orden del servidor
    public void ajustarReloj(int ajuste) {
        this.tiempoLocal += ajuste;
        System.out.println(this.nombre + " ajustó su reloj en " + ajuste + " unidades. Nuevo tiempo local: " + this.tiempoLocal);
    }
}

public class AlgoritmoBerkeley {
    public static void main(String[] args) {
        System.out.println("--- INICIANDO ALGORITMO DE BERKELEY ---");
        
        // 1. Inicialización de los nodos con tiempos desfasados
        NodoBerkeley servidor = new NodoBerkeley("Servidor (Master)", 150);
        NodoBerkeley cliente1 = new NodoBerkeley("Cliente 1", 140);
        NodoBerkeley cliente2 = new NodoBerkeley("Cliente 2", 165);

        List<NodoBerkeley> red = new ArrayList<>();
        red.add(servidor);
        red.add(cliente1);
        red.add(cliente2);

        // 2. El servidor recopila los tiempos
        System.out.println("\n--- FASE 1: Recopilación de tiempos ---");
        int sumaTiempos = 0;
        for (NodoBerkeley nodo : red) {
            System.out.println(nodo.getNombre() + " reporta tiempo: " + nodo.getTiempo());
            sumaTiempos += nodo.getTiempo();
        }

        // 3. El servidor calcula el promedio
        int promedio = sumaTiempos / red.size();
        System.out.println("\n--- FASE 2: Cálculo del promedio ---");
        System.out.println("Tiempo promedio calculado por el Servidor: " + promedio);

        // 4. El servidor calcula y envía los ajustes (offsets)
        System.out.println("\n--- FASE 3: Aplicación de ajustes ---");
        for (NodoBerkeley nodo : red) {
            int ajuste = promedio - nodo.getTiempo();
            System.out.println("Enviando ajuste de " + ajuste + " a " + nodo.getNombre());
            nodo.ajustarReloj(ajuste);
        }
        
        System.out.println("\n--- SINCRONIZACIÓN COMPLETADA ---");
    }
}