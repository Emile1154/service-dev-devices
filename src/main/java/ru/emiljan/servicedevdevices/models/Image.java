package ru.emiljan.servicedevdevices.models;

import lombok.*;

import javax.persistence.*;

/**
 * @author EM1LJAN
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "icon64")
    private String icon64;

    public Image(String icon64) {
        this.icon64 = icon64;
    }
}
