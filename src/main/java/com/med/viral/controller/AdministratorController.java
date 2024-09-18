package com.med.viral.controller;

import com.med.viral.model.Action;
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
        var user = userRepository.findById(userId).orElseThrow();
        if (!user.getRole().equals(Role.ADMIN)) {
            userRepository.delete(user);
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

    @PostMapping("/updateAppointmentStatus/{id}")
    public void changeAppoitmentStatus(@RequestBody String status, @PathVariable("id") Long appointmentID) {
        var appointmentToChange = appointmentRepository.findById(appointmentID);
        if (status.equals(AppointmentStatus.OPEN.getValue())) {
            appointmentToChange.orElseThrow().setStatus(AppointmentStatus.OPEN);
        } else if (status.equals(AppointmentStatus.CLOSED.getValue())) {
            appointmentToChange.orElseThrow().setStatus(AppointmentStatus.CLOSED);
        } else if (status.equals(AppointmentStatus.CANCELLED.getValue())) {
            appointmentToChange.orElseThrow().setStatus(AppointmentStatus.CANCELLED);
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
