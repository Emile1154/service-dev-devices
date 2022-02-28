package ru.emiljan.servicedevdevices.models.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransferInfo {
    private String path;
    private List<String> allowedTypes;
}
