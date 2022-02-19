package ru.emiljan.servicedevdevices.models;

import lombok.*;

import javax.persistence.*;
import java.net.URI;


/**
 * @author EM1LJAN
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("PayPal")
public class PaypalPayment extends Payment {
    @Column(name = "token")
    private String token;

    @Transient
    private URI paypalApproveLink;

    public PaypalPayment(String token, URI paypalApproveLink) {
        this.token = token;
        this.paypalApproveLink = paypalApproveLink;
    }
}
