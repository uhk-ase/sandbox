package cz.uhk.fim.ase.network;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import org.zeromq.ZMQ;

/**
 * @author Tomáš Kolinger <tomas@kolinger.name>
 */
public class ZeromqClient implements Runnable {

    private String address;
    private int port;
    private boolean running = true;

    public ZeromqClient(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        try {
            ZMQ.Context context = ZMQ.context(1);
            ZMQ.Socket socket = context.socket(ZMQ.PUSH);
            socket.connect("tcp://" + address + ":" + port);
            Message message;
            for (int count = 1; count <= Benchmark.MESSAGES_COUNT; count++) {
                message = new Message();
                message.setContent(Message.DEFAULT_CONTENT);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ObjectOutput objectOutput = new ObjectOutputStream(outputStream);
                objectOutput.writeObject(message);
                byte[] bytes = outputStream.toByteArray();
                socket.send(bytes, 0);
                outputStream.close();
                objectOutput.close();
            }
            running = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
