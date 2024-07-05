package temp;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class BARBEIRO {

    public static class Barbearia {
        private ReentrantLock lock = new ReentrantLock();
        int VagasTotais;
        boolean atendendo = false;
        ArrayList<Cliente> fila;

        boolean TryToGetVaga(){
            lock.lock();
            boolean conseguiu = false;

            try
            {
                if(this.VagasTotais > 0)
                {
                    if(!atendendo) atendendo = true; //acorda o barbeiro
                    else this.VagasTotais--;



                    conseguiu = true;
                }
            }
            finally { lock.unlock(); }

            return conseguiu;
        }

    }

    public static class Barbeiro implements Runnable {
        public void run() {

        }
    }

    public static class Cliente implements Runnable {
        public void run() {

        }
    }

    public static void main(String[] args) throws InterruptedException {


    }
}
