package ru.emiljan.servicedevdevices.models.payment;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author EM1LJAN
 */
@Getter
@Setter
@Builder
@JsonPropertyOrder(alphabetic = true)
@JsonAutoDetect
public class Data {
    private String currency;
    private String merchant_data;
    private String merchant_params;
    private String merchant_sign;
    private String order_id;
    private long ts;
}
