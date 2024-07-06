package Entrega_2;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class BARBEIRO {

    public static class Barbeiro implements Runnable {

        private ReentrantLock lockFila = new ReentrantLock();
        private ReentrantLock lockEspediente = new ReentrantLock();
        private ReentrantLock lockDormir = new ReentrantLock();
        private Semaphore semaphore;
        int VagasTotais;
        int Atendidos = 0, Chegaram = 0;

        ArrayList<Cliente> fila = new ArrayList<>(0);

        private boolean dormindo = true;
        private boolean espediente = true;

        Barbeiro(int VagasTotais)
        {
            semaphore = new Semaphore(VagasTotais);
            this.VagasTotais = VagasTotais;
        }

        void encerrarEspediente(){
            lockEspediente.lock();
            espediente = false;
            lockEspediente.unlock();
        }

        boolean isEspediente(){
            lockEspediente.lock();
            try { return espediente; }
            finally { lockEspediente.unlock(); }
        }

        boolean TryToGetVaga(Cliente c) {

            boolean conseguiu = false;

            lockFila.lock();
            try
            {
                Chegaram++;
                if(semaphore.tryAcquire())
                {
                    // ocupa uma vaga se o barbeiro estiver atendendo outro cliente, ou
                    fila.add(c);

                    // acorda o barbeiro para ser atendido por ele e libera a vaga.
                    System.out.println("Cliente " + c.id + " entrou na Barbearia");

                    if(isDormindo()) awake(c.id);

                    conseguiu = true;
                }
                else
                {
                    System.out.println("Cliente " + c.id + " não conseguiu vaga");
                }
            }
            finally { lockFila.unlock(); }

            return conseguiu;
        }

        void awake(int cid)
        {
            lockDormir.lock();
            try
            {
                this.dormindo = false;
                System.out.println("Barbeiro Awaken by Cliente " + cid +  "!");
            }
            finally
            {
                lockDormir.unlock();
            }
        }

        void dormir()
        {
            lockDormir.lock();
            try
            {
                this.dormindo = true;
                System.out.println("Barbeiro Sleeping!");
            }
            finally
            {
                lockDormir.unlock();
            }
        }

        boolean isDormindo(){
            lockDormir.lock();
            try
            {
                return dormindo;
            }
            finally
            {
                lockDormir.unlock();
            }
        }

        void atender()
        {
            lockFila.lock();

            try
            {
                if(fila.isEmpty()) dormir();
                else
                {
                    Cliente nxt = this.fila.remove(0);

                    System.out.println("Cliente " + nxt.id + " sendo atendido!");

                    semaphore.release();

                    nxt.atender();

                    Atendidos++;
                }
            }
            finally { lockFila.unlock(); }
        }

        public void run()
        {
            while(isEspediente())
            {
                if(isDormindo()) continue;
                atender();
            }
        }
    }


    public static class Cliente implements Runnable {
        Barbeiro barbeiro;
        int id;

        Cliente(int id, Barbeiro barbeiro)
        {
            this.id = id;
            this.barbeiro = barbeiro;
        }

        void atender()
        {
            System.out.println("Cliente " + id + " foi atendido e saiu da Barbearia");
        }

        public void run()
        {
            // tenta pegar uma vaga na barbearia
            this.barbeiro.TryToGetVaga(this);
            // aguarda o barbeiro atender ele
            // e sai da barbearia
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int vagas = 10;

        Barbeiro barbeiro = new Barbeiro(vagas);
        Thread Tbarbeiro = new Thread(barbeiro);
        Tbarbeiro.start();

        int TOTAL_CLIENTES = 105;
        ArrayList<Thread> Tclientes = new ArrayList<>(0);

        for(int i=0; i<TOTAL_CLIENTES; i++)
        {
            Cliente c = new Cliente(i, barbeiro);
            Thread t = new Thread(c);
            Tclientes.add(t);
            t.start();
        }

        for(int i=0; i<TOTAL_CLIENTES; i++)
            Tclientes.get(i).join();

        barbeiro.encerrarEspediente();
        Tbarbeiro.join();

        System.out.println("Total de Clientes atendidos: " + barbeiro.Atendidos);
    }
}
