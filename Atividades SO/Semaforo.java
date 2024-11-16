import java.util.concurrent.Semaphore;

public class Semaforo {
    private static int recurso = 0; 
    private int leitores = 0; 
    private final Semaphore mutex = new Semaphore(1); 
    private final Semaphore recursoSemaforo = new Semaphore(1); 

    
    public void iniciarLeitura() throws InterruptedException {
        mutex.acquire(); 
        leitores++;
        if (leitores == 1) {
            recursoSemaforo.acquire(); 
        }
        mutex.release(); 
        System.out.println(Thread.currentThread().getName() + " está lendo: " + recurso);
    }

    public void terminarLeitura() throws InterruptedException {
        mutex.acquire(); 
        leitores--;
        if (leitores == 0) {
            recursoSemaforo.release(); 
        }
        mutex.release(); 
        System.out.println(Thread.currentThread().getName() + " terminou de ler.");
    }

    
    public void iniciarEscrita() throws InterruptedException {
        recursoSemaforo.acquire(); 
        System.out.println(Thread.currentThread().getName() + " está escrevendo...");
        recurso++; 
        Thread.sleep(100); 
    }

    public void terminarEscrita() {
        System.out.println(Thread.currentThread().getName() + " terminou de escrever. Novo valor: " + recurso);
        recursoSemaforo.release(); 
    }

    public static void main(String[] args) {
        Semaforo controlador = new Semaforo();

        
        Thread leitor1 = new Thread(() -> {
            try {
                controlador.iniciarLeitura();
                Thread.sleep(50); 
                controlador.terminarLeitura();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Leitor 1");

        Thread leitor2 = new Thread(() -> {
            try {
                controlador.iniciarLeitura();
                Thread.sleep(50); 
                controlador.terminarLeitura();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Leitor 2");

        
        Thread escritor1 = new Thread(() -> {
            try {
                controlador.iniciarEscrita();
                Thread.sleep(100); 
                controlador.terminarEscrita();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Escritor 1");

        Thread escritor2 = new Thread(() -> {
            try {
                controlador.iniciarEscrita();
                Thread.sleep(100); 
                controlador.terminarEscrita();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Escritor 2");

        // Iniciando as threads
        leitor1.start();
        leitor2.start();
        escritor1.start();
        escritor2.start();
    }
}
