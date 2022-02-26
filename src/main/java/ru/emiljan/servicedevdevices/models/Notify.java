package ru.emiljan.servicedevdevices.models;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "notifies")
public class Notify {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "message")
    private String message;

    @CreationTimestamp
    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "is_read")
    private boolean isRead;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;
}
