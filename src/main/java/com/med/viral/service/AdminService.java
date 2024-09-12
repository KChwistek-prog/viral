package com.med.viral.service;

import com.med.viral.model.Admin;
import com.med.viral.model.LoginDto;

import java.util.List;

public interface AdminService {
    String login(LoginDto loginDto);

    Admin saveAdmin(Admin admin);

    List<Admin> fetchAdminList();

    Admin updateAdmin(Admin admin, Integer id);

    void deleteAdminById(Integer adminId);
}