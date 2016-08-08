package edu.duke.bioinformatics.service;

import java.net.URL;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface QueueService
{
    public void send(String queue, String message);

    public void registerListeners(String queue, List<String> listeners);

    public void deregisterListeners(String queue, List<String> listeners);
}
