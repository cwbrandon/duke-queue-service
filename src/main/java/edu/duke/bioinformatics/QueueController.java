package edu.duke.bioinformatics;

import com.wordnik.swagger.annotations.ApiOperation;
import edu.duke.bioinformatics.service.QueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URL;
import java.util.List;

import static edu.duke.bioinformatics.Constants.API_PATH;
import static edu.duke.bioinformatics.Constants.PRODUCE_PATH;

@RestController
@RequestMapping(value = API_PATH, consumes = MediaType.APPLICATION_JSON_VALUE)
public class QueueController
{
    private static final Logger logger = LoggerFactory.getLogger(QueueController.class);

    @Resource(name = "kafkaQueueService")
    private QueueService queueService;

    @ApiOperation(value = "Sends a message to the given queue")
    @RequestMapping(value = PRODUCE_PATH, method = RequestMethod.POST)
    public void send(@PathVariable String queue, @RequestBody String message)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("-- sending message={} to queue={} --", message, queue);
        }

        queueService.send(queue, message);
    }

    @ApiOperation(value = "Registers URL(s) to which messages are posted when consumed")
    public void registerListeners(@PathVariable String queue, @RequestBody List<URL> listeners)
    {
        logger.info("-- registering listeners={} to queue={} --");
    }

    @ApiOperation(value = "De-registers URL(s) to which messages are posted to")
    public void deregisterListeners(@PathVariable String queue, @RequestBody List<URL> listeners)
    {
        logger.info("-- de-registering listeners={} from queue={} --" , listeners, queue);
    }
}
