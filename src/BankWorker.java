package Guiao7.Exe1e2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BankWorker implements Runnable {
    private Socket clSock;
    private Bank banco;

    public BankWorker(Socket clSock, Bank banco) {
        this.clSock = clSock;
        this.banco = banco;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clSock.getInputStream()));
            PrintWriter out = new PrintWriter(clSock.getOutputStream());

            String r;
            while ((r = in.readLine()) != null && !r.equals("quit")) {
                System.out.println(r);
                out.print("Server> ");
                String[] args = r.toLowerCase().split(" ");
                try {
                    switch (args[0]) {
                        case "create":
                            out.println(this.banco.createAccount(Integer.parseInt(args[1])));
                            break;
                        case "close":
                            out.println(this.banco.closeAccount(Integer.parseInt(args[1])));
                            break;
                        case "balance":
                            out.println(this.banco.balance(Integer.parseInt(args[1])));
                            break;
                        case "totalbalance":
                            int[] contas = new int[args.length - 1];
                            for (int i = 1; i < args.length; i++)
                                contas[i - 1] = Integer.parseInt(args[i]);
                            out.println(this.banco.totalBalance(contas));
                            break;
                        case "deposit":
                            this.banco.deposit(
                                    Integer.parseInt(args[1]),
                                    Integer.parseInt(args[2])
                            );
                            out.println(this.banco.balance(Integer.parseInt(args[1])));
                            break;
                        case "withdraw":
                            this.banco.withdraw(
                                    Integer.parseInt(args[1]),
                                    Integer.parseInt(args[2])
                            );
                            out.println(this.banco.balance(Integer.parseInt(args[1])));
                            break;
                        case "transfer":
                            this.banco.transfer(
                                    Integer.parseInt(args[1]),
                                    Integer.parseInt(args[2]),
                                    Float.parseFloat(args[3])
                            );
                        case "movements":
                            out.println(this.banco.getMovsFromAccount(Integer.parseInt(args[1])));
                    }


                } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {
                    out.println(-1);
                    System.out.println(ignored);
                } catch (InvalidAccount | InterruptedException | NotEnoughFunds invalidAccount) {
                    invalidAccount.printStackTrace();
                }
                out.flush();
            }

            clSock.shutdownOutput();
            clSock.shutdownInput();
            clSock.close();
        } catch (IOException ignored) {
        }

    }
}
