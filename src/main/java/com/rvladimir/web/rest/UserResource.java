package com.rvladimir.web.rest;

import com.rvladimir.service.UserService;
import com.rvladimir.service.dto.CreateUserDTO;
import com.rvladimir.service.dto.UserDTO;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "User")
@Controller("/user")
@Slf4j
@AllArgsConstructor
public class UserResource {

    private final UserService userService;

    @ApiResponse(responseCode = "201", description = "User created successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid user data.")
    @Operation(summary = "Create a new user", description = "Creates a new user in the system.")
    @Post(uri = "/create")
    public HttpResponse<UserDTO> createUser(@Body @Valid CreateUserDTO createUserDto) {
        log.info("Creating user with email: {}", createUserDto.getEmail());
        UserDTO userDTO = userService.create(createUserDto);
        if (userDTO == null) {
            log.warn("User creation failed for email: {}", createUserDto.getEmail());
        } else {
            log.info(
                "User created successfully: id={}, email={}",
                userDTO.getId(),
                userDTO.getEmail()
            );
        }
        return HttpResponse.created(userDTO);
    }

    @Get()
    public HttpResponse<String> getUser() {
        log.info("getUser endpoint called");
        return HttpResponse.ok().body("{\"message\": \"All good!\"}");
    }

}
