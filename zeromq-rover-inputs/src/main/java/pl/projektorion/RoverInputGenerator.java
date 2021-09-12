package pl.projektorion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Random;

public class RoverInputGenerator {
    private static final Logger log = LoggerFactory.getLogger(RoverInputGenerator.class);

    public static void main(String[] args) throws Exception {
        log.info("Initializing {}", RoverInputGenerator.class.getName());
        try (final ZContext ctx = new ZContext()) {
            final ZMQ.Socket inputsPublisher = ctx.createSocket(SocketType.PUB);
            inputsPublisher.connect("tcp://*:5000");

            while (!Thread.currentThread().isInterrupted()) {
                final String roverInputs = generateRoverInputs();
                inputsPublisher.send(roverInputs);
                log.info("Sent = {}", roverInputs);
                Thread.sleep(100);
            }
        }
    }

    private static String generateRoverInputs() {
        final Random r = new Random(System.currentTimeMillis());
        final int BOUND = 256;

        return String.format("X=%d, Y=%d", r.nextInt(BOUND), r.nextInt(BOUND));
    }
}
