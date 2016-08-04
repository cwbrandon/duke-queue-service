package edu.duke.bioinformatics.service;

import edu.duke.bioinformatics.queue.kafka.KafkaSender;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.transaction.Transactional;

@Transactional
@Component
public class KafkaQueueService implements QueueService
{
    @Resource
    private KafkaSender kafkaSender;

    @Override
    public void send(String queue, String message)
    {
        kafkaSender.send(queue, null, message);
    }
}
