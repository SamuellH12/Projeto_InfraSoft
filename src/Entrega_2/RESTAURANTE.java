package Entrega_2;
import java.util.concurrent.locks.ReentrantLock;

public class RESTAURANTE {

    public static class Restaurante {
        private ReentrantLock lock = new ReentrantLock();
        int senhaAtual = 0;       // Para simular a "Fila" requerida na questão
        int senhaNova = 0;
        int lugaresOcupados = 0;
        boolean juntos = false;   //

        int GetSenhaAtual(){ return senhaAtual; }

        int GetNewSenha(int id){
            lock.lock();
            int valor = -1;

            try
            {
                valor = this.senhaNova++;
                System.out.println("Cliente com id " + id + " recebeu a senha " + valor );
            }
            finally { lock.unlock(); }

            return valor;
        }

        boolean GetAssento(int senhaCliente, int id){
            lock.lock();
            try
            {
                if(this.juntos || this.senhaAtual != senhaCliente)
                {
                    lock.unlock();
                    return false;
                }

                this.lugaresOcupados++;
                this.senhaAtual++;

                System.out.println("Cliente " + id + " acaba de entrar no restaurante.\n" + this.lugaresOcupados + " cliente(s) sentado(s)");

                if(this.lugaresOcupados == 5) this.juntos = true;
            }
            finally { lock.unlock(); }

            return true;
        }

        void LeaveAssento(int id){
            lock.lock();

            this.lugaresOcupados--;
            if(this.lugaresOcupados == 0) juntos = false;

            System.out.println("Cliente " + id + " saiu.\n" + this.lugaresOcupados + " cliente(s) sentado(s).");

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
            int senha = restaurante.GetNewSenha();
            boolean sentado = false;

            while(!sentado) sentado = restaurante.GetAssento(senha, id);

            try
            {
                Thread.sleep(tempo);
            }
            catch (InterruptedException e)
            {
                restaurante.LeaveAssento(id);
            }

            restaurante.LeaveAssento(id);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Restaurante restaurante = new Restaurante();

        int TOTALCLIENTES = 100;

        for(int id=1; id<=TOTALCLIENTES; id++);
            

    }
}
