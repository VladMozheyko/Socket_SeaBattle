import java.io.*;
import java.net.Socket;

public class TCPConnection {

    private final Socket socket;
    private final Thread thread;
    private final TCPConnectionListener eventListener;
    private final BufferedReader in;
    private final BufferedWriter out;
    private boolean sender = false;
    private boolean receiver = false;

    public TCPConnection(TCPConnectionListener eventListener, String ip, int port) throws IOException {
        this(eventListener, new Socket(ip, port));

    }

    public TCPConnection(TCPConnectionListener eventListener, Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.onConnectionReady(TCPConnection.this);
                    while (!thread.isInterrupted()) {
                        String msg = in.readLine();
                        eventListener.onReceiveString(TCPConnection.this, msg);

                    }
                } catch (IOException e) {
                    eventListener.onException(TCPConnection.this, e);
                } finally {
                    eventListener.onDisconnect(TCPConnection.this);
                }
            }
        });
        thread.start();
    }

    public synchronized void sendString(String str) {
        try {
            out.write(str + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
            disconnect();
        }

    }

    public synchronized void disconnect() {
        thread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
        }
    }

    public synchronized void setReceiverTrue() {
        receiver = true;
    }

    public synchronized void setReceiverFalse() {
        receiver = false;
    }

    public synchronized boolean isReceiver() {
        return receiver;
    }


    public synchronized void setSenderTrue() {
        sender = true;
    }

    public synchronized void setSenderFalse() {
        sender = false;
    }

    public synchronized boolean isSender() {
        return sender;
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
