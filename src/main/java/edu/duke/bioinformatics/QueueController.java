package edu.duke.bioinformatics;

import static edu.duke.bioinformatics.Constants.*;

import com.wordnik.swagger.annotations.ApiOperation;
import edu.duke.bioinformatics.service.QueueService;
import java.net.URL;
import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
            logger.debug("-- sending message={} to: {} --", message, queue);
        }

        queueService.send(queue, message);
    }

    @ApiOperation(value = "Registers URL(s) to which messages are posted when consumed")
    @RequestMapping(value = REGISTER_PATH, method = RequestMethod.POST)
    public void registerListeners(@PathVariable String queue, @RequestBody List<String> listeners)
    {
        logger.info("-- registering listeners={} to: {} --", listeners, queue);

        queueService.registerListeners(queue, listeners);
    }

    @ApiOperation(value = "De-registers URL(s) to which messages are posted to")
    @RequestMapping(value = DEREGISTER_PATH, method = RequestMethod.POST)
    public void deregisterListeners(@PathVariable String queue, @RequestBody List<String> listeners)
    {
        logger.info("-- de-registering listeners={} from: {} --" , listeners, queue);

        queueService.deregisterListeners(queue, listeners);
    }
}
