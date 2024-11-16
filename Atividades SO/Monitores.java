public class Monitores {
    private static int recurso = 0; 
    private int leitores = 0; 

    
    public synchronized void iniciarLeitura() {
        leitores++;
        System.out.println(Thread.currentThread().getName() + " está lendo. Leitores ativos: " + leitores);
    }

    public synchronized void terminarLeitura() {
        leitores--;
        System.out.println(Thread.currentThread().getName() + " terminou de ler. Leitores ativos: " + leitores);
    
        if (leitores == 0) {
            notifyAll();
        }
    }

    
    public synchronized void iniciarEscrita() {
        while (leitores > 0) {
            try {
                wait(); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() + " está escrevendo...");
        recurso++;
        System.out.println(Thread.currentThread().getName() + " terminou de escrever. Novo valor do recurso: " + recurso);
    }

    public static void main(String[] args) {
        Monitores controlador = new Monitores();

        
        Thread leitor1 = new Thread(() -> {
            controlador.iniciarLeitura();
            try {
                Thread.sleep(100); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            controlador.terminarLeitura();
        }, "Leitor 1");

        Thread leitor2 = new Thread(() -> {
            controlador.iniciarLeitura();
            try {
                Thread.sleep(100); 
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

        
        leitor1.start();
        leitor2.start();
        escritor1.start();
        escritor2.start();
    }
}

