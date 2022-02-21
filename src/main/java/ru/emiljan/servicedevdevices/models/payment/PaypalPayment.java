package ru.emiljan.servicedevdevices.models.payment;

import lombok.*;
import ru.emiljan.servicedevdevices.models.payment.Payment;

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
