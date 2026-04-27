public class CubbyHole{ 
    private int contents; // 
    private boolean available = false; //si es que hay un dato disponible
    public synchronized int get() {
        while (available == false) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // restaurar flag de interrupción
            }
        }
        available = false;
        notifyAll();
        return contents;
    }
    public synchronized void put(int value) {
        while (available == true) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // restaurar flag de interrupción
            }
        }
        contents = value;
        available = true;
        notifyAll();
    }
}
