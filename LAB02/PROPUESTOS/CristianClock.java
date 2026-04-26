import java.util.ArrayList;
import java.util.List;

public class CristianClock {
    public long getServerTime() {
        return System.currentTimeMillis();
    }
    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        CristianClock centralServer = new CristianClock();
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        long t1 = System.currentTimeMillis();                                 
                        Thread.sleep((long) (100 + Math.random() * 400));
                        long serverTime = centralServer.getServerTime();
                        Thread.sleep((long) (100 + Math.random() * 400));
                        long t2 = System.currentTimeMillis();
                        long rtt = t2 - t1;
                        long adjustedTime = serverTime + (rtt / 2); 
                        System.out.println("Hilo " + Thread.currentThread().getId() + " | RTT: " + rtt + "ms" +" | Hora Servidor: " + serverTime + " | Hora Ajustada: " + adjustedTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}