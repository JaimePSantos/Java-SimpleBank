package Guiao7.Exe1e2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static int port = 12345;
    private static int n = 5;
    private static ArrayList<BankWorker> clients = new ArrayList<>();
    private static Bank bank = new Bank(0);

    public static void main(String[] args) throws IOException, InterruptedException {

        ExecutorService servers = Executors.newFixedThreadPool(n);
        ServerSocket sSock = new ServerSocket(port);

        while (true) {
            System.out.println("New Client");
            Socket clSock = sSock.accept();
            BankWorker clientThread = new BankWorker(clSock, bank);
            clients.add(clientThread);
            servers.execute(clientThread);
        }
    }
}
