import javax.imageio.IIOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server implements TCPConnectionListener {
    private static int x;
    private static int y;

    public static void main(String args[]) {

        new Server();

    }

    public static ArrayList<TCPConnection> connections = new ArrayList<>();

    public Server() {
        System.out.println("Server running...");
        try (ServerSocket serverSocket = new ServerSocket(8001)) {
            while (true) {
                try {
                    new TCPConnection(this, serverSocket.accept());

                } catch (IIOException ex) {
                    System.out.println("Ошибка");
                }
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);

    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String str) {

        tcpConnection.setSenderTrue();
        for (int i = 0; i < connections.size(); i++) {
            connections.get(i).setReceiverTrue();
        }
        sendToAllConnections(str);
        tcpConnection.setSenderFalse();


    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToAllConnections("Клиент вышел " + tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception ex) {
        System.out.println("Ошибка");
    }

    private void sendToAllConnections(String str) {
        for (int i = 0; i < connections.size(); i++) {
            if (connections.get(i).isReceiver() && !connections.get(i).isSender()) {
                connections.get(i).sendString(str);
            }
        }

    }
}
