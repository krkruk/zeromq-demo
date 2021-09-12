package pl.projektorion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Client {
    private final static Logger log = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws Exception {
        log.info("Initializing Client");

        try (ZContext ctx = new ZContext()) {
            ZMQ.Socket request = ctx.createSocket(SocketType.REQ);
            request.connect("tcp://localhost:4999");

            int count = 0;
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(1000);
                request.send(String.format("My request #%d", count++), 0);
                Thread.sleep(100);
                final String response = request.recvStr();
                log.info("Response = {}", response);
            }
        }
    }
}
