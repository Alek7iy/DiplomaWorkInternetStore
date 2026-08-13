package DTO;

public record UserRegistrationRequest(
        String email,
        String password,
        String name
) {}
