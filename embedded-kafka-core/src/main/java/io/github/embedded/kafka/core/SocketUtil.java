package io.github.embedded.kafka.core;

import java.net.ServerSocket;

public class SocketUtil {

    public static int getFreePort() throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        }
    }

}
