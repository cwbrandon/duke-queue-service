package edu.duke.bioinformatics.service;

import edu.duke.bioinformatics.domain.MessageListener;
import edu.duke.bioinformatics.domain.Queue;
import edu.duke.bioinformatics.queue.kafka.KafkaSender;
import edu.duke.bioinformatics.repository.MessageListenerRepository;
import edu.duke.bioinformatics.repository.QueueRepository;
import java.net.URL;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Example;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.transaction.Transactional;

@Transactional
@Component
public class KafkaQueueService implements QueueService
{
    private static final Logger logger = LoggerFactory.getLogger(KafkaQueueService.class);

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

        for (String listener : listeners)
        {
            listener = StringUtils.trimToEmpty(listener);
            MessageListener messageListener = messageListenerRepository.findByQueueAndUrl(queueEntity, listener);
            boolean registered = messageListener != null;
            if (!registered)
            {
                messageListenerRepository.save(new MessageListener().setQueue(queueEntity).setUrl(listener));
            }
            logger.info("-- {} " + (registered ? "already " : "")
                    + "registered for: {} --", listener, queue);
        }
    }

    @Override
    public void deregisterListeners(@NotBlank String queue, @NotEmpty List<String> listeners)
    {
        final Queue queueEntity = queue(queue);
        for (String listener : listeners)
        {
            listener = StringUtils.trimToEmpty(listener);
            final MessageListener messageListener = messageListenerRepository.findByQueueAndUrl(
                queueEntity, listener);
            boolean deregistered = messageListener == null;
            if (!deregistered)
            {
                messageListenerRepository.delete(messageListener);
            }
            logger.info("-- {} " + (deregistered ? "already " : "")
                    + "de-registered for: {} --", listener, queue);
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
