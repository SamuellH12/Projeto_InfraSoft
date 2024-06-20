package Entrega_1;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Banco {

    public static class Conta {
        public int Saldo;
        private ReentrantLock lock = new ReentrantLock();
        Conta(int saldo){ this.Saldo = saldo; }

        public void Deposito(int valor, String user){
            lock.lock();
            try
            {
                this.Saldo += valor;
                System.out.println(user + "\t| Deposito de R$ " + valor + ",00.");
                System.out.println("Saldo Anterior: R$ " + (this.Saldo - valor) + ",00\t Novo Saldo: R$ " + this.Saldo + ",00\n");
            }
            finally{ lock.unlock(); }
        }

        public void Saque(int valor, String user){
            lock.lock();
            try{
                System.out.println(user + "\t| Saque de R$ " + (-valor) + ",00.");

                int old = this.Saldo;
                if(valor <= this.Saldo)
                    this.Saldo -= valor;
                else {
                    System.out.println("Saldo insuficiente: R$ " + (this.Saldo) + ",00.");
                    this.Saldo = 0;
                }

                System.out.println("Saldo Anterior: R$ " + old + ",00\t Novo Saldo: R$ " + this.Saldo + ",00\n");
            }
            finally{ lock.unlock(); }
        }
    }

    public static class User implements Runnable {
        private String Nome;
        private Conta conta;
        private int [] Operations;

        User(String nome, Conta conta, int [] Operations)
        {
            this.Nome = nome;
            this.conta = conta;
            this.Operations = Operations;
        }

        public void run() {

            for(int i=0, valor; i<this.Operations.length; i++)
            {
                valor = this.Operations[i];
                if (valor > 0)
                {
                    this.conta.Deposito(valor, this.Nome);
                }
                else {
                    this.conta.Saque(-valor, this.Nome);
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {

        Conta contaCompartilhada = new Conta(100);

        User u1 = new User("Sofia", contaCompartilhada, new int[]{220, 55 , 53, 101, 404});
        User u2 = new User("Miguel", contaCompartilhada, new int[]{100, -4, 25, 10, 45});
        User u3 = new User("Maria", contaCompartilhada, new int[]{-50, 33, 12, 2, 90});
        User u4 = new User("Zé", contaCompartilhada, new int[]{-200, 100, -455, 250, 45});

        Thread t1 = new Thread(u1);
        Thread t2 = new Thread(u2);
        Thread t3 = new Thread(u3);
        Thread t4 = new Thread(u4);

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        t1.join();
        t2.join();
        t3.join();
        t4.join();

        System.out.println("--\nSaldo final: " + contaCompartilhada.Saldo);
    }
}
