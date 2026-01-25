package com.rvladimir.service;

import com.rvladimir.service.dto.LoginDTO;

public interface AuthService {

    String login(LoginDTO loginDTO);

    String refreshLogin(String email);
}
