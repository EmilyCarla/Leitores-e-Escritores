import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Barreiras {
    private static final Lock lock = new ReentrantLock();
    private static final CyclicBarrier barreiraLeitores = new CyclicBarrier(3); 
    private static int leitoresAtivos = 0;
    private static int recursoCompartilhado = 0; 

   
    static class Leitor implements Runnable {
        private final String nome;

        public Leitor(String nome) {
            this.nome = nome;
        }

        @Override
        public void run() {
            try {
                entrarLeitura();
                System.out.println(nome + " está lendo o valor: " + recursoCompartilhado);
                sairLeitura();
                barreiraLeitores.await(); 
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void entrarLeitura() {
            lock.lock();
            try {
                leitoresAtivos++;
            } finally {
                lock.unlock();
            }
        }

        private void sairLeitura() {
            lock.lock();
            try {
                leitoresAtivos--;
                if (leitoresAtivos == 0) {
                    synchronized (lock) {
                        lock.notifyAll(); 
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    }

    
    static class Escritor implements Runnable {
        private final String nome;

        public Escritor(String nome) {
            this.nome = nome;
        }

        @Override
        public void run() {
            try {
                lock.lock();
                while (leitoresAtivos > 0) {
                    synchronized (lock) {
                        lock.wait(); 
                    }
                }
                
                recursoCompartilhado++;
                System.out.println(nome + " escreveu o valor: " + recursoCompartilhado);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        Thread leitor1 = new Thread(new Leitor("Leitor 1"));
        Thread leitor2 = new Thread(new Leitor("Leitor 2"));
        Thread leitor3 = new Thread(new Leitor("Leitor 3"));
        Thread escritor1 = new Thread(new Escritor("Escritor 1"));

        // Inicia as threads
        leitor1.start();
        leitor2.start();
        leitor3.start();

        try {
            // Espera um pouco antes de iniciar o escritor para dar tempo aos leitores
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        escritor1.start();
    }
}
