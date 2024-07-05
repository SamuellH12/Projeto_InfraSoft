package temp;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class BARBEIRO {

    public static class Barbearia {
        private ReentrantLock lock = new ReentrantLock();
        int VagasTotais;
        boolean atendendo = false;
        ArrayList<Cliente> fila;

        boolean TryToGetVaga(Cliente c){
            lock.lock();
            boolean conseguiu = false;

            try
            {
                if(this.VagasTotais > 0)
                {
                    if(!atendendo) atendendo = true; //acorda o barbeiro para ser atendido por ele.
                    else this.VagasTotais--;         // só ocupa uma vaga se o barbaeiro não estiver atendendo ele.

                    fila.add(c);

                    conseguiu = true;

                    System.out.println("Cliente " + c.id + " entrou na Barbearia");
                }
            }
            finally { lock.unlock(); }

            return conseguiu;
        }

        boolean
    }

    public static class Barbeiro implements Runnable {

        public void run() {

        }
    }

    public static class Cliente implements Runnable {

        int id;

        public void run() {

        }
    }

    public static void main(String[] args) throws InterruptedException {


    }
}
