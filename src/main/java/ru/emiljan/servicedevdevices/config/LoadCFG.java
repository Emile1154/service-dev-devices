package ru.emiljan.servicedevdevices.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.emiljan.servicedevdevices.models.order.TransferInfo;

import java.util.List;

@Configuration
public class LoadCFG {
    @Value("${upload.order.path}")
    private String orderPath;
    @Value("${upload.project.path}")
    private String projectPath;

    @Value("#{'${upload.order.types}'.split(',')}")
    private List<String> orderTypes;
    @Value("#{'${upload.project.types}'.split(',')}")
    private List<String> projectTypes;

    @Bean("transferOrder")
    public TransferInfo transferOrderBean(){
        return TransferInfo.builder()
                    .id(1)
                    .path(orderPath)
                    .allowedTypes(orderTypes)
                .build();
    }

    @Bean("transferProject")
    public TransferInfo transferProjectBean(){
        return TransferInfo.builder()
                    .id(2)
                    .path(projectPath)
                    .allowedTypes(projectTypes)
                .build();
    }
}
