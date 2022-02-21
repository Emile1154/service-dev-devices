package ru.emiljan.servicedevdevices.models.payment;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

/**
 * @author EM1LJAN
 */
@Getter
@Setter
@JsonAutoDetect
public class Parameters extends SerializeParam{
    private String action;
    private String sign;

    public Parameters(SerializeParam param) {
        super(param.amount, param.data, param.description, param.merchant_id, param.version);
    }
}