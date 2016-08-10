package edu.duke.bioinformatics.queue.kafka;

import java.util.Collections;
import java.util.Iterator;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class KafkaListenerWorker implements Runnable
{
    private static final Logger logger = LoggerFactory.getLogger(KafkaListenerWorker.class);

    private static final long POLL_TIMEOUT = 1000;

    private KafkaListener kafkaListener;
    private KafkaConsumer<String, String> kafkaConsumer;

    KafkaListenerWorker(KafkaConsumer<String, String> kafkaConsumer, KafkaListener kafkaListener)
    {
        this.kafkaConsumer = kafkaConsumer;
        this.kafkaListener = kafkaListener;
    }

    @Override
    public void run()
    {
        kafkaConsumer.subscribe(Collections.singletonList(kafkaListener.getTopic()));
        final Iterator<ConsumerRecord<String, String>> iterator = kafkaConsumer.poll(POLL_TIMEOUT).iterator();
        ConsumerRecord<String, String> previous = null;
        while(previous != null || iterator.hasNext())
        {
            previous = handleMessage(iterator, previous);
        }
    }

    ConsumerRecord<String, String> handleMessage(
        final Iterator<ConsumerRecord<String, String>> iterator,  ConsumerRecord<String, String> previous)
    {
        final ConsumerRecord<String, String> current = previous != null ? previous : iterator.next();
        try
        {
            previous = handleOnMessage(current);
        }
        catch (Exception exception)
        {
            previous = handleException(current, exception);
        }
        return previous;
    }

    ConsumerRecord<String, String> handleOnMessage(ConsumerRecord<String, String> current)
    {
        try
        {
            final String message = current.value();
            kafkaListener.messageHandler().onMessage(message);
            commit(current);
        }
        catch (final IllegalStateException exception)
        {
            logger.error("Illegal state exception while executing handleOnMessage", exception);
            return null;
        }
        return null;
    }

    ConsumerRecord<String, String> handleException(ConsumerRecord<String, String> current, final Exception exception)
    {
        logger.error("Failed processing message: {}", current, exception);
        final String message = current.value();
        boolean retry = true;
        try
        {
            // the message listener configuration can decide what exceptions to retry on possibly
            retry = kafkaListener.messageHandler().retryOnFailure(message, exception);;
            if (!retry)
            {
                logger.info("-- retryOnFailure is false for message: {}; committing --", current);
                commit(current);
                current = null;
            }
        }
        catch (Exception failure)
        {
            logger.error(
                "Failure while attempting handleException",
                failure);
        }

        if (retry)
        {
            waitAfterError(current, exception);
        }

        return current;
    }

    void waitAfterError(ConsumerRecord<String, String> message, Exception exception)
    {
        final long retryDelay = kafkaListener.getRetryDelay(exception);
        logger.info("-- waiting {} to retry message: {} --", retryDelay, message);
        try
        {
            Thread.sleep(retryDelay);
        }
        catch (InterruptedException interruptedException)
        {
            throw new RuntimeException(interruptedException);
        }
    }

    private void commit(final ConsumerRecord<String, String> current)
    {
        logger.trace("committing for message: {}", current);
        kafkaConsumer.commitSync();
        logger.trace("finished committing for message: {}", current);
    }
}
