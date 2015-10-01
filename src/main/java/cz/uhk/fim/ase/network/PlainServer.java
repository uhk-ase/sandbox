package cz.uhk.fim.ase.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Tomáš Kolinger <tomas@kolinger.name>
 */
public class PlainServer implements Runnable {

    private String address;
    private int port;
    private int success = 0;
    private int corrupted = 0;
    private long startTime;
    private ServerSocket serverSocket;

    public PlainServer(String address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port, 1024, Inet4Address.getByName(address));

            Message message;
            startTime = System.currentTimeMillis();
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket socket = serverSocket.accept();
                    ObjectInput objectInput = new ObjectInputStream(socket.getInputStream());
                    message = (Message) objectInput.readObject();
                    if (message == null || !message.getContent().equals(Message.DEFAULT_CONTENT)) {
                        corrupted++;
                    } else {
                        success++;
                    }

                    objectInput.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void print() throws Exception {
        serverSocket.close();

        double duration = (System.currentTimeMillis() - startTime) / (double) 1000; // second
        double rate = success / duration;
        System.out.println("Plain -> success: " + success + " / corrupted: " + corrupted + " / rate: " + rate);
    }
}
