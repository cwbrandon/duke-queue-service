package edu.duke.bioinformatics.domain;

import javax.persistence.*;

@Entity
@Table(
    name = "message_listener",
    uniqueConstraints = @UniqueConstraint(columnNames={"queue_fk", "url"})
)
public class MessageListener
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    public String getUrl()
    {
        return url;
    }

    public MessageListener setUrl(String url)
    {
        this.url = url;
        return this;
    }

    @ManyToOne
    @JoinColumn(name = "queue_fk", nullable = false)
    private Queue queue;

    public Queue getQueue()
    {
        return queue;
    }

    public MessageListener setQueue(Queue queue)
    {
        this.queue = queue;
        return this;
    }
}
