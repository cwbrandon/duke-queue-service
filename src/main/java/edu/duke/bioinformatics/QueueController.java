package edu.duke.bioinformatics;

import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static edu.duke.bioinformatics.Constants.API_PATH;
import static edu.duke.bioinformatics.Constants.PRODUCE_PATH;

@RestController
@RequestMapping(value = API_PATH, consumes = MediaType.APPLICATION_JSON_VALUE)
public class QueueController
{
    @ApiOperation(value = "Produces a message to the queue")
    @RequestMapping(value = PRODUCE_PATH, method = RequestMethod.POST)
    public void produce(@PathVariable String queue, @RequestBody String message)
    {
        System.out.println("the queue===" + queue);
        System.out.println("the message===: " + message);
    }
}
