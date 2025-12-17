package com.rvladimir.service;

import com.rvladimir.service.dto.JwtDTO;
import com.rvladimir.service.dto.LoginDTO;

public interface AuthService {

    JwtDTO login(LoginDTO loginDTO);
}
