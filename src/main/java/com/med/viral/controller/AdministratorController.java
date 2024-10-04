package com.med.viral.controller;

import com.med.viral.exceptions.AppointmentNotFoundException;
import com.med.viral.exceptions.UserNotFoundException;
import com.med.viral.model.Action;
import com.med.viral.model.ActionType;
import com.med.viral.model.Admin;
import com.med.viral.model.DTO.UserDTO;
import com.med.viral.model.User;
import com.med.viral.model.mapper.UserMapper;
import com.med.viral.model.security.Role;
import com.med.viral.service.ActionService;
import com.med.viral.service.AppointmentService;
import com.med.viral.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/administrator")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdministratorController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final AppointmentService appointmentService;
    private final ActionService actionService;
    private final Clock clock;

    @DeleteMapping("/deleteAccount/{id}")
    public ResponseEntity<Void> deleteUserAccount(@PathVariable("id") Integer userId) throws Exception {
        var loggedAdmin = (Admin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var userToDelete = userService.getById(userId);
        Action action = Action.builder()
                .actionType(ActionType.DELETE_ACCOUNT)
                .createdBy(loggedAdmin.getId())
                .createdDate(LocalDateTime.now(clock))
                .fieldName("User account")
                .oldVersion(userToDelete.username())
                .newVersion("Deleted")
                .build();
        if (!userToDelete.role().equals(Role.ADMIN)) {
            userService.deleteUser(userToDelete);
            actionService.saveAction(action);
        } else throw new Exception("Can't delete administrator account");
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/updateUser/{userId}")
    public ResponseEntity<UserDTO> editUserAccount(@PathVariable("userId") Integer id, @RequestBody UserDTO userDTO) throws UserNotFoundException {
        var loggedAdmin = (Admin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = userService.getById(id);
        var action = Action.builder()
                .actionType(ActionType.MODIFY_ACCOUNT)
                .createdBy(loggedAdmin.getId())
                .createdDate(LocalDateTime.now(clock))
                .fieldName(userDTO.toString())
                .oldVersion("false")
                .newVersion(userDTO.toString())
                .build();
        if (!user.role().equals(Role.ADMIN)) {
            userMapper.updateUserFromDto(userDTO, userMapper.UserDTOtoEntity(userDTO));
            userService.saveUser(userDTO);
            actionService.saveAction(action);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/addAccount")
    public ResponseEntity<UserDTO> addAccount(@RequestBody UserDTO userDTO) {
        var user = userMapper.UserDTOtoEntity(userDTO);
        user.setAccountNonLocked(true);
        var loggedAdmin = (Admin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var action = Action.builder()
                .actionType(ActionType.ADD_ACCOUNT)
                .createdBy(loggedAdmin.getId())
                .createdDate(LocalDateTime.now(clock))
                .fieldName("New account")
                .oldVersion("false")
                .newVersion("true")
                .build();
        actionService.saveAction(action);
        return ResponseEntity.ok(userService.saveUser(userMapper.UserEntityToDTO(user)));
    }

    @PostMapping("/updateAppointmentStatus/{id}")
    public ResponseEntity<Void> changeAppointmentStatus(@RequestBody String status, @PathVariable("id") Long appointmentID) throws AppointmentNotFoundException {
        var loggedAdmin = (Admin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var appointmentToChange = appointmentService.getAppointmentById(appointmentID);
        appointmentService.changeAppointmentStatus(appointmentToChange, status);
        Action action = Action.builder()
                .actionType(ActionType.MODIFY_ACCOUNT)
                .createdBy(loggedAdmin.getId())
                .createdDate(LocalDateTime.now(clock))
                .fieldName("Appointment status")
                .oldVersion(appointmentToChange.getStatus().toString())
                .newVersion(status)
                .build();
        actionService.saveAction(action);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/changeAccountStatus/{name}")
    public ResponseEntity<Void> changeAccountStatus(@PathVariable("name") String name) throws UserNotFoundException {
        var loggedAdmin = (Admin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var getUser = userService.getUserByUsername(name);
        User user = userMapper.UserDTOtoEntity(getUser);
        user.setAccountNonLocked(true);
        userService.saveUser(userMapper.UserEntityToDTO(user));

        Action action = Action.builder()
                .actionType(ActionType.MODIFY_ACCOUNT)
                .createdBy(loggedAdmin.getId())
                .createdDate(LocalDateTime.now(clock))
                .fieldName("IsAccountNonLocked")
                .oldVersion(String.valueOf(getUser.isAccountNonLocked()))
                .newVersion(String.valueOf(user.isAccountNonLocked()))
                .build();
        actionService.saveAction(action);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/cancelAppointment/{id}")
    public ResponseEntity<Void> cancelAppointment(@PathVariable("id") Long appointmentId) throws UserNotFoundException, AppointmentNotFoundException {
        var loggedAdmin = (Admin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       var appointment = appointmentService.getAppointmentById(appointmentId);
        Action action = Action.builder()
                .actionType(ActionType.MODIFY_ACCOUNT)
                .createdBy(loggedAdmin.getId())
                .createdDate(LocalDateTime.now(clock))
                .fieldName("IsAccountNonLocked")
                .oldVersion(appointment.getStatus().toString())
                .newVersion("CANCELLED")
                .build();
        actionService.saveAction(action);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/userList")
    public List<UserDTO> listAllUsers() {
        return userService.getAllNonAdminUsers();
    }

    @GetMapping("/getActions")
    public List<Action> listAllActions() {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return actionService.getActions(loggedUser.getId());
    }


}
