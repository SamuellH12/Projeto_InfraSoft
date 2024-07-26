package Entrega_3;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BANHEIRO {

    public static class Banheiro {
        private ReentrantLock lock = new ReentrantLock();
        private Condition ocupado = lock.newCondition();
        int flag = 0;   // Flag de sinalização do banheiro: 0, Livre; X < 0, |X| homens no banheiro; X > 0, X mulheres no banheiro

        void entrar(int id, int genero) throws InterruptedException {
            lock.lock();
            try
            {
                // enquanto o banheiro estiver cheio ou com pessoas do outro gênero, a pessoa deve esperar
                while ((genero > 0 && flag < 0) || (genero < 0 && flag > 0) || Math.abs(flag) >= 3)
                    ocupado.await();

                flag += genero;

                System.out.println( (genero > 0 ? "Uma mulher" : "Um homem")  + " com id " + id + " ENTROU no banheiro.\n Agora há " + flag*genero + " pessoas no banheiro.\n" );
            }
            finally { lock.unlock(); }
        }

        void sair(int id, int genero) {
            lock.lock();
            try
            {
                flag -= genero;

                System.out.println( (genero > 0 ? "Uma mulher" : "Um homem")  + " com id " + id + " SAIU do banheiro.\n Agora há " + flag*genero + " pessoas no banheiro.\n" );

                ocupado.signalAll();
            }
            finally { lock.unlock(); }
        }

    }

    public static class Pessoa implements Runnable {
        private int tempo, id, genero;          // O Gênero é tal que: Se 1, Mulher, Se -1, Homem
        private Banheiro banheiro;

        Pessoa(int id, int tempo, int genero, Banheiro banheiro){
            this.id = id;
            this.tempo = tempo;
            this.genero = genero;
            this.banheiro = banheiro;
        }

        public void run()
        {
            try
            {
                banheiro.entrar(id, genero);
                Thread.sleep(tempo);                //simula o tempo que a pessoa passa no banheiro
            }
            catch (InterruptedException e) { throw new RuntimeException(e); }
            finally
            {
                banheiro.sair(id, genero);
            }
        }
    }

    public static void main(String[] args) {
        Banheiro banheiro = new Banheiro();

        int totalDePessoas = 100;
        Random random = new Random();

        for(int i=0; i<totalDePessoas; i++)
        {
            int tempo = random.nextInt(5, 21);
            int genero = (random.nextInt(0, 2) == 1 ?   1  : -1); // 1 -> Mulher // -1 -> Homem    //intervalo do random [0, 2)
            Pessoa p = new Pessoa( i, tempo, genero, banheiro);
            Thread t = new Thread(p);
            t.start();
        }
    }
}
