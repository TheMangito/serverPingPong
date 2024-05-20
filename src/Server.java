import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.System.in;
import static java.lang.System.out;

public class Server {
    private int posicionInicialPelotaX = 453;
    private int posicionInicialPelotaY = 200;
    private int posicionPelotaX = 440;
    private int posicionPelotaY = 200;
    private int movimientoPelotaX = 3;
    private int movimientoPelotaY = 1;
    private Timer timer;
    private boolean gameScoreChange = false;
    private boolean totalStop = false;
    private List<ClientHandler> clients;
    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private boolean movimientoActivo;

    private int player1 = 150;
    private int player2 = 150;

    private int scorePlayer1 = 0;
    private int scorePlayer2 = 0;

    public void broadcast(String message, ClientHandler clientException){
        for (ClientHandler client : clients){
            if (client != clientException){
                client.getOut().println(message);
            }
        }
    }

    public void setMovimientoActivo(boolean movimientoActivo){
        this.movimientoActivo = movimientoActivo;
    }

    public void iniciarPelota(){
        if (!movimientoActivo){
            posicionPelotaX = posicionInicialPelotaX;
            posicionPelotaY = posicionInicialPelotaY;
            if (timer != null) {
                timer.cancel();
            }
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    moverPelota();
                    if (gameScoreChange || totalStop) {
                        this.cancel();
                        timer.cancel();
                    }
                }
            }, 0, 7);
        }

    }
    public void moverJugador(String input){
        new Thread(() -> {
            switch (input){
                case "W" -> {
                    if (!(player1 + 20 < 40))
                        player1-=20;
                    System.out.println(player1);
                    broadcast("UPP1:"+player1, null);
                }
                case "S" -> {
                    if (!(player1 + 20 > 300))
                        player1+=20;
                    System.out.println(player1);
                    broadcast("DWP1:"+player1, null);
                }
                case "UP" -> {
                    if (!(player2 + 20 < 40))
                        player2 -= 20;
                    System.out.println(player2);
                    broadcast("UPP2:"+player2, null);
                }
                case "DOWN" -> {
                    if (!(player2 + 20 > 300))
                        player2 += 20;
                    System.out.println(player2);
                    broadcast("DWP2:"+player2, null);
                }
            }

        }).start();
    }
    public void restart() {
        movimientoPelotaX = -movimientoPelotaX;
        gameScoreChange = false;
        iniciarPelota();
    }
    public void moverPelota() {
            posicionPelotaX += movimientoPelotaX;
            posicionPelotaY += movimientoPelotaY;
            if (posicionPelotaX > 920 || posicionPelotaX < 0) {
                gameScoreChange = true;
                if (posicionPelotaX>500){
                    scorePlayer1++;
                    broadcast("SCOP1", null);
                }
                else{
                    scorePlayer2++;
                    broadcast("SCOP2", null);
                }
                if (scorePlayer1 == 3 || scorePlayer2 == 3){
                    totalStop = true;
                    if (scorePlayer1 == 3){
                        broadcast("P1WIN", null);
                    }else{
                        broadcast("P2WIN", null);
                    }
                }else {
                    restart();
                }


            }
            if ((posicionPelotaX < 15 && posicionPelotaX > 0) || (posicionPelotaX < 920 && posicionPelotaX > 900))
                if (posicionPelotaY > player1 && posicionPelotaY<(player1+100) || posicionPelotaY > player2 && posicionPelotaY<(player2+100))
                    movimientoPelotaX = -movimientoPelotaX;
            if (posicionPelotaY > 400 || posicionPelotaY < 0) {
                movimientoPelotaY = -movimientoPelotaY;
            }
            broadcast(posicionPelotaX+","+posicionPelotaY, null);

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
