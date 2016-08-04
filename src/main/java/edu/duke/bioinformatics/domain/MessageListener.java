package edu.duke.bioinformatics.domain;

import javax.persistence.*;

@Entity
public class MessageListener
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;
}
