package com.med.viral.model.mapper;

import com.med.viral.model.DTO.UserDTO;
import com.med.viral.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "accountNonLocked", target = "isAccountNonLocked")
    UserDTO toDTO(User user);

    @Mapping(source = "isAccountNonLocked", target = "isAccountNonLocked")
    User toEntity(UserDTO userDTO);
}
