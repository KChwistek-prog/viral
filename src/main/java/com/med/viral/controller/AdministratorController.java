package com.med.viral.controller;

import com.med.viral.exceptions.UserNotFoundException;
import com.med.viral.model.Action;
import com.med.viral.model.ActionType;
import com.med.viral.model.AppointmentStatus;
import com.med.viral.model.DTO.UserDTO;
import com.med.viral.model.User;
import com.med.viral.model.mapper.UserMapper;
import com.med.viral.model.security.Role;
import com.med.viral.repository.ActionRepository;
import com.med.viral.repository.AppointmentRepository;
import com.med.viral.repository.UserRepository;
import com.med.viral.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class AdministratorController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final AppointmentRepository appointmentRepository;
    private final ActionRepository actionRepository;
    private final Clock clock;

    @Autowired
    public AdministratorController(UserService userService, UserMapper userMapper, AppointmentRepository appointmentRepository, ActionRepository actionRepository, Clock clock) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.appointmentRepository = appointmentRepository;
        this.actionRepository = actionRepository;
        this.clock = clock;
    }

    @DeleteMapping("/deleteAccount/{id}")
    public void deleteUserAccount(@PathVariable("id") Integer userId) throws Exception {
        User loggedAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var userToDelete = userService.getById(userId);
        Action action = Action.builder()
                .actionType(ActionType.DELETE_ACCOUNT)
                .createdBy(loggedAdmin)
                .createdDate(LocalDateTime.now(clock))
                .fieldName("User account")
                .oldVersion(userToDelete.toString())
                .newVersion("Deleted")
                .build();
        if (!userToDelete.role().equals(Role.ADMIN)) {
            userService.deleteUser(userToDelete);
            actionRepository.save(action);
        } else throw new Exception("Can't delete administrator account");
    }

    @PatchMapping("/updateUser/{userId}")
    public ResponseEntity<UserDTO> editUserAccount(@PathVariable("userId") Integer id, @RequestBody UserDTO userDTO) throws UserNotFoundException {
        var loggedAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = userService.getById(id);
        var action = Action.builder()
                .actionType(ActionType.MODIFY_ACCOUNT)
                .createdBy(loggedAdmin)
                .createdDate(LocalDateTime.now(clock))
                .fieldName(userDTO.toString())
                .oldVersion("false")
                .newVersion(userDTO.toString())
                .build();
        if (!user.role().equals(Role.ADMIN)) {
            userMapper.updateUserFromDto(userDTO, userMapper.toEntity(userDTO));
            userService.saveUser(userDTO);
            actionRepository.save(action);
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/addAccount")
    public ResponseEntity<UserDTO> addAccount(@RequestBody UserDTO userDTO) {
        var user = userMapper.toEntity(userDTO);
        user.setAccountNonLocked(true);
        var loggedAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var action = Action.builder()
                .actionType(ActionType.ADD_ACCOUNT)
                .createdBy(loggedAdmin)
                .createdDate(LocalDateTime.now(clock))
                .fieldName("New account")
                .oldVersion("false")
                .newVersion("true")
                .build();
        actionRepository.save(action);
        return ResponseEntity.ok(userService.saveUser(userMapper.toDTO(user)));
    }

    @PostMapping("/updateAppointmentStatus/{id}")
    public void changeAppointmentStatus(@RequestBody String status, @PathVariable("id") Long appointmentID) {
        User loggedAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var appointmentToChange = appointmentRepository.findById(appointmentID).orElseThrow();

        Action action = Action.builder()
                .actionType(ActionType.MODIFY_ACCOUNT)
                .createdBy(loggedAdmin)
                .createdDate(LocalDateTime.now(clock))
                .fieldName("Appointment status")
                .oldVersion(appointmentToChange.getStatus().toString())
                .newVersion(status)
                .build();
        if (status.equals(AppointmentStatus.OPEN.getValue())) {
            appointmentToChange.setStatus(AppointmentStatus.OPEN);
            actionRepository.save(action);
        } else if (status.equals(AppointmentStatus.CLOSED.getValue())) {
            appointmentToChange.setStatus(AppointmentStatus.CLOSED);
            actionRepository.save(action);
        } else if (status.equals(AppointmentStatus.CANCELLED.getValue())) {
            appointmentToChange.setStatus(AppointmentStatus.CANCELLED);
            actionRepository.save(action);
        }
    }

    @PostMapping("/changeAccountStatus/{name}")
    public void changeAccountStatus(@PathVariable("name") String name) throws UserNotFoundException {
        User loggedAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var getUser = userService.getByUsername(name);
        User user = userMapper.toEntity(getUser);
        user.setAccountNonLocked(true);
        userService.saveUser(userMapper.toDTO(user));

        Action action = Action.builder()
                .actionType(ActionType.MODIFY_ACCOUNT)
                .createdBy(loggedAdmin)
                .createdDate(LocalDateTime.now(clock))
                .fieldName("IsAccountNonLocked")
                .oldVersion(String.valueOf(getUser.isAccountNonLocked()))
                .newVersion(String.valueOf(user.isAccountNonLocked()))
                .build();
        actionRepository.save(action);
    }

    @PostMapping("/cancelAppointment/{id}")
    public void cancelAppointment(@PathVariable("id") Long appointmentId) {
        var appointmentToCancel = appointmentRepository.findById(appointmentId);
        appointmentToCancel.orElseThrow().setStatus(AppointmentStatus.CANCELLED);
    }

    @GetMapping("/userList")
    public List<UserDTO> listAllUsers() {
        return userService.getAllNonAdminUsers();
    }

    @GetMapping("/getActions")
    public List<Action> listAllActions() {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var adminActions = actionRepository.findAll();
        return adminActions.stream().filter(a -> a.getCreatedBy().getId().equals(loggedUser.getId())).toList();
    }


}
