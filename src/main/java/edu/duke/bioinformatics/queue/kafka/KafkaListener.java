package edu.duke.bioinformatics.queue.kafka;

import edu.duke.bioinformatics.queue.MessageHandler;
import edu.duke.bioinformatics.queue.handler.UrlMessageHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaListener
{
    private static final Logger logger = LoggerFactory.getLogger(KafkaListener.class);

    private static final long DEFAULT_RETRY_DELAY = 5000;

    private final List<KafkaConsumer<String,String>> kafkaConsumers = new ArrayList<>();
    private Map<Class<? extends Exception>, Long> retryDelays;

    private String bootstrapServers;
    private String topic;
    private int consumerThreadCount = 3;
    private boolean autoStart = true;

    private String listenerUrl;

    private ExecutorService executor;

    private MessageHandler<String> messageHandler = new UrlMessageHandler();

    public KafkaListener(String bootstrapServers, String topic, String listenerUrl)
    {
        this.bootstrapServers = bootstrapServers;
        this.topic = topic;
        this.listenerUrl = listenerUrl;
    }

    public void start()
    {
        if (isAutoStart())
        {
            final Object[] messageArguments = new Object[]{
                    topic, consumerThreadCount, listenerUrl };
            logger.info("-- creating Kafka consumer for topic '{}' with {} " +
                    "threads using messageHandler: {} --", messageArguments);
            initConsumerWorkers();
            logger.info("-- done creating Kafka consumer for topic '{}' with {} " +
                    "threads using messageHandler: {} --", messageArguments);
        }
        else
        {
            logger.info("-- NOT starting consumer, autoStart=false --");
        }
    }

    private Properties properties()
    {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, listenerUrl);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.FALSE.toString());
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        return properties;
    }

    public void setRetryDelays(Map<Class<? extends Exception>, Long> retryDelays)
    {
        this.retryDelays = retryDelays;
    }

    long getRetryDelay(Exception exception)
    {
        Long retryDelay = exception != null && retryDelays != null ?
                retryDelays.get(exception.getClass()) : null;
        return retryDelay != null ? retryDelay : DEFAULT_RETRY_DELAY;
    }

    private void initConsumerWorkers()
    {
        executor = Executors.newCachedThreadPool();
        initConsumerConnectors();
        for (KafkaConsumer consumer : kafkaConsumers)
        {
            getExecutor().execute(newWorker(consumer));
        }
    }

    KafkaListenerWorker newWorker(final KafkaConsumer consumer)
    {
        return new KafkaListenerWorker(consumer, this);
    }

    void initConsumerConnectors()
    {
        for (int ctr = 0; ctr < consumerThreadCount; ctr++)
        {
            kafkaConsumers.add(kafkaConsumer(properties()));
        }
    }

    KafkaConsumer<String, String> kafkaConsumer(Properties properties)
    {
        return new KafkaConsumer<>(properties);
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getConsumerThreadCount()
    {
        return consumerThreadCount;
    }

    public void setConsumerThreadCount(int consumerThreadCount)
    {
        this.consumerThreadCount = consumerThreadCount;
    }

    public boolean isAutoStart()
    {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart)
    {
        this.autoStart = autoStart;
    }

    public MessageHandler messageHandler()
    {
        return messageHandler;
    }

    public String getListenerUrl()
    {
        return listenerUrl;
    }

    ExecutorService getExecutor()
    {
        return executor;
    }

    @PreDestroy
    public void close()
    {
        getExecutor().shutdown();
        try
        {
            getExecutor().awaitTermination(1, TimeUnit.SECONDS);
        }
        catch (InterruptedException exception)
        {
            logger.warn("Exception while attempting to shutdown", exception);
        }

        for (KafkaConsumer kafkaConsumer : kafkaConsumers)
        {
            kafkaConsumer.close();
        }
    }
}
