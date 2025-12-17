package com.rvladimir.web.rest;

import com.rvladimir.service.UserService;
import com.rvladimir.service.dto.CreateUserDTO;
import com.rvladimir.service.dto.UserDTO;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.extern.slf4j.Slf4j;

@Tag(name = "User")
@Controller("/user")
@Slf4j
public class UserResource {

    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @ApiResponse(responseCode = "201", description = "User created successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid user data.")
    @Post(uri = "/create", produces = "application/json", consumes = "application/json")
    public HttpResponse<UserDTO> createUser(@Body CreateUserDTO createUserDto) {
        log.info("Creating user with username: {}", createUserDto.getEmail());
        UserDTO userDTO = userService.create(createUserDto);
        return HttpResponse.created(userDTO);
    }

}
