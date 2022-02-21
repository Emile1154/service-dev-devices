package ru.emiljan.servicedevdevices.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.emiljan.servicedevdevices.models.Image;
import ru.emiljan.servicedevdevices.models.Role;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.repositories.ImageRepository;
import ru.emiljan.servicedevdevices.repositories.RoleRepository;
import ru.emiljan.servicedevdevices.repositories.UserRepository;
import ru.emiljan.servicedevdevices.specifications.UserSpecifications;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author EM1LJAN
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MailSenderService mailSender;
    private final ImageRepository imageRepository;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder,
                       MailSenderService mailSender,
                       ImageRepository imageRepo) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.mailSender = mailSender;
        this.imageRepository = imageRepo;
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findUserByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }


    private String formatPhone(String phoneNumber){
        if(phoneNumber.isEmpty()){
            return null;
        }
        phoneNumber = phoneNumber.
                replaceAll("\\D", "");
        String AAA, BBB, CC, DD;
        if(phoneNumber.length() == 11){ // X XXX XXX XX XX type
            AAA = phoneNumber.substring(1,4);
            BBB = phoneNumber.substring(4,7);
            CC = phoneNumber.substring(7,9);
            DD = phoneNumber.substring(9,11);

            return "+7"+"("+ AAA +")" + BBB + "-" + CC + "-" + DD;
        }
        // XXX XXX XX XX type
        AAA = phoneNumber.substring(0,3);
        BBB = phoneNumber.substring(3,6);
        CC = phoneNumber.substring(6,8);
        DD = phoneNumber.substring(8,10);
        return "+7"+"("+ AAA +")" + BBB + "-" + CC + "-" + DD;
    }

    public Map<String, String> checkRepeats(User user){
        Map<String,String> errors = new HashMap<>();
        user.setPhone(formatPhone(user.getPhone()));
        if(userRepository.findByPhone(user.getPhone())!=null){
            errors.put("phone","Данный номер телефона уже занят :(");
        }
        if(userRepository.findByNickname(user.getNickname())!=null){
            errors.put("nickname", "Это имя уже занято :(");
        }
        if(userRepository.findByEmail(user.getEmail())!=null){
            errors.put("email", "Данный почтовый адрес уже занят :(");//field
        }
        return errors;
    }

    @Transactional
    public void saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.
                encode(user.getPassword()));
        Role userRole = roleRepository.findByRole("ROLE_USER");
        user.setRoles(new HashSet<>(List.of(userRole)));
        user.setAccountNonLocked(true);
        user.setActive(false);
        user.setActivateCode(UUID.randomUUID().toString());
        Image defaultIcon = imageRepository.getById(1L);

        user.setImage(defaultIcon);

        String message = String.format("Привет, %s! \n" +
                "Если Вы получили данное письмо, то значит регистрация аккаунта на сервисе прошла успешно," +
                " для активации вашего аккаунта перейдите по ссылке: http://localhost:8080/users/activate/%s \n" +
                        "Если проходили регистрацию не Вы, то проигнорируйте это письмо.",
                user.getNickname(),user.getActivateCode());

        mailSender.send(user.getEmail(), "Активация аккаунта", message);
        userRepository.save(user);
        imageRepository.save(defaultIcon);
    }


    public List<User> getUsersByKeyword(List<String> columns, String keyword){
        List<String> columns_bool = columns.stream()
                .filter(s->s.startsWith("a")).collect(Collectors.toList());

        columns.removeIf(columns_bool::contains);
        return userRepository.findAll(UserSpecifications.findByKeyword(keyword,columns)
                .and(UserSpecifications.findTrueBool(columns_bool)));
    }

    public List<User> findAll(){
        return userRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id){
        userRepository.deleteById(id);
    }

    public User findById(Long id){
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public void BanUserById(Long id, boolean param){
        userRepository.setLockById(id, param);
    }

    @Transactional
    public boolean activateUser(String code) {
        User user = userRepository.findUserByActivateCode(code);
        if(user != null){
            user.setActive(true);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
