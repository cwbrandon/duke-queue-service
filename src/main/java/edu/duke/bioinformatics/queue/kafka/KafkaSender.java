package edu.duke.bioinformatics.queue.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Properties;

@Component
public class KafkaSender
{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String PROPERTY_BOOTSTRAP_SERVERS = "bootstrap.servers";
    private static final String PROPERTY_KEY_SERIALIZER = "key.serializer";
    private static final String PROPERTY_VALUE_SERIALIZER = "value.serializer";
    private static final String PROPERTY_ACKS = "acks";

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    private Producer<String, String> producer;

    @PostConstruct
    public void open()
    {
        logger.debug("opening kafka connection");

        Properties properties = initializeProperties();

        producer = new KafkaProducer<>(properties);
    }

    Properties initializeProperties()
    {
        Properties properties = new Properties();
        properties.put(PROPERTY_BOOTSTRAP_SERVERS, bootstrapServers);
        logger.info("-- initializing with bootstrap servers={}", bootstrapServers);
        properties.put(PROPERTY_KEY_SERIALIZER, StringSerializer.class.getName());
        properties.put(PROPERTY_VALUE_SERIALIZER, StringSerializer.class.getName());
        properties.put(PROPERTY_ACKS, "all");
        return properties;
    }

    public void send(String topic, String key, String message)
    {
        producer.send(new ProducerRecord<>(topic, key, message));
    }

    @PreDestroy
    public void close()
    {
        logger.debug("closing kafka connection");
        producer.close();
    }
}
