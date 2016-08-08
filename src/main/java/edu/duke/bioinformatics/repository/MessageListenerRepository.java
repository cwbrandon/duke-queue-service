package edu.duke.bioinformatics.repository;

import edu.duke.bioinformatics.domain.MessageListener;
import edu.duke.bioinformatics.domain.Queue;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;

public interface MessageListenerRepository extends JpaRepository<MessageListener, Integer>
{
    public List<MessageListener> findByQueue(Queue queue);

    public MessageListener findByQueueAndUrl(Queue queue, String url);
}
