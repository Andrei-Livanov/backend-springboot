package ru.webapp.springboot.auth.service;

import org.springframework.stereotype.Service;
import ru.webapp.springboot.auth.entity.Activity;
import ru.webapp.springboot.auth.entity.Role;
import ru.webapp.springboot.auth.entity.User;
import ru.webapp.springboot.auth.repository.ActivityRepository;
import ru.webapp.springboot.auth.repository.RoleRepository;
import ru.webapp.springboot.auth.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    public static final String DEFAULT_ROLE = "USER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ActivityRepository activityRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, ActivityRepository activityRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.activityRepository = activityRepository;
    }

    public void register(User user, Activity activity) {

        userRepository.save(user);

        activityRepository.save(activity);
    }

    public boolean userExists(String username, String email) {

        if (userRepository.existsByUsername(username)) {
            return true;
        }

        if (userRepository.existsByEmail(email)) {
            return true;
        }

        return false;
    }

    public Optional<Role> findByName(String role) {
        return roleRepository.findByName(role);
    }

    public Activity saveActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    public Optional<Activity> findActivityByUserId(Long id) {
        return activityRepository.findByUserId(id);
    }

    public Optional<Activity> findActivityByUuid(String uuid) {
        return activityRepository.findByUuid(uuid);
    }

    public int activate(String uuid) {
        return activityRepository.changeActivated(uuid, true);
    }

    public int deactivate(String uuid) {
        return activityRepository.changeActivated(uuid, false);
    }

    public int updatePassword(String password, String email) {
        return userRepository.updatePassword(password, email);
    }

}
