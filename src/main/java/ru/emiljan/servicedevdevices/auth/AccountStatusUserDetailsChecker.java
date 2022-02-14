package ru.emiljan.servicedevdevices.auth;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;

/**
 * @author EM1LJAN
 */
@Component
public class AccountStatusUserDetailsChecker implements UserDetailsChecker {

    @Override
    public void check(UserDetails user) {
        if (!user.isAccountNonLocked()) {
            throw new LockedException(String.format("Аккаунт %s заблокирован", user.getUsername()));
        }
        if (!user.isEnabled()) {
            throw new DisabledException(String.format("Аккаунт %s не активирован, на вашу почту " +
                    "было отправлено письмо с ссылкой на активацию", user.getUsername()));
        }
    }
}
