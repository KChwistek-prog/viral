package com.med.viral.controller;

import com.med.viral.model.Action;
import com.med.viral.model.ActionType;
import com.med.viral.model.AppointmentStatus;
import com.med.viral.model.User;
import com.med.viral.model.security.Role;
import com.med.viral.repository.ActionRepository;
import com.med.viral.repository.AppointmentRepository;
import com.med.viral.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/administrator")
@PreAuthorize("hasRole('ADMIN')")
public class AdministratorController {
    UserRepository userRepository;
    AppointmentRepository appointmentRepository;
    ActionRepository actionRepository;

    @Autowired
    public AdministratorController(UserRepository userRepository, AppointmentRepository appointmentRepository, ActionRepository actionRepository) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.actionRepository = actionRepository;
    }


    @DeleteMapping("/deleteAccount/{id}")
    public void deleteUserAccount(@PathVariable("id") Integer userId) throws Exception {
        User loggedAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var userToDelete = userRepository.findById(userId).orElseThrow().getUsername();
        Action action = Action.builder()
                .actionType(ActionType.DELETE_ACCOUNT)
                .createdBy(loggedAdmin)
                .createdDate(LocalDateTime.now())
                .fieldName("User account")
                .oldVersion(userToDelete)
                .newVersion("Deleted")
                .build();
        var user = userRepository.findById(userId).orElseThrow();
        if (!user.getRole().equals(Role.ADMIN)) {
            userRepository.delete(user);
            actionRepository.save(action);
        } else throw new Exception("Can't delete administrator account");
    }

    @PatchMapping("/updateUser")
    public ResponseEntity<User> editUserAccount(@RequestBody User user) {
        var localUser = userRepository.findById(user.getId()).orElseThrow();
        if (!localUser.getRole().equals(Role.ADMIN)) {
            return ResponseEntity.ok(userRepository.save(user));
        }
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/addAccount/{id}")
    public ResponseEntity<User> addAccount(@PathVariable("id") Integer userId) {
        var localUser = userRepository.findById(userId).orElseThrow();
        localUser.setAccountNonLocked(true);
        User loggedAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var action = Action.builder()
                .actionType(ActionType.ADD_ACCOUNT)
                .createdBy(loggedAdmin)
                .createdDate(LocalDateTime.now())
                .fieldName("AccountNonLocked")
                .oldVersion("false")
                .newVersion("true")
                .build();
        return ResponseEntity.ok(userRepository.save(localUser));
    }

    @PostMapping("/updateAppointmentStatus/{id}")
    public void changeAppoitmentStatus(@RequestBody String status, @PathVariable("id") Long appointmentID) {
        User loggedAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var appointmentToChange = appointmentRepository.findById(appointmentID).orElseThrow();

        Action action = Action.builder()
                .actionType(ActionType.MODIFY_ACCOUNT)
                .createdBy(loggedAdmin)
                .createdDate(LocalDateTime.now())
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

    @PostMapping("/changeAccountStatus/{id}")
    public void changeAccountStatus(@PathVariable("id") Integer userId) {
        var user = userRepository.findById(userId).orElseThrow();
        user.setAccountNonLocked(false);
    }

    @PostMapping("/cancelAppointment/{id}")
    public void cancelAppointment(@PathVariable("id") Long appointmentId) {
        var appointmentToCancel = appointmentRepository.findById(appointmentId);
        appointmentToCancel.orElseThrow().setStatus(AppointmentStatus.CANCELLED);
    }

    @GetMapping("/userList")
    public List<User> listAllUsers() {
        var users = userRepository.findAll();
        return users.stream().filter(u -> !u.getRole().equals(Role.ADMIN)).toList();
    }

    @GetMapping("/getActions")
    public List<Action> listAllActions() {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var adminActions = actionRepository.findAll();
        return adminActions.stream().filter(a -> a.getCreatedBy().equals(loggedUser.getId())).toList();

    }


}
