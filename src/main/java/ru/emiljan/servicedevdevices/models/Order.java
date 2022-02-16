package ru.emiljan.servicedevdevices.models;


import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

/**
 * @author EM1LJAN
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "title", unique = true)
    @NotEmpty(message = "*Please provide task")
    private String title;

    @Column(name = "description", unique = true)
    @NotEmpty(message = "*Please provide description")
    private String description;

    @Column(name="is_accepted")
    private boolean isAccepted = false;

    @Column(name="is_completed")
    private boolean isCompleted = false;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;
}
