package edu.duke.bioinformatics.queue.handler;

import edu.duke.bioinformatics.queue.MessageHandler;

public class UrlMessageHandler<T> implements MessageHandler<T>
{
    @Override
    public void onMessage(T message)
    {
        System.out.println("should post to URL===: " + message);
        // TODO post to the message listener URL
    }

    @Override
    public boolean retryOnFailure(T message, Exception exception)
    {
        // - always retry for now
        return true;
    }
}
