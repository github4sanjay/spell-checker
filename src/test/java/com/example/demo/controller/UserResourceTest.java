package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repo.UserRepo;
import com.example.demo.service.UserService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = UserResource.class)
public class UserResourceTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;


//    @Test
//    public void get() throws Exception{
//
//        when(userService.getAll()).thenReturn(
//                List.of(
//                        new User(
//                                UUID.randomUUID(),
//                                "sanju4115",
//                                "Sanjay", "Kumar"
//                        )
//                )
//        );
//        mvc.perform( MockMvcRequestBuilders
//                .get("/api/v1/users")
//                .accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$[0].userName").exists())
//                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value("Sanjay"));
//
//    }
}