package ru.emiljan.servicedevdevices.models.portfolio;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;
import ru.emiljan.servicedevdevices.models.order.FileInfo;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author EM1LJAN
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    @Length(min = 7, max = 100, message = "Название проекта должно содержать от 7 до 100 символов")
    private String title;

    @Column(name="description")
    @Length(min = 25, max = 500, message = "Описание проекта должно содержать от 25 до 500 символов")
    private String description;

    @Column(name = "rating")
    private int rating;

    @Column(name = "created")
    @CreationTimestamp
    private LocalDateTime created;

    @OneToMany(mappedBy = "project",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Comment> comments;

    @OneToMany(mappedBy = "project", cascade = CascadeType.MERGE,fetch = FetchType.LAZY)
    private List<FileInfo> fileList;

    @Column(name = "preview_id")
    private Long previewId;
}
