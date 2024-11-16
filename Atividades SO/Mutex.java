import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class Mutex {
    private static int recurso = 0; 
    private int leitores = 0; // 
    private final Lock lock = new ReentrantLock();
    private final Condition recursoDisponivel = lock.newCondition(); 

    
    public void iniciarLeitura() {
        lock.lock();
        try {
            leitores++;
            if (leitores == 1) {
                
                recursoDisponivel.await();
            }
            System.out.println(Thread.currentThread().getName() + " está lendo: " + recurso);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void terminarLeitura() {
        lock.lock();
        try {
            leitores--;
            if (leitores == 0) {
                
                recursoDisponivel.signal(); 
            }
            System.out.println(Thread.currentThread().getName() + " terminou de ler.");
        } finally {
            lock.unlock();
        }
    }

    
    public void iniciarEscrita() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " está escrevendo...");
            recurso++;
            Thread.sleep(100); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println(Thread.currentThread().getName() + " terminou de escrever. Novo valor: " + recurso);
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        Mutex controlador = new Mutex();

        
        Thread leitor1 = new Thread(() -> {
            controlador.iniciarLeitura();
            try {
                Thread.sleep(50); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            controlador.terminarLeitura();
        }, "Leitor 1");

        Thread leitor2 = new Thread(() -> {
            controlador.iniciarLeitura();
            try {
                Thread.sleep(50); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            controlador.terminarLeitura();
        }, "Leitor 2");

        
        Thread escritor1 = new Thread(() -> {
            controlador.iniciarEscrita();
        }, "Escritor 1");

        Thread escritor2 = new Thread(() -> {
            controlador.iniciarEscrita();
        }, "Escritor 2");

        // Iniciando as threads
        leitor1.start();
        leitor2.start();
        escritor1.start();
        escritor2.start();
    }
}

