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
        this.id = id;
        System.out.println(id);
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
                        case "W" -> {
                            if (id==1){
                                server.moverJugador("W");
                            }
                        }
                        case "S" -> {
                            if (id==1){
                                server.moverJugador("S");
                            }

                        }
                        case "UP" -> {
                            if (id==2){
                                server.moverJugador("UP");
                            }

                        }
                        case "DOWN" -> {
                            if (id==2){
                                server.moverJugador("DOWN");
                            }

                        }
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
