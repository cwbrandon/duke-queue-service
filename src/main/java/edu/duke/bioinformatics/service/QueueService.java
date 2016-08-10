package edu.duke.bioinformatics.service;

import java.net.URL;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface QueueService
{
    void send(String queue, String message);

    void registerListeners(String queue, List<String> listeners);

    void deregisterListeners(String queue, List<String> listeners);

    int startListeners();

    void stopListeners();
}
