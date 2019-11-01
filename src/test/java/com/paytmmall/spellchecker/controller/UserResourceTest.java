package com.paytmmall.spellchecker.controller;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
//@WebMvcTest(value = UserResource.class)
public class UserResourceTest {

    @Autowired
    private MockMvc mvc;

//    @MockBean
//    private UserService userService;


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