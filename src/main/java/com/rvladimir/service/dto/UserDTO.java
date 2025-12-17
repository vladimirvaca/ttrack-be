package com.rvladimir.service.dto;

import com.rvladimir.domain.User;

import io.micronaut.serde.annotation.Serdeable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Serdeable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @NotNull
    private String name;

    @NotNull
    private String lastname;

    @NotNull
    private LocalDate dateBirth;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String password;

    @NotNull
    private User.Role role;
}
