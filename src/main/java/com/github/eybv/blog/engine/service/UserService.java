package com.github.eybv.blog.engine.service;

import com.github.eybv.blog.core.annotation.Component;
import com.github.eybv.blog.engine.dto.UserData;
import com.github.eybv.blog.engine.dto.UserWithToken;
import com.github.eybv.blog.engine.exception.*;
import com.github.eybv.blog.engine.model.User;
import com.github.eybv.blog.engine.model.UserRole;
import com.github.eybv.blog.engine.model.UserWithPassword;
import com.github.eybv.blog.engine.repository.UserRepository;
import com.github.eybv.blog.engine.repository.UserRoleRepository;
import com.github.eybv.blog.engine.repository.WrongCredentialsRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final WrongCredentialsRepository wrongCredentialsRepository;

    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public Optional<UserData> findById(long id) {
        return userRepository.findById(id)
                .map(UserWithPassword::getUser)
                .map(u -> new UserData(u.getId(), u.getUsername(), getUserRoleList(u.getId()), u.isActive()));
    }

    public Optional<UserData> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserWithPassword::getUser)
                .map(u -> new UserData(u.getId(), u.getUsername(), getUserRoleList(u.getId()), u.isActive()));
    }

    public UserWithToken login(String username, String password) {
        final var user = userRepository.findByUsername(username)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                .filter(u -> u.getUser().isActive())
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"))
                .getUser();

        final var roles = getUserRoleList(user.getId());
        final var token = tokenService.generateToken(user.getId()).getValue();

        return new UserWithToken(user.getId(), user.getUsername(), roles, token);
    }

    public UserWithToken register(String username, String password) {
        username = username.toLowerCase(Locale.ROOT).trim();
        password = password.trim();

        if (wrongCredentialsRepository.containsUsername(username) ||
                wrongCredentialsRepository.containsPassword(password)) {
            throw new WrongCredentialsException("Wrong credentials");
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new ResourceAlreadyExists("User %s already exists".formatted(username));
        }

        var user = new User(0, username, true);
        final var userWithPassword = new UserWithPassword(user, passwordEncoder.encode(password));
        user = userRepository.save(userWithPassword);

        final var roles = userRoleRepository.assignToUser(UserRole.ROLE_USER, user.getId())
                .stream()
                .map(Enum::name)
                .collect(Collectors.toList());
        final var token = tokenService.generateToken(user.getId()).getValue();

        return new UserWithToken(user.getId(), user.getUsername(), roles, token);
    }

    public UserWithToken changeUserPassword(long userId, String password) {
        final var userWithPassword = userRepository.findById(userId)
                .orElseThrow(() -> newUserNotFoundException(userId));
        if (wrongCredentialsRepository.containsPassword(password.trim())) {
            throw new WrongCredentialsException("Wrong credentials");
        }
        userWithPassword.setPassword(passwordEncoder.encode(password.trim()));
        final var user = userRepository.save(userWithPassword);
        final var token = tokenService.generateToken(user.getId()).getValue();
        final var roles = getUserRoleList(user.getId());
        return new UserWithToken(user.getId(), user.getUsername(), roles, token);
    }

    public void disableUser(long userId) {
        final var userWithPassword = userRepository.findById(userId)
                .orElseThrow(() -> newUserNotFoundException(userId));
        userWithPassword.getUser().setActive(false);
        userRepository.save(userWithPassword);
    }

    public void enableUser(long userId) {
        final var userWithPassword = userRepository.findById(userId)
                .orElseThrow(() -> newUserNotFoundException(userId));
        userWithPassword.getUser().setActive(true);
        userRepository.save(userWithPassword);
    }

    public UserData addUserRole(long userId, String role) {
        final var userRole = UserRole.valueOf("ROLE_" + role.toUpperCase(Locale.ROOT));
        final var user = userRepository.findById(userId)
                .orElseThrow(() -> newUserNotFoundException(userId))
                .getUser();
        final var roles = userRoleRepository.assignToUser(userRole, userId)
                .stream()
                .map(Enum::name)
                .collect(Collectors.toList());
        return new UserData(user.getId(), user.getUsername(), roles, user.isActive());
    }

    public UserData removeUserRole(long userId, String role) {
        final var userRole = UserRole.valueOf("ROLE_" + role.toUpperCase(Locale.ROOT));

        if (userRole.equals(UserRole.ROLE_USER)) {
            throw new IllegalOperationException("Unable to delete role");
        }

        final var user = userRepository.findById(userId)
                .orElseThrow(() -> newUserNotFoundException(userId))
                .getUser();
        final var roles = userRoleRepository.removeAssignToUser(userRole, userId)
                .stream()
                .map(Enum::name)
                .collect(Collectors.toList());
        return new UserData(user.getId(), user.getUsername(), roles, user.isActive());
    }

    private List<String> getUserRoleList(long userId) {
        return userRoleRepository.findAllAssignedToUser(userId)
                .stream()
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    private RuntimeException newUserNotFoundException(long userId) {
        final var error = "User with ID %s not found".formatted(userId);
        return new ResourceNotFoundException(error);
    }

}
