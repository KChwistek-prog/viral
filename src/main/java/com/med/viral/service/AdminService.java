package com.med.viral.service;

import com.med.viral.model.Admin;

import java.util.List;

public interface AdminService {
    Admin saveAdmin(Admin admin);

    List<Admin> fetchAdminList();

    Admin updateAdmin(Admin admin, Integer id);

    void deleteAdminById(Integer adminId);
}
