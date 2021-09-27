package com.github.eybv.blog.engine.controller;

import com.github.eybv.blog.core.annotation.*;
import com.github.eybv.blog.core.request.RequestMethod;
import com.github.eybv.blog.engine.dto.*;
import com.github.eybv.blog.engine.service.PasswordResetService;
import com.github.eybv.blog.engine.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Log
@Component
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;

    @RequestMapping(method = RequestMethod.POST, path = "/user/login")
    public UserWithToken login(@RequestBody LoginRequest request) {
        return userService.login(request.getUsername(), request.getPassword());
    }

    @RequestMapping(method = RequestMethod.POST, path = "/user/register")
    public UserWithToken register(@RequestBody RegistrationRequest request) {
        return userService.register(request.getUsername(), request.getPassword());
    }

    @RequestMapping(method = RequestMethod.POST, path = "/user/password/reset")
    public void passwordReset(@RequestBody PasswordResetRequest request) {
        final var code = passwordResetService.generateCode(request.getUsername());
        log.info("PASSWORD RESET CODE FOR USER %s: %s".formatted(request.getUsername(), code));
    }

    @RequestMapping(method = RequestMethod.POST, path = "/user/password/reset/confirm")
    public UserWithToken confirmPasswordReset(@RequestBody ConfirmPasswordResetRequest request) {
        return passwordResetService.changePassword(request.getUsername(), request.getCode(), request.getPassword());
    }

    @HasRole("ROLE_ADMIN")
    @RequestMapping(method = RequestMethod.PATCH, path = "/user/{id}/disable")
    public void disableUser(@PathVariable("id") Long userId) {
        userService.disableUser(userId);
    }

    @HasRole("ROLE_ADMIN")
    @RequestMapping(method = RequestMethod.PATCH, path = "/user/{id}/enable")
    public void enableUser(@PathVariable("id") Long userId) {
        userService.enableUser(userId);
    }

    @HasRole("ROLE_ADMIN")
    @RequestMapping(method = RequestMethod.POST, path = "/user/{id}/role/{role}")
    public UserData addUserRole(@PathVariable("id") Long userId, @PathVariable("role") String role) {
        return userService.addUserRole(userId, role);
    }

    @HasRole("ROLE_ADMIN")
    @RequestMapping(method = RequestMethod.DELETE, path = "/user/{id}/role/{role}")
    public UserData removeUserRole(@PathVariable("id") Long userId, @PathVariable("role") String role) {
        return userService.removeUserRole(userId, role);
    }

}
