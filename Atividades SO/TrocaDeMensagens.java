import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class TrocaDeMensagens {

    
    enum TipoOperacao {
        LEITURA, ESCRITA
    }

    
    static class Operacao {
        final TipoOperacao tipo;
        final String mensagem;

        public Operacao(TipoOperacao tipo, String mensagem) {
            this.tipo = tipo;
            this.mensagem = mensagem;
        }
    }

   
    static class RecursoCompartilhado {
        private final AtomicInteger valor = new AtomicInteger(0);

        public void ler() {
            System.out.println(Thread.currentThread().getName() + " lendo valor: " + valor.get());
        }

        public void escrever(int novoValor) {
            valor.set(novoValor);
            System.out.println(Thread.currentThread().getName() + " escreveu novo valor: " + valor.get());
        }
    }

    
    static class ProcessadorOperacoes implements Runnable {
        private final RecursoCompartilhado recurso;
        private final BlockingQueue<Operacao> filaOperacoes;
        private final Object lock = new Object();
        private int leitoresAtivos = 0;

        public ProcessadorOperacoes(RecursoCompartilhado recurso, BlockingQueue<Operacao> filaOperacoes) {
            this.recurso = recurso;
            this.filaOperacoes = filaOperacoes;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Operacao operacao = filaOperacoes.take(); 

                    if (operacao.tipo == TipoOperacao.LEITURA) {
                        synchronized (lock) {
                            leitoresAtivos++;
                        }
                        
                        recurso.ler();
                        synchronized (lock) {
                            leitoresAtivos--;
                            if (leitoresAtivos == 0) {
                                lock.notifyAll(); 
                            }
                        }
                    } else if (operacao.tipo == TipoOperacao.ESCRITA) {
                        synchronized (lock) {
                            while (leitoresAtivos > 0) {
                                lock.wait(); 
                            }
                        }
                        // Processa a escrita
                        recurso.escrever((int) (Math.random() * 100));
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Processador de Operações interrompido");
            }
        }
    }

    public static void main(String[] args) {
        RecursoCompartilhado recurso = new RecursoCompartilhado();
        BlockingQueue<Operacao> filaOperacoes = new LinkedBlockingQueue<>();

        
        Thread processador = new Thread(new ProcessadorOperacoes(recurso, filaOperacoes));
        processador.start();

       
        Runnable geradorLeitura = () -> {
            for (int i = 0; i < 10; i++) {
                try {
                    filaOperacoes.put(new Operacao(TipoOperacao.LEITURA, "Leitura " + i));
                    Thread.sleep(100); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        };

        
        Runnable geradorEscrita = () -> {
            for (int i = 0; i < 5; i++) {
                try {
                    filaOperacoes.put(new Operacao(TipoOperacao.ESCRITA, "Escrita " + i));
                    Thread.sleep(200); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        };

        // Inicia threads geradoras de leitura e escrita
        Thread leitor1 = new Thread(geradorLeitura, "Leitor 1");
        Thread leitor2 = new Thread(geradorLeitura, "Leitor 2");
        Thread escritor1 = new Thread(geradorEscrita, "Escritor 1");

        leitor1.start();
        leitor2.start();
        escritor1.start();
    }
}
