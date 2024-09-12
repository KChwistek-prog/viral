package com.med.viral.service;

import com.med.viral.model.Admin;
import com.med.viral.model.LoginDto;
import com.med.viral.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class AdminServiceImpl implements AdminService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private JwtTokenService jwtTokenService;
    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Override
    public String login(LoginDto loginDto) {
        final Authentication authentication = authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.username(),
                        loginDto.password()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final Admin admin = adminRepository.findByUserName(loginDto.username());

        return jwtTokenService.generateToken(admin.getUserName(), admin.getPassword());
    }

    @Override
    public Admin saveAdmin(Admin admin) {
        return null;
    }


    @Override
    public List<Admin> fetchAdminList() {
        return List.of();
    }

    @Override
    public Admin updateAdmin(Admin admin, Integer id) {
        return null;
    }

    @Override
    public void deleteAdminById(Integer adminId) {

    }
}
