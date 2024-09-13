package com.med.viral.controller;

import com.med.viral.model.LoginDto;
import com.med.viral.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<String> adminLogin(@RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(adminService.login(loginDto));
    }
}