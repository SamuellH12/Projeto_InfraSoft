package Entrega_1;
import java.util.concurrent.locks.ReentrantLock;

public class Ponte {

    public static class PonteControl {
        public boolean lado = false;
        private ReentrantLock lock = new ReentrantLock();

        public void lado() {}

        public boolean atravessar(boolean lado, int id){
            lock.lock();
            this.lado = lado;
            System.out.println("Carro "+ id + " atravessando da " + (lado ? "esquerda" : "direita") );
            return true;
        }

        public void sair(){
            System.out.println("Carro saiu. Ponte vazia");
            lock.unlock();
        }
    }

    public static class Carro implements Runnable{
        private boolean lado;
        private int tempo, id;
        private PonteControl control;

        Carro(boolean lado, int tempo, int id, PonteControl control){
            this.lado = lado;
            this.tempo = tempo;
            this.control = control;
            this.id = id;
        }

        public void run(){
            boolean got = false;

            try
            {
                got = this.control.atravessar(this.lado, this.id);
                Thread.sleep(tempo);
            }
            catch (InterruptedException e)
            {
                if(got) this.control.sair();
                got = false;
                throw new RuntimeException(e);
            }

            if(got) this.control.sair();
        }
    }

    public static void main(String[] args) throws InterruptedException {

        PonteControl control = new PonteControl();

        Carro c1 = new Carro(true,  1050, 1, control);
        Carro c2 = new Carro(false, 1250, 2, control);
        Carro c3 = new Carro(true,  1020, 3, control);
        Carro c4 = new Carro(false, 2100, 4, control);
        Carro c5 = new Carro(true,  1350, 5, control);
        Carro c6 = new Carro(false, 1450, 6, control);

        Thread t1 = new Thread(c1);
        Thread t2 = new Thread(c2);
        Thread t3 = new Thread(c3);
        Thread t4 = new Thread(c4);
        Thread t5 = new Thread(c5);
        Thread t6 = new Thread(c6);

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();


        t1.join();
        t2.join();
        t3.join();
        t4.join();
        t5.join();
        t6.join();

        System.out.println("Encerrando...");
    }
}
