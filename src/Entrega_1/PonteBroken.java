package Entrega_1;

import java.util.concurrent.locks.ReentrantLock;

// Versão da Ponte SEM controle de fluxo
public class PonteBroken {

    public static class PonteControl {
        public boolean lado = false;

        public void lado() {}

        public boolean atravessar(boolean lado, int id){
            this.lado = lado;
            System.out.println("Carro "+ id + " atravessando da " + (lado ? "esquerda" : "direita") );
            return true;
        }

        public void sair(int id){
            System.out.println("Carro "+id+" saiu");
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
                if(got) this.control.sair(this.id);
                got = false;
                throw new RuntimeException(e);
            }

            if(got) this.control.sair(this.id);
        }
    }

    public static void main(String[] args) throws InterruptedException {

        PonteControl control = new PonteControl();
        // Sem o controle vários carros entram na ponte ao mesmo tempo

        Carro c1 = new Carro(true,  105, 1, control);
        Carro c2 = new Carro(false, 125, 2, control);
        Carro c3 = new Carro(true,  102, 3, control);
        Carro c4 = new Carro(false, 210, 4, control);
        Carro c5 = new Carro(true,  135, 5, control);
        Carro c6 = new Carro(false, 145, 6, control);

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
