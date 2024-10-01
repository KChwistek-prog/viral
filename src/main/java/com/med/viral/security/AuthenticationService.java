package com.med.viral.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.med.viral.exceptions.UserNotFoundException;
import com.med.viral.exceptions.UsernameAlreadyExistsException;
import com.med.viral.model.Admin;
import com.med.viral.model.DTO.CreateAdminDTO;
import com.med.viral.model.DTO.CreateDoctorDTO;
import com.med.viral.model.DTO.CreateUserDTO;
import com.med.viral.model.Doctor;
import com.med.viral.model.User;
import com.med.viral.model.mapper.UserMapper;
import com.med.viral.model.security.*;
import com.med.viral.repository.AdminRepository;
import com.med.viral.repository.DoctorRepository;
import com.med.viral.repository.TokenRepository;
import com.med.viral.repository.UserRepository;
import com.med.viral.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;

@AllArgsConstructor
@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserMapper userMapper;

    public AuthenticationResponse register(RegisterRequest request) {
        if (adminRepository.existsByUsername(request.username()) ||
                doctorRepository.existsByUsername(request.username()) ||
                userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException("Username already exists.");
        }
        var claims = new HashMap<String, Object>();

        return switch (request.role()) {
            case ADMIN -> {
                CreateAdminDTO createAdminDTO = new CreateAdminDTO(request.firstname(), request.lastname(), request.username(), passwordEncoder.encode(request.password()), request.role());
                Admin adminEntity = adminRepository.save(userMapper.UserDTOtoEntity(createAdminDTO));
                claims.put("id", adminEntity.getId());
                var jwtToken = jwtService.generateToken(claims, adminEntity);
                var refreshToken = jwtService.generateRefreshToken(adminEntity);
                saveUserToken(adminEntity, jwtToken);
                yield AuthenticationResponse.builder()
                        .accessToken(jwtToken)
                        .refreshToken(refreshToken)
                        .build();
            }
            case DOCTOR -> {
                CreateDoctorDTO createDoctorDTO = new CreateDoctorDTO(request.firstname(), request.lastname(), request.username(), passwordEncoder.encode(request.password()), request.role());
                Doctor doctorEntity = doctorRepository.save(userMapper.createDoctorDTOToEntity(createDoctorDTO));
                claims.put("id", doctorEntity.getId());
                var jwtToken = jwtService.generateToken(claims, doctorEntity);
                var refreshToken = jwtService.generateRefreshToken(doctorEntity);
                saveUserToken(doctorEntity, jwtToken);
                yield AuthenticationResponse.builder()
                        .accessToken(jwtToken)
                        .refreshToken(refreshToken)
                        .build();
            }
            case PATIENT -> {
                CreateUserDTO create = new CreateUserDTO(request.firstname(), request.lastname(), request.username(), passwordEncoder.encode(request.password()), request.role());
                User userEntity = userRepository.save(userMapper.createUserDTOToEntity(create));
                userEntity.setAccountNonLocked(false);
                claims.put("id", userEntity.getId());
                var jwtToken = jwtService.generateToken(claims, userEntity);
                var refreshToken = jwtService.generateRefreshToken(userEntity);
                saveUserToken(userEntity, jwtToken);
                yield AuthenticationResponse.builder()
                        .accessToken(jwtToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        };
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws UserNotFoundException {
        var userRequest = new UsernamePasswordAuthenticationToken(request.username(), request.password());
        authenticationManager.authenticate(userRequest);
        return switch (request.role()) {
            case PATIENT -> {
                User user = userMapper.UserDTOtoEntity(userService.getUserByUsername(request.username()));
                var jwtToken = jwtService.generateToken(user);
                var refreshToken = jwtService.generateRefreshToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, jwtToken);
                yield AuthenticationResponse.builder()
                        .accessToken(jwtToken)
                        .refreshToken(refreshToken)
                        .build();
            }
            case DOCTOR -> {
                Doctor doctor = userMapper.DoctorDTOtoEntity(userService.getDoctorByUsername(request.username()));
                var jwtToken = jwtService.generateToken(doctor);
                var refreshToken = jwtService.generateRefreshToken(doctor);
                revokeAllUserTokens(doctor);
                saveUserToken(doctor, jwtToken);
                yield AuthenticationResponse.builder()
                        .accessToken(jwtToken)
                        .refreshToken(refreshToken)
                        .build();
            }
            case ADMIN -> {
                Admin admin = userMapper.adminDTOToEntity(userService.getAdminByUsername(request.username()));
                var jwtToken = jwtService.generateToken(admin);
                var refreshToken = jwtService.generateRefreshToken(admin);
                revokeAllUserTokens(admin);
                saveUserToken(admin, jwtToken);
                yield AuthenticationResponse.builder()
                        .accessToken(jwtToken)
                        .refreshToken(refreshToken)
                        .build();
            }

        };
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void saveUserToken(Doctor doctor, String jwtToken) {
        var token = Token.builder()
                .doctor(doctor)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void saveUserToken(Admin admin, String jwtToken) {
        var token = Token.builder()
                .admin(admin)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void revokeAllUserTokens(Doctor doctor) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(doctor.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void revokeAllUserTokens(Admin admin) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(admin.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException, UserNotFoundException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = userMapper.UserDTOtoEntity(this.userService.getByEmail(userEmail));
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
