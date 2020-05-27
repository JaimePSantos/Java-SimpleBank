package Guiao7.Exe1e2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);

        try (Socket socket = new Socket("127.0.0.1", 12345)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));


            while (true) {
                System.out.print("Client>");
                String line = scan.nextLine();
                if ( line.equals("quit") )
                    break;
                out.println(line);
                out.flush();
                System.out.println(in.readLine());
            }

            socket.shutdownInput();
            socket.shutdownOutput();
        }
    }
}
