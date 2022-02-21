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
@Table(name = "roles")
public class Role{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String role;
}
