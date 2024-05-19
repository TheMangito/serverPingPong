import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class Server {
    private List<ClientHandler> clients;
    private ServerSocket serverSocket = null;
    private Socket socket = null;

    public void broadcast(String message, ClientHandler clientException){
        for (ClientHandler client : clients){
            if (client != clientException){
                client.getOut().println(clientException.getPlayerID() + " : " + message);
            }
        }
    }

    public Server(){

        int id = 1;
        clients = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(6666);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            if(id < 3){
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    out.println("I/O error: " + e);
                }
                clients.add(new ClientHandler(socket, this, id));
                clients.getLast().start();
                id++;
            }

        }
    }


}
