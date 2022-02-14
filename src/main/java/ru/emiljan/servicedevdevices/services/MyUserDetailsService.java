package ru.emiljan.servicedevdevices.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.emiljan.servicedevdevices.auth.AccountStatusUserDetailsChecker;
import ru.emiljan.servicedevdevices.models.Role;
import ru.emiljan.servicedevdevices.models.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author EM1LJAN
 */
@Service
public class MyUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Autowired
    public MyUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login){
        User user;
        if(login.contains("@")){
            user = userService.findUserByEmail(login);
        }else{
            user = userService.findUserByNickname(login);
        }
        if(user!=null){
            List<GrantedAuthority> authorities = getUserAuthority(user.getRoles());
            return buildUserForAuthentication(user, authorities);
        }
        throw new BadCredentialsException(String.format("Логин %s неверный",login));
    }


    private List<GrantedAuthority> getUserAuthority(Set<Role> userRoles) {
        Set<GrantedAuthority> roles = new HashSet<>();
        for (Role role : userRoles) {
            roles.add(new SimpleGrantedAuthority(role.getRole()));
        }
        return new ArrayList<>(roles);
    }

    private UserDetails buildUserForAuthentication(User user, List<GrantedAuthority> authorities) {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getNickname(),
                user.getPassword(),user.isActive(), true,true,
                user.isAccountNonLocked(), authorities);
        new AccountStatusUserDetailsChecker().check(userDetails);
        return userDetails;
    }
}