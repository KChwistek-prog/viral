package com.med.viral.service;

import com.med.viral.model.Admin;
import com.med.viral.model.Role;
import com.med.viral.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByUserName(username);
        if (admin == null) {
            throw new UsernameNotFoundException("Invalid username or password");
        }
        return new User(admin.getUserName(), admin.getPassword(), getAuthority(admin.getRoles()));
    }

    private List<SimpleGrantedAuthority> getAuthority(Set<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRole().getValue())).collect(Collectors.toUnmodifiableList());
    }
}