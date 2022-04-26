package ru.webapp.springboot.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.webapp.springboot.auth.entity.User;
import ru.webapp.springboot.auth.repository.UserRepository;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {

        Optional<User> userOptional = userRepository.findByUsername(usernameOrEmail);

        if (!userOptional.isPresent()) {
            userOptional = userRepository.findByEmail(usernameOrEmail);
        }

        if (!userOptional.isPresent()) {
            throw new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail);
        }

        return new UserDetailsImpl(userOptional.get());
    }

}
