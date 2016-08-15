package edu.duke.bioinformatics.service;

import edu.duke.bioinformatics.domain.MessageListener;
import edu.duke.bioinformatics.domain.Queue;
import edu.duke.bioinformatics.queue.kafka.KafkaListener;
import edu.duke.bioinformatics.queue.kafka.KafkaSender;
import edu.duke.bioinformatics.repository.MessageListenerRepository;
import edu.duke.bioinformatics.repository.QueueRepository;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.transaction.Transactional;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Transactional
@Component
public class KafkaQueueService implements QueueService
{
    private static final Logger logger = LoggerFactory.getLogger(KafkaQueueService.class);

    private List<KafkaListener> kafkaListeners = new ArrayList<>();

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    @Resource
    private KafkaSender kafkaSender;

    @Resource
    private MessageListenerRepository messageListenerRepository;

    @Resource
    private QueueRepository queueRepository;

    @Override
    public void send(@NotBlank String queue, @NotBlank String message)
    {
        kafkaSender.send(queue, null, message);
    }

    @Override
    public void registerListeners(@NotBlank String queue, @NotEmpty List<String> listeners)
    {
        final Queue queueEntity = queue(queue);

        logger.info("-- registering listeners={} to: {} --", listeners, queue);

        for (String listener : listeners)
        {
            listener = StringUtils.trimToEmpty(listener);
            MessageListener messageListener = messageListenerRepository.findByQueueAndUrl(queueEntity, listener);
            boolean registered = messageListener != null;
            if (!registered)
            {
                messageListener = messageListenerRepository.save(new MessageListener().setQueue(queueEntity).setUrl(listener));
                startListener(messageListener);
            }
            logger.info("-- {} " + (registered ? "already " : "")
                    + "registered for: {} --", listener, queue);
        }
    }

    @Override
    public void deregisterListeners(@NotBlank String queue, @NotEmpty List<String> listeners)
    {
        final Queue queueEntity = queue(queue);

        logger.info("-- de-registering listeners={} from: {} --" , listeners, queue);

        for (String listener : listeners)
        {
            listener = StringUtils.trimToEmpty(listener);
            final MessageListener messageListener = messageListenerRepository.findByQueueAndUrl(
                queueEntity, listener);
            boolean deregistered = messageListener == null;
            if (!deregistered)
            {
                messageListenerRepository.delete(messageListener);

                synchronized (kafkaListeners)
                {
                    final KafkaListener matchedListener = findListener(queue, listeners);
                    if (matchedListener != null)
                    {
                        kafkaListeners.remove(matchedListener);
                    }
                }
            }
            logger.info("-- {} " + (deregistered ? "already " : "")
                    + "de-registered for: {} --", listener, queue);
        }
    }

    public KafkaListener findListener(@NotBlank String queue, @NotEmpty List<String> listeners)
    {
        return IterableUtils.find(
            kafkaListeners,
            (kafkaListener) -> queue.equalsIgnoreCase(kafkaListener.getTopic()) &&
                    listeners.contains(kafkaListener.getListenerUrl()));
    }

    @Override
    public int startListeners()
    {
        final List<MessageListener> messageListeners = messageListenerRepository.findAll(new Sort("queue"));
        for (final MessageListener messageListener : messageListeners)
        {
            startListener(messageListener);
        }
        return kafkaListeners.size();
    }

    private void startListener(MessageListener messageListener)
    {
        System.out.println("messageListener===: " + messageListener);
        System.out.println("messageListener====: " + messageListener.getQueue());
        final KafkaListener listener = new KafkaListener(
            bootstrapServers, messageListener.getQueue().getName(), messageListener.getUrl());
        listener.start();
        synchronized (kafkaListeners)
        {
            kafkaListeners.add(listener);
        }
    }

    @Override
    public void stopListeners()
    {
        for (final KafkaListener kafkaListener : kafkaListeners)
        {
            kafkaListener.close();
        }
    }

    private Queue queue(String name)
    {
        Queue queue = queueRepository.findByName(name);
        if (queue == null)
        {
            queue = queueRepository.save(new Queue().setName(name));
        }
        return queue;
    }
}
