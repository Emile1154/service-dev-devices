package ru.emiljan.servicedevdevices.models;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author EM1LJAN
 */
@Getter
@Setter
@Entity
@Table(name = "payments")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "pay_method", discriminatorType = DiscriminatorType.STRING)
public abstract class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    protected Long id;

    @CreationTimestamp
    @Column(name = "created")
    protected LocalDateTime created;

    @UpdateTimestamp
    @Column(name = "updated")
    protected LocalDateTime updated;

    @Enumerated(EnumType.STRING)
    @Column(name = "pay_status")
    protected PayStatus payStatus;

    @ManyToOne
    @JoinColumn(name = "user_id")
    protected User user;

    @ManyToOne
    @JoinColumn(name = "order_id")
    protected CustomOrder order;
}
