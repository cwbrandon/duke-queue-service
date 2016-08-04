package edu.duke.bioinformatics.service;

public interface QueueService
{
    public void send(String queue, String message);
}
