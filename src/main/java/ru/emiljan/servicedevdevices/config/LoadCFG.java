package ru.emiljan.servicedevdevices.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.emiljan.servicedevdevices.models.order.TransferInfo;

import java.util.List;

@Configuration
public class LoadCFG {
    @Value("${upload.order.path}")
    private String path;

    @Value("#{'${upload.order.types}'.split(',')}")
    private List<String> allowedTypes;

    @Bean
    public TransferInfo transferInfoBean(){
        return TransferInfo.builder()
                    .path(path)
                    .allowedTypes(allowedTypes)
                .build();
    }
}
