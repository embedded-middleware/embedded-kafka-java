package io.github.embedded.kafka.core;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmbeddedKafkaConfig {

    private int kafkaPort;

    private int controllerPort;

    public EmbeddedKafkaConfig() {
    }

    public EmbeddedKafkaConfig kafkaPort(int kafkaPort) {
        this.kafkaPort = kafkaPort;
        return this;
    }

    public EmbeddedKafkaConfig controllerPort(int controllerPort) {
        this.controllerPort = controllerPort;
        return this;
    }
}
