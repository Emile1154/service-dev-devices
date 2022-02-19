package ru.emiljan.servicedevdevices.models;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Set;

/**
 * @author EM1LJAN
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "nickname", unique = true)
    @Length(min = 5, max = 12,
            message = "Имя должно быть не меньше 5 и не более 12 символов")
    private String nickname;

    @Column(name = "password")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{6,}$",
            message = "Пароль должен содержать хотя бы 1 цифру и 1 символ и быть не менее 6 символов")
    private String password;

    @Column(name = "phone", unique = true)
    @Pattern(regexp = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$",
            message = "Введите действующий номер телефона")
    private String phone;

    @Column(name = "email", unique = true)
    @NotBlank(message = "Введите адрес вашей почты")
    @Email(message = "Введите действующий адрес почты")
    private String email;

    @Column(name = "account_non_locked", columnDefinition = "true")
    private boolean accountNonLocked;

    @Column(name = "active")
    private boolean active;

    @Column(name = "activation_code", unique = true)
    private String activateCode;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @OneToMany(mappedBy="user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CustomOrder> orders;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments;

    @OneToOne
    @JoinColumn(name = "image_id")
    private Image image;

}
