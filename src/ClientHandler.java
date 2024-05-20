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
            // Manejo de mensajes recibidos del cliente
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("RECEIVED FROM CLIENT: " + inputLine);
                synchronized (lock) {
                    switch (inputLine) {
                        case "W" -> server.moverJugador("W");
                        case "S" -> server.moverJugador("S");
                        case "UP" -> server.moverJugador("UP");
                        case "DOWN" -> server.moverJugador("DOWN");
                        case "A" -> {
                            server.setMovimientoActivo(false);
                            server.iniciarPelota();
                        }
                        default -> {
                        }
                    }
                }

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
