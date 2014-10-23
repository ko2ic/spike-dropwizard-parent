package com.github.ko2ic.auth;

import java.util.Optional;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.java8.auth.Authenticator;

import com.github.ko2ic.core.User;

public class ExampleAuthenticator implements Authenticator<BasicCredentials, User> {
    @Override
    public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {
        if ("user".equals(credentials.getUsername()) && "pass".equals(credentials.getPassword())) {
            return Optional.of(new User("ko2ic " + credentials.getUsername()));
        }
        return Optional.empty();
    }
}
