package cz.uhk.fim.ase.network;

import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author Tomáš Kolinger <tomas@kolinger.name>
 */
public class PlainClient implements Runnable {

    private String address;
    private int port;
    private boolean running = true;

    public PlainClient(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        try {
            Message message;
            for (int count = 1; count <= Benchmark.MESSAGES_COUNT; count++) {
                try {
                    Socket socket = new Socket(address, port);
                    message = new Message();
                    message.setContent(Message.DEFAULT_CONTENT);
                    ObjectOutput objectOutput = new ObjectOutputStream(socket.getOutputStream());
                    objectOutput.writeObject(message);
                    objectOutput.flush();
                } catch (Exception e) {
                    // ignore
                }
            }
            running = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
