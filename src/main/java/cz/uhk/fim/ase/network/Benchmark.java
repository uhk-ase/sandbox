package cz.uhk.fim.ase.network;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Tomáš Kolinger <tomas@kolinger.name>
 */
public class Benchmark {

    final public static String SERVER_ADDRESS = "127.0.0.1";
    final public static int SERVER_PORT = 13338;

    final public static int MESSAGES_COUNT = 1000000;
    final public static int THREADS = 10;

    public void run() throws Exception  {
        runZeromqSingle();
//        runZeromqMultiThreaded();
//        runPlainSingle();
//        runPlainMultiThreaded();
    }

    public void runZeromqSingle() throws Exception {
        ZeromqServer server = new ZeromqServer(SERVER_ADDRESS, SERVER_PORT);
        Thread serverThread = new Thread(server);
        serverThread.start();

        Thread.sleep(1000);

        ZeromqClient client = new ZeromqClient(SERVER_ADDRESS, SERVER_PORT);
        Thread clientThread = new Thread(client);
        clientThread.start();

        while (client.isRunning()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // ignore
            }
        }
        server.print();
    }

    public void runZeromqMultiThreaded() throws Exception {
        ZeromqServer server = new ZeromqServer(SERVER_ADDRESS, SERVER_PORT);
        Thread serverThread = new Thread(server);
        serverThread.start();

        Thread.sleep(1000);

        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(THREADS, THREADS, 100, TimeUnit.MILLISECONDS, queue);

        List<Future<?>> futures = new ArrayList<>();
        for (int count = 1; count <= THREADS; count++) {
            ZeromqClient client = new ZeromqClient(SERVER_ADDRESS, SERVER_PORT);
            futures.add(executor.submit(client));
        }

        while (true) {
            Thread.sleep(100);
            Iterator<Future<?>> iterator = futures.iterator();
            while (iterator.hasNext()) {
                Future<?> future = iterator.next();
                if (future.isDone()) {
                    iterator.remove();
                }
            }
            if (futures.size() == 0) {
                break;
            }
        }

        server.print();
    }

    public void runPlainSingle() throws Exception {
        PlainServer server = new PlainServer(SERVER_ADDRESS, SERVER_PORT);
        Thread serverThread = new Thread(server);
        serverThread.start();

        Thread.sleep(1000);

        PlainClient client = new PlainClient(SERVER_ADDRESS, SERVER_PORT);
        Thread clientThread = new Thread(client);
        clientThread.start();

        while (client.isRunning()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // ignore
            }
        }
        server.print();
    }


    public void runPlainMultiThreaded() throws Exception {
        PlainServer server = new PlainServer(SERVER_ADDRESS, SERVER_PORT);
        Thread serverThread = new Thread(server);
        serverThread.start();

        Thread.sleep(1000);

        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(THREADS, THREADS, 100, TimeUnit.MILLISECONDS, queue);

        List<Future<?>> futures = new ArrayList<>();
        for (int count = 1; count <= THREADS; count++) {
            PlainClient client = new PlainClient(SERVER_ADDRESS, SERVER_PORT);
            futures.add(executor.submit(client));
        }

        while (true) {
            Thread.sleep(100);
            Iterator<Future<?>> iterator = futures.iterator();
            while (iterator.hasNext()) {
                Future<?> future = iterator.next();
                if (future.isDone()) {
                    iterator.remove();
                }
            }
            if (futures.size() == 0) {
                break;
            }
        }

        server.print();
    }
}
