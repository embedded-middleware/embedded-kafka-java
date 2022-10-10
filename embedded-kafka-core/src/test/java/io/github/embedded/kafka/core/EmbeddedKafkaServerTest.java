package io.github.embedded.kafka.core;

import org.junit.jupiter.api.Test;

class EmbeddedKafkaServerTest {

    @Test
    public void testKfkServerBoot() throws Exception {
        EmbeddedKafkaServer server = new EmbeddedKafkaServer();
        server.start();
        server.close();
    }

}
