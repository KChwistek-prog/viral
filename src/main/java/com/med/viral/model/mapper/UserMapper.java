package com.med.viral.model.mapper;

import com.med.viral.model.DTO.UserDTO;
import com.med.viral.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface UserMapper {
    @Mapping(source = "accountNonLocked", target = "isAccountNonLocked")
    UserDTO toDTO(User user);

    @Mapping(source = "isAccountNonLocked", target = "isAccountNonLocked")
    User toEntity(UserDTO userDTO);

    void updateUserFromDto(UserDTO dto, @MappingTarget User entity);
}
