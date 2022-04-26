package ru.webapp.springboot.auth.controller;

import lombok.extern.java.Log;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.webapp.springboot.auth.entity.Activity;
import ru.webapp.springboot.auth.entity.Role;
import ru.webapp.springboot.auth.entity.User;
import ru.webapp.springboot.auth.exception.RoleNotFoundException;
import ru.webapp.springboot.auth.exception.UserAlreadyActivatedException;
import ru.webapp.springboot.auth.exception.UserOrEmailExistsException;
import ru.webapp.springboot.auth.objects.JsonException;
import ru.webapp.springboot.auth.service.EmailService;
import ru.webapp.springboot.auth.service.UserDetailsImpl;
import ru.webapp.springboot.auth.service.UserDetailsServiceImpl;
import ru.webapp.springboot.auth.service.UserService;
import ru.webapp.springboot.auth.utils.CookieUtils;
import ru.webapp.springboot.auth.utils.JwtUtils;

import javax.validation.Valid;
import java.util.UUID;

import static ru.webapp.springboot.auth.service.UserService.DEFAULT_ROLE;

@RestController
@RequestMapping("/auth")
@Log
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final CookieUtils cookieUtils;
    private final EmailService emailService;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthController(UserService userService,
                          PasswordEncoder encoder, AuthenticationManager authenticationManager,
                          JwtUtils jwtUtils, CookieUtils cookieUtils, EmailService emailService,
                          UserDetailsServiceImpl userDetailsService) {
        this.userService = userService;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.cookieUtils = cookieUtils;
        this.emailService = emailService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> logout() {

        HttpCookie cookie = cookieUtils.deleteJwtCookie();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok().headers(responseHeaders).build();
    }

    @PutMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody User user) {

        if (userService.userExists(user.getUsername(), user.getEmail())) {
            throw new UserOrEmailExistsException("User or email already exists");
        }

        Role userRole = userService.findByName(DEFAULT_ROLE)
                .orElseThrow(() -> new RoleNotFoundException("Default role USER not found"));
        user.getRoles().add(userRole);

        user.setPassword(encoder.encode(user.getPassword()));

        Activity activity = new Activity();
        activity.setUser(user);
        activity.setUuid(UUID.randomUUID().toString());

        userService.register(user, activity);

        emailService.sendActivationEmail(user.getEmail(), user.getUsername(), activity.getUuid());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/activate-account")
    public ResponseEntity<Boolean> activateUser(@RequestBody String uuid) {

        Activity activity = userService.findActivityByUuid(uuid)
                .orElseThrow(() -> new UsernameNotFoundException("Activity not found with uuid: " + uuid));

        if (activity.isActivated())
            throw new UserAlreadyActivatedException("User already activated");

        int updateCount = userService.activate(uuid);

        return ResponseEntity.ok(updateCount == 1);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@Valid @RequestBody User user) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        if (!userDetails.isActivated()) {
            throw new DisabledException("User disabled");
        }

        String jwt = jwtUtils.createAccessToken(userDetails.getUser());

        HttpCookie cookie = cookieUtils.createJwtCookie(jwt);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok().headers(responseHeaders).body(userDetails.getUser());
    }

    @PostMapping("/resend-activate-email")
    public ResponseEntity<?> resendActivateEmail(@RequestBody String usernameOrEmail) {

        UserDetailsImpl user = (UserDetailsImpl) userDetailsService.loadUserByUsername(usernameOrEmail);

        Activity activity = userService.findActivityByUserId(user.getId())
                .orElseThrow(() -> new UsernameNotFoundException("Activity not found with user: " + usernameOrEmail));

        if (activity.isActivated()) {
            throw new UserAlreadyActivatedException("User already activated: " + usernameOrEmail);
        }

        emailService.sendActivationEmail(user.getEmail(), user.getUsername(), activity.getUuid());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-reset-password-email")
    public ResponseEntity<?> sendEmailResetPassword(@RequestBody String email) {

        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(email);

        User user = userDetails.getUser();

        if (user != null) {
            emailService.sendResetPasswordEmail(user.getEmail(), jwtUtils.createEmailResetToken(user));
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/update-password")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Boolean> updatePassword(@RequestBody String password) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();

        int updateCount = userService.updatePassword(encoder.encode(password), user.getEmail());

        return ResponseEntity.ok(updateCount == 1);
    }

    @PostMapping("/auto")
    public ResponseEntity<User> autoLogin() {
        UserDetailsImpl userDetails =
                (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok().body(userDetails.getUser());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<JsonException> handlerException(Exception ex) {

        return new ResponseEntity<>(new JsonException(ex.getClass().getSimpleName()), HttpStatus.BAD_REQUEST);
    }

}
