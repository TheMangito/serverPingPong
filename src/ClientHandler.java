import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread{

    private Server server;

    private int id;

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private final Object lock = new Object();

    public int getPlayerID(){
        return this.id;
    }
    public ClientHandler(Socket client, Server server, int id){
        this.clientSocket = client;
        this.server = server;
        this.id = 1;
    }
    public PrintWriter getOut(){
        return out;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            Thread t1 = new Thread(() -> {
                while (true) {
                    synchronized (lock) {
                        out.println("Enviando Paquete");
                    }
                    try {
                        Thread.sleep(7);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            t1.start();

            // Manejo de mensajes recibidos del cliente
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("RECEIVED FROM CLIENT: " + inputLine);
                synchronized (lock) {
                    switch (inputLine) {
                        case "W" -> out.println("Moviendo:Arriba");
                        case "S" -> out.println("Moviendo:Abajo");
                        default -> {
                        }
                    }
                }

                // Broadcast a todos los clientes


                if (".".equals(inputLine)) {
                    synchronized (lock) {
                        out.println("GOODBYE");
                    }
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                out.close();
                in.close();
                clientSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
