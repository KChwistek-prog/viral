package com.med.viral.model.mapper;

import com.med.viral.model.Admin;
import com.med.viral.model.DTO.*;
import com.med.viral.model.Doctor;
import com.med.viral.model.Patient;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(source = "accountNonLocked", target = "isAccountNonLocked")
    PatientDTO PatientEntityToDTO(Patient patient);

    @Mapping(source = "isAccountNonLocked", target = "accountNonLocked")
    Patient PatientDTOtoEntity(PatientDTO patientDTO);

    Patient createUserDTOToEntity(CreatePatientDTO createPatientDTO);

    @Mapping(target = "id", ignore = true)
    void updateUserFromDto(PatientDTO dto, @MappingTarget Patient entity);

    AdminDTO AdminEntityToDTO(Admin admin);

    Admin adminDTOToEntity(AdminDTO adminDTO);

    Admin UserDTOtoEntity(CreateAdminDTO createAdminDTO);

    Doctor DoctorDTOtoEntity(DoctorDTO DoctorDTO);

    Doctor createDoctorDTOToEntity(CreateDoctorDTO createDoctorDTO);

    DoctorDTO DoctorEntityToDTO(Doctor doctor);


}
