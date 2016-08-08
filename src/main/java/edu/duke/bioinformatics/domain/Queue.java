package edu.duke.bioinformatics.domain;

import javax.persistence.*;

@Entity
@Table(
    name = "queue",
    uniqueConstraints = @UniqueConstraint(columnNames={"name"})
)
public class Queue
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private String name;

    public String getName()
    {
        return name;
    }

    public Queue setName(String name)
    {
        this.name = name;
        return this;
    }
}

