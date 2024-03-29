package io.github.embedded.kafka.core;

import kafka.server.KafkaConfig;
import kafka.server.KafkaRaftServer;
import kafka.server.MetaProperties;
import kafka.tools.StorageTool;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.Uuid;
import org.apache.kafka.common.utils.Time;
import org.assertj.core.util.Files;
import scala.Option;
import scala.collection.JavaConverters;
import scala.collection.immutable.Seq;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Slf4j
public class EmbeddedKafkaServer {

    private int kafkaPort;

    private int controllerPort;

    private KafkaRaftServer kafkaRaftServer;

    public EmbeddedKafkaServer() {
        this(new EmbeddedKafkaConfig());
    }

    public EmbeddedKafkaServer(EmbeddedKafkaConfig embeddedKafkaConfig) {
        try {
            if (embeddedKafkaConfig.getKafkaPort() == 0) {
                this.kafkaPort = SocketUtil.getFreePort();
            } else {
                this.kafkaPort = embeddedKafkaConfig.getKafkaPort();
            }
            if (embeddedKafkaConfig.getControllerPort() == 0) {
                this.controllerPort = SocketUtil.getFreePort();
            } else {
                this.controllerPort = embeddedKafkaConfig.getControllerPort();
            }
        } catch (Exception e) {
            log.error("exception is ", e);
            throw new IllegalStateException("start kafka broker failed");
        }
    }

    public void start() throws Exception {
        Properties properties = new Properties();
        File temporaryFolder = Files.newTemporaryFolder();
        temporaryFolder.deleteOnExit();
        properties.setProperty("log.dirs", temporaryFolder.getCanonicalPath());
        properties.setProperty("process.roles", "broker,controller");
        properties.setProperty("node.id", "0");
        properties.setProperty("broker.id", "0");
        properties.setProperty("auto.create.topics.enable", "true");
        properties.setProperty("offsets.topic.replication.factor", "1");
        properties.setProperty("listeners",
                String.format("PLAINTEXT://:%d,CONTROLLER://:%d", kafkaPort, controllerPort));
        properties.setProperty("controller.quorum.voters", String.format("0@localhost:%d", controllerPort));
        properties.setProperty("controller.listener.names", "CONTROLLER");
        properties.setProperty("listener.security.protocol.map",
                "CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT,SSL:SSL,SASL_PLAINTEXT:SASL_PLAINTEXT,SASL_SSL:SASL_SSL");
        KafkaConfig kafkaConfig = new KafkaConfig(properties);
        log.info("begin to format raft");
        MetaProperties metaProperties = StorageTool.buildMetadataProperties(Uuid.randomUuid().toString(), kafkaConfig);
        List<String> auxList = Collections.singletonList(temporaryFolder.getCanonicalPath());
        Seq<String> seq = JavaConverters.collectionAsScalaIterableConverter(auxList).asScala().toSeq();
        StorageTool.formatCommand(System.out, seq, metaProperties, true);
        this.kafkaRaftServer = new KafkaRaftServer(kafkaConfig, Time.SYSTEM,
                Option.apply("kafka-broker"));
        kafkaRaftServer.startup();
    }

    public int getKafkaPort() {
        return kafkaPort;
    }

    public void close() throws Exception {
        kafkaRaftServer.shutdown();
    }

}
