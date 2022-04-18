package ru.emiljan.servicedevdevices.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.emiljan.servicedevdevices.models.User;
import ru.emiljan.servicedevdevices.repositories.ImageRepository;
import ru.emiljan.servicedevdevices.repositories.RoleRepository;
import ru.emiljan.servicedevdevices.repositories.UserRepository;
import ru.emiljan.servicedevdevices.services.notify.NotifyService;
import ru.emiljan.servicedevdevices.specifications.UserSpecifications;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for {@link ru.emiljan.servicedevdevices.models.User}
 *
 * @author EM1LJAN
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MailSenderService mailSender;
    private final ImageRepository imageRepository;
    private final NotifyService notifyService;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder,
                       MailSenderService mailSender, ImageRepository imageRepo,
                       NotifyService notifyService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.mailSender = mailSender;
        this.imageRepository = imageRepo;
        this.notifyService = notifyService;
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findUserByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    public boolean checkNewNotifies(User user){
       return this.userRepository.checkNotifies(user);
    }

    /**
     * this method formats the phone number entered by the user to RU standart
     * @param phoneNumber
     * @return formatted phone number
     */
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


    /**
     * this method checks if the entered data is busy: phone number, nickname and email
     * @param user
     * @return error map
     */
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

    /**
     * add new user to database
     * @param user
     */
    @Transactional
    public void saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(new HashSet<>(List.of(roleRepository.findByRole("ROLE_USER"))));
        user.setAccountNonLocked(true);
        user.setActive(false);
        user.setActivateCode(UUID.randomUUID().toString());
        user.setImage(this.imageRepository.getById(1L));

        String message = String.format("Привет, %s! \n" +
                "Если Вы получили данное письмо, то значит регистрация аккаунта на сервисе прошла успешно," +
                " для активации вашего аккаунта перейдите по ссылке: http://localhost:8080/users/activate/%s \n" +
                        "Если проходили регистрацию не Вы, то проигнорируйте это письмо.",
                user.getNickname(),user.getActivateCode());
        mailSender.send(user.getEmail(), "Активация аккаунта", message);
        userRepository.save(user);
    }

    /**
     * this method filters users by columns and keyword
     * @param columns
     * @param keyword
     * @return list of users matching the specification
     */
    public List<User> getUsersByKeyword(List<String> columns, String keyword){
        List<String> columns_bool = columns.stream() //boolean columns name accountNonLocked and active.start with a
                .filter(s->s.startsWith("a")).collect(Collectors.toList());

        columns.removeIf(columns_bool::contains);
        return userRepository.findAll(UserSpecifications.findByKeyword(keyword,columns)
                .and(UserSpecifications.findTrueBool(columns_bool)));
    }

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public List<User> findAllByRole(Long id){
        return userRepository.findAllByRoles(id);
    }

    @Transactional
    public void deleteById(Long id){
        userRepository.deleteById(id);
    }

    public User findById(Long id){
        return userRepository.findById(id).orElse(null);
    }

    /**
     * ban/unban method
     * @param id user id
     * @param param true - unban, false - ban
     */
    @Transactional
    public void banUserById(Long id, boolean param){
        this.userRepository.setLockById(id, param);
    }

    @Transactional
    public void update(User user){
        this.userRepository.save(user);
    }

    /**
     * this method activates the user account by email
     * @param code
     * @return true - activation success, false - activation failed
     */
    public boolean activateUser(String code) {
        User user = userRepository.findUserByActivateCode(code);
        if(user != null){
            user.setActive(true);
            user.setActivateCode(null);
            notifyService.createNotify("welcome",user);
            return true;
        }
        return false;
    }
}
