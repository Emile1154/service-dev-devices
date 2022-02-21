package ru.emiljan.servicedevdevices.models.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * @author EM1LJAN
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("VK Pay")
public class VKPayPayment extends Payment {
    @Transient
    private Parameters param;

}
