package pl.projektorion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class OnBoardPlatformServer {
    private static final Logger log = LoggerFactory.getLogger(OnBoardPlatformServer.class);

    /*
    A rover will provide the following API:
        * Request-reply telemetry data
        * Publisher-subscribe remote-control data
     */

    public static void main(String[] args) throws Exception {
        log.info("Initializing server");

        try (ZContext ctx = new ZContext()) {
            final ZMQ.Socket inputCommandReceiver = ctx.createSocket(SocketType.SUB);
            final ZMQ.Socket telemetryReply = ctx.createSocket(SocketType.REP);
            inputCommandReceiver.bind("tcp://*:5000");
            telemetryReply.bind("tcp://*:4999");

            inputCommandReceiver.setReceiveTimeOut(0);
            inputCommandReceiver.subscribe("");

            ZMQ.Poller poller = ctx.createPoller(1);
            poller.register(telemetryReply);
            telemetryReply.setReceiveTimeOut(200);

            while (!Thread.currentThread().isInterrupted()) {
                handleTelemetryRequest(poller, telemetryReply);
                handleRoverInputs(inputCommandReceiver);

                Thread.sleep(1000);
            }
        }
    }

    private static void handleTelemetryRequest(ZMQ.Poller poller, ZMQ.Socket telemetryReply) {
        log.info("Polling...");
        final int expectedReplies = poller.poll(10);
        boolean hasMore = true;
        for (int i = 0; i<expectedReplies && hasMore; i++) {
            log.info("Replying {} out of {}", i, expectedReplies);
            if (poller.pollin(0)) {
                final String request = telemetryReply.recvStr(0);
                final String response = processRequest(request);
                log.info("{}: Request = {}, sending response = {}", i, request, response);

                telemetryReply.send(response);
                hasMore = telemetryReply.hasReceiveMore();
            }
        }
    }

    private static void handleRoverInputs(ZMQ.Socket inputCommandReceiver) {
        final String inputs = inputCommandReceiver.recvStr();
        if (inputs != null) {
            log.info("inputs = {}", inputs);
        }
    }

    private static int counter = 0;
    private static String processRequest(final String request) {
        return "Response #" + counter++;
    }

}
