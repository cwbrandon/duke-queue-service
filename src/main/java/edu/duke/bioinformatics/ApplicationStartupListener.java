package edu.duke.bioinformatics;

import edu.duke.bioinformatics.service.QueueService;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupListener implements ApplicationListener
{
    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartupListener.class);

    @Resource(name = "kafkaQueueService")
    private QueueService queueService;

    @Override
    public void onApplicationEvent(ApplicationEvent event)
    {
        if(event instanceof ApplicationReadyEvent)
        {
            logger.info("-- initializing consumers ---");
            final int count = queueService.startListeners();
            logger.info("-- finished initializing {} consumers --", count);
        }
        else if (event instanceof ContextClosedEvent)
        {
            queueService.stopListeners();
        }
    }
}
