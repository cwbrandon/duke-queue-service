package edu.duke.bioinformatics.queue;

public interface MessageHandler<T>
{
    void onMessage(T message);

    boolean retryOnFailure(T message, Exception exception);
}
