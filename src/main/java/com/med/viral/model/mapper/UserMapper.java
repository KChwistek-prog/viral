package com.med.viral.model.mapper;

import com.med.viral.model.DTO.UserDTO;
import com.med.viral.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserDTO userDTO);
    UserDTO toDTO(User user);
}
