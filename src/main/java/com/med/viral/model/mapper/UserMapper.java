package com.med.viral.model.mapper;

import com.med.viral.model.Admin;
import com.med.viral.model.DTO.*;
import com.med.viral.model.Doctor;
import com.med.viral.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(source = "accountNonLocked", target = "isAccountNonLocked")
    UserDTO UserEntityToDTO(User user);

    User CreateDTOToEntity(CreateUserDTO createUserDTO);

    @Mapping(source = "isAccountNonLocked", target = "isAccountNonLocked")
    User UserDTOtoEntity(UserDTO userDTO);

    void updateUserFromDto(UserDTO dto, @MappingTarget User entity);

    AdminDTO AdminEntityToDTO(Admin admin);

    Admin UserDTOtoEntity(CreateAdminDTO createAdminDTO);

    Doctor UserDTOtoEntity(CreateDoctorDTO createDoctorDTO);
}
