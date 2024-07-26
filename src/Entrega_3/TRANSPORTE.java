package Entrega_3;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TRANSPORTE {

    public static class Onibus implements Runnable {
        boolean rodando = true;
        boolean embarcando = false;
        boolean podeDesembarcar = false;
        int totalAguardando = 0;
        final int maximoDePassageiros = 50;

        Random random = new Random();

        Lock lock = new ReentrantLock();
        Semaphore capacidade = new Semaphore(maximoDePassageiros);
        Condition desembarque = lock.newCondition();
        Condition embarque = lock.newCondition();
        Condition guarda = lock.newCondition();


        void iniciarEmbarque()
        {
            lock.lock();

            System.out.println("\n\n### vvv INICIANDO EMBARQUE vvv ###");
            System.out.println("Passageiros no terminal: " + totalAguardando + "\n");

            embarcando = true;
            embarque.signalAll();   //alerta a quem estava esperando no terminal que o embarque começou

            lock.unlock();
        }

        void encerrarEmbarque()
        {
            lock.lock();

            // motivo do fim do embarque
            if(capacidade.availablePermits() == 0)
                System.out.println("\n>> Onibus cheio!!! <<");
            else if(totalAguardando == 0 && capacidade.availablePermits() < maximoDePassageiros)
                System.out.println("\n>> Terminal vazio!!! <<");
            else
                System.out.println(">> Terminal vazio e ninguem entrou no onibus <<");

            System.out.println("\nPassageiros no terminal: " + totalAguardando);
            System.out.println("### ^^^ FIM DO EMBARQUE ^^^ ###\n\n");

            embarcando = false;
            guarda.signalAll(); //alerta a quem estava tentando entrar no terminal que o embarque acabou
            lock.unlock();
        }

        void desembarcarPassageiros(){
            lock.lock();
            podeDesembarcar = true;
            lock.unlock();

            // Só prossegue quando todos os passageiros tiverem desembarcado
            while(capacidade.availablePermits() < maximoDePassageiros)
            {
                lock.lock();
                desembarque.signalAll();
                lock.unlock();
            }

            lock.lock();
            podeDesembarcar = false;
            lock.unlock();
        }

        void entrarNoTerminal(int id) throws InterruptedException
        {
            lock.lock();
            try
            {
                while(embarcando) guarda.await();   //aguarda enquando o ônibus estiver embarcando outros passageiros (pois não pode entrar no terminal durante o embarque)
                totalAguardando++;
                System.out.println("* Passageiro com id " + id + " entrou no terminal");
            }
            finally { lock.unlock(); }
        }

        void entrarNoOnibus(int id) throws InterruptedException
        {
            boolean embarcou = false;

            while(!embarcou)    //tenta embarcar enquanto não estiver no ônibus
            {
                lock.lock();

                while(!embarcando) embarque.await();    //se o embarque ainda não começou, espera

                embarcou = capacidade.tryAcquire();     //tenta embarcar

                if(embarcou)
                {
                    System.out.println("\t> Passageiro com id " +  id + " entrou no onibus");
                    totalAguardando--;
                }

                lock.unlock();
            }
        }

        void sairDoOnibus() throws InterruptedException {
            lock.lock();

            while(!podeDesembarcar) desembarque.await();    //só sai quando puder sair, se não aguarda

            capacidade.release();

            lock.unlock();
        }

        void esperarViagem() {
            int tempoDeViagem = random.nextInt(1000, 3001);
            try
            {
                Thread.sleep(tempoDeViagem);    //aguarda de 1 a 3 segundos para simular a viagem de onibus
            }
            catch (InterruptedException e) {}
        }

        public void pararDeRodar(){ this.rodando = false; }


        public void run()
        {
            while(rodando)
            {
                iniciarEmbarque();

                boolean passageirosEmbarcando = true;
                while(passageirosEmbarcando)
                {
                    lock.lock();
                    passageirosEmbarcando = totalAguardando != 0 && capacidade.availablePermits() > 0;
                    lock.unlock();
                }
                encerrarEmbarque();
                esperarViagem();
                desembarcarPassageiros();
            }
        }
    }


    public static class Passageiro implements Runnable {
        private int tempoAteIrAoTerminal, id;
        Onibus onibus;

        Passageiro(int id, int tempo, Onibus onibus){
            this.id = id;
            this.tempoAteIrAoTerminal = tempo;
            this.onibus = onibus;
        }

        public void run()
        {
            try
            {
                Thread.sleep(tempoAteIrAoTerminal);
                onibus.entrarNoTerminal(id);
                onibus.entrarNoOnibus(id);
                onibus.sairDoOnibus();
            }
            catch (InterruptedException e) { throw new RuntimeException(e); }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Onibus onibus = new Onibus();
        Thread tOnibus = new Thread(onibus);

        int totalDePassageiros = 175;
        ArrayList<Thread> tPassageiros = new ArrayList<>(0);

        Random random = new Random();

        for(int i=0; i<totalDePassageiros; i++)
        {
            int tempo = random.nextInt(10001);
            Passageiro p = new Passageiro(i, tempo, onibus);
            Thread t = new Thread(p);
            tPassageiros.add(t);
            t.start();
        }

        tOnibus.start();

        for(int i=0; i<totalDePassageiros; i++)
            tPassageiros.get(i).join();

        onibus.pararDeRodar();
    }
}
