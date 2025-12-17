package com.rvladimir.service;

import com.rvladimir.service.dto.CreateUserDTO;
import com.rvladimir.service.dto.UserDTO;

public interface UserService {

    UserDTO create(CreateUserDTO createUserDTO);
}
