package Entrega_2;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class RESTAURANTE {

    public static class Restaurante {
        private ReentrantLock lock = new ReentrantLock();
        int senhaAtual = 0;       // Para simular a "Fila" requerida na questão
        int senhaNova = 0;
        int lugaresOcupados = 0;
        boolean juntos = false;   //

        int GetNewSenha(int id){
            lock.lock();
            int valor = -1;

            try
            {
                valor = this.senhaNova++;
                System.out.println("||| Cliente com id " + id + " recebeu a senha " + valor + " |||\n");
            }
            finally { lock.unlock(); }

            return valor;
        }

        boolean GetAssento(int senhaCliente, int id){
            lock.lock();
            try
            {
                if(this.juntos || this.senhaAtual != senhaCliente)  return false;

                this.lugaresOcupados++;
                this.senhaAtual++;

                System.out.println(">>> Cliente " + id + " entrou. <<<\n" + this.lugaresOcupados + " cliente(s) sentado(s)\n");

                if(this.lugaresOcupados == 5){
                    this.juntos = true;
                    System.out.println("\nMesa cheia!!!\n");
                }
            }
            finally { lock.unlock(); }

            return true;
        }

        void LeaveAssento(int id){
            lock.lock();

            this.lugaresOcupados--;

            System.out.println("<<< Cliente " + id + " saiu. >>>\n" + this.lugaresOcupados + " cliente(s) sentado(s).\n");

            if(this.lugaresOcupados == 0){
                juntos = false;
                System.out.println("\nMesa vazia!!!\n");
            }

            lock.unlock();
        }
    }

    public static class Cliente implements Runnable {
        private int tempo, id;
        private Restaurante restaurante;

        Cliente(int id, int tempo, Restaurante restaurante){
            this.id = id;
            this.tempo = tempo;
            this.restaurante = restaurante;
        }

        public void run()
        {
            int senha = restaurante.GetNewSenha(id);
            boolean sentado = false;

            while(!sentado) sentado = restaurante.GetAssento(senha, id);

            try
            {
                Thread.sleep(tempo);
            }
            catch (InterruptedException e)
            {
                restaurante.LeaveAssento(id);
                return;
            }

            restaurante.LeaveAssento(id);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Restaurante restaurante = new Restaurante();

        int TOTAL_CLIENTES = 105;
        ArrayList<Thread> Tclientes = new ArrayList<>(0);

        Random random = new Random();

        for(int i=0; i<TOTAL_CLIENTES; i++)
        {
            int tempo = random.nextInt(10, 200);
            Cliente c = new Cliente(i, tempo, restaurante);
            Thread t = new Thread(c);
            Tclientes.add(t);
            t.start();
        }

        for(int i=0; i<TOTAL_CLIENTES; i++)
            Tclientes.get(i).join();
    }
}
