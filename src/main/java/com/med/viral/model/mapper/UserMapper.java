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

    @Mapping(source = "isAccountNonLocked", target = "isAccountNonLocked")
    User UserDTOtoEntity(UserDTO userDTO);

    User createUserDTOToEntity(CreateUserDTO createUserDTO);

    void updateUserFromDto(UserDTO dto, @MappingTarget User entity);

    AdminDTO AdminEntityToDTO(Admin admin);

    Admin adminDTOToEntity(AdminDTO adminDTO);

    Admin UserDTOtoEntity(CreateAdminDTO createAdminDTO);

    Doctor DoctorDTOtoEntity(DoctorDTO DoctorDTO);

    Doctor createDoctorDTOToEntity(CreateDoctorDTO createDoctorDTO);

    DoctorDTO DoctorEntityToDTO(Doctor doctor);


}
