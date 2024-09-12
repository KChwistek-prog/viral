package com.med.viral.repository;

import com.med.viral.model.Admin;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends CrudRepository<Admin, Integer> {
    Admin findByUserName(String username);
}
