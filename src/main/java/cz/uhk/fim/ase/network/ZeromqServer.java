package cz.uhk.fim.ase.network;

import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import org.zeromq.ZMQ;

/**
 * @author Tomáš Kolinger <tomas@kolinger.name>
 */
public class ZeromqServer implements Runnable {

    private String address;
    private int port;
    private int success = 0;
    private int corrupted = 0;
    private long startTime;
    private ZMQ.Context context;
    private ZMQ.Socket socket;

    public ZeromqServer(String address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            context = ZMQ.context(10);
            socket = context.socket(ZMQ.PULL);
            socket.bind("tcp://" + address + ":" + port);

            Message message;
            startTime = System.currentTimeMillis();
            while (!Thread.currentThread().isInterrupted()) {
                byte[] bytes = socket.recv(0);
                if (bytes == null || bytes.length == 0) {
                    continue;
                }

                ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                ObjectInput objectInput = new ObjectInputStream(inputStream);
                message = (Message) objectInput.readObject();
                if (message == null || !message.getContent().equals(Message.DEFAULT_CONTENT)) {
                    corrupted++;
                } else {
                    success++;
                }
                objectInput.close();
                inputStream.close();
            }
        } catch (Exception e) {
            // ignore
        }
    }

    public void print() {
        socket.close();
        //context.close();

        double duration = (System.currentTimeMillis() - startTime) / (double) 1000; // second
        double rate = success / duration;
        System.out.println("ZeroMQ -> success: " + success + " / corrupted: " + corrupted + " / rate: " + rate);
    }
}
