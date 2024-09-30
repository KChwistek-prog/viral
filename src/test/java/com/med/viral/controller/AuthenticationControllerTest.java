package com.med.viral.controller;

import com.med.viral.model.Admin;
import com.med.viral.repository.AdminRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AdminRepository adminRepository;


    @Test
    void testRegister() throws Exception {
        //given
        String body = "{\"name\":\"Alice\",\"lastname\":\"Cooper\",\"username\":\"admin\",\"password\":\"1234\",\"role\":\"ADMIN\"}";

        //when
        var result = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
        Admin newUser = adminRepository.findById(1).orElseThrow();

        //then
        result.andExpect(status().isOk());
        assertEquals("Cooper", newUser.getLastname());
    }

}