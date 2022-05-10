package ru.emiljan.servicedevdevices.models.dto;

import lombok.Getter;

@Getter
public class ProjectDTO {
    private Long id;
    private String title;
    private String description;

    public ProjectDTO(Long id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }
}
