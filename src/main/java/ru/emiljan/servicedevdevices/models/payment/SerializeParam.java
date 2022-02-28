package ru.emiljan.servicedevdevices.models.payment;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author EM1LJAN
 */
@Builder
@Setter
@Getter
@JsonPropertyOrder(alphabetic = true)
@JsonAutoDetect
public class SerializeParam {
    protected Double amount;
    protected Data data;
    protected String description;
    protected int merchant_id;
    protected int version;
}
