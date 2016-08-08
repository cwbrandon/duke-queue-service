package edu.duke.bioinformatics.repository;

import edu.duke.bioinformatics.domain.MessageListener;
import edu.duke.bioinformatics.domain.Queue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;

public interface QueueRepository extends JpaRepository<Queue, Integer>
{
    public Queue findByName(String name);
}
