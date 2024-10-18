package com.med.viral.controller;

import com.med.viral.exceptions.AppointmentNotFoundException;
import com.med.viral.exceptions.UserNotFoundException;
import com.med.viral.model.Action;
import com.med.viral.model.ActionType;
import com.med.viral.model.Admin;
import com.med.viral.model.DTO.PatientDTO;
import com.med.viral.model.User;
import com.med.viral.model.mapper.UserMapper;
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
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/updateUser/{userId}")
    public ResponseEntity<PatientDTO> editUserAccount(@PathVariable("userId") Integer id, @RequestBody PatientDTO patientDTO) throws Exception {
        return userService.editUser(id, patientDTO);
    }

    @PutMapping("/addAccount")
    public ResponseEntity<PatientDTO> addAccount(@RequestBody PatientDTO patientDTO) {
        var user = userMapper.PatientDTOtoEntity(patientDTO);
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
        return ResponseEntity.ok(userService.saveUser(userMapper.PatientEntityToDTO(user)));
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
        var user = userMapper.PatientDTOtoEntity(getUser);
        user.setAccountNonLocked(true);
        userService.saveUser(userMapper.PatientEntityToDTO(user));

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
    public List<PatientDTO> listAllUsers() {
        return userService.getAllNonAdminUsers();
    }

    @GetMapping("/getActions")
    public List<Action> listAllActions() {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return actionService.getActions(loggedUser.getId());
    }


}
