package com.bartek.supportportal.resource;

import com.bartek.supportportal.domain.User;
import com.bartek.supportportal.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserResourceTest {

    private static final String API_ROOT = "/user";
    private static final String[] authorities_user = {"ROLE_USER"};
    public static final String PASSWORD = "somepass";
    public static final String USERNAME = "jdoe";
    public static final String USERNAME_OKOT = "okot";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    User one, two, three, fourth;

    @BeforeEach
    void setUp() {
        userRepository.flush();

        one = User.builder().userId("2131j2asda").email("one@gmail.com").firstName("Joe").lastName("Doe")
                .username(USERNAME).isActive(true).isNonLocked(true).authorities(authorities_user)
                .password(new BCryptPasswordEncoder().encode(PASSWORD)).build();

        two = User.builder().userId("213qwe2asda").email("two@gmail.com").firstName("Ola").lastName("Kot")
                .username(USERNAME_OKOT).role("ROLE_ADMIN").isActive(true).isNonLocked(true).authorities(authorities_user)
                .password(new BCryptPasswordEncoder().encode(PASSWORD)).build();

        three = User.builder().email("three@gmail.com").firstName("Rick").lastName("Morty")
                .username("rmorty").build();

        fourth = User.builder().email("ncage@gmail.com").firstName("Nicolas").lastName("Cage")
                .username("ncage").role("ROLE_USER").isActive(true).isNonLocked(true).authorities(authorities_user).build();

        userRepository.saveAll(Arrays.asList(one, two));
    }


    @Test
    void register() throws Exception {
        mockMvc.perform(post(API_ROOT + "/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(three)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", Matchers.is(three.getEmail())));
    }

    @Test
    void addNotAuthenticated() throws Exception {
        mockMvc.perform(post(API_ROOT + "/add")
                        .contentType(MULTIPART_FORM_DATA)
                        .param("email", fourth.getEmail())
                        .param("firstName", fourth.getFirstName())
                        .param("isActive", String.valueOf(fourth.isActive()))
                        .param("lastName", fourth.getLastName())
                        .param("nonLocked", String.valueOf(fourth.isNonLocked()))
                        .param("role", fourth.getRole())
                        .param("username", fourth.getUsername()))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void addAuthenticated() throws Exception {
        mockMvc.perform(post(API_ROOT + "/add")
                        .contentType(MULTIPART_FORM_DATA)
                        .param("email", fourth.getEmail())
                        .param("firstName", fourth.getFirstName())
                        .param("isActive", String.valueOf(fourth.isActive()))
                        .param("lastName", fourth.getLastName())
                        .param("nonLocked", String.valueOf(fourth.isNonLocked()))
                        .param("role", fourth.getRole())
                        .param("username", fourth.getUsername()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", Matchers.is(fourth.getEmail())));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateAuthenticated() throws Exception {
        mockMvc.perform(post(API_ROOT + "/update")
                        .contentType(MULTIPART_FORM_DATA)
                        .param("currentUsername", two.getUsername())
                        .param("email", "newemail@email.com")
                        .param("firstName", two.getFirstName())
                        .param("isActive", String.valueOf(two.isActive()))
                        .param("lastName", two.getLastName())
                        .param("nonLocked", String.valueOf(fourth.isNonLocked()))
                        .param("role", two.getRole())
                        .param("username", two.getUsername()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", Matchers.is("newemail@email.com")));
    }


    @Test
    void updateNotAuthenticated() throws Exception {
        mockMvc.perform(post(API_ROOT + "/update")
                        .contentType(MULTIPART_FORM_DATA)
                        .param("currentUsername", two.getUsername())
                        .param("email", "newemail@email.com")
                        .param("firstName", two.getFirstName())
                        .param("isActive", String.valueOf(two.isActive()))
                        .param("lastName", two.getLastName())
                        .param("nonLocked", String.valueOf(fourth.isNonLocked()))
                        .param("role", two.getRole())
                        .param("username", two.getUsername()))
                .andExpect(status().isForbidden());
    }


    @Test
    void loginWrongCredentials() throws Exception {

        JSONObject json = new JSONObject();
        json.put("username", one.getUsername());
        json.put("password", "wrongpassword");

        mockMvc.perform(post(API_ROOT + "/login")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(json.toJSONString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus", Matchers.is("BAD_REQUEST")));

    }

    @Test
    void loginCorrectCredentials() throws Exception {

        JSONObject json = new JSONObject();
        json.put("username", one.getUsername());
        json.put("password", PASSWORD);

        mockMvc.perform(post(API_ROOT + "/login")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(json.toJSONString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", Matchers.is(one.getEmail())));

    }

    @Test
    void findUserNotAuthenticated() throws Exception {
        mockMvc.perform(get(API_ROOT + "/find/" + USERNAME))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void findUserAuthenticated() throws Exception {
        mockMvc.perform(get(API_ROOT + "/find/" + USERNAME))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void userListNotAuthenticated() throws Exception {
        mockMvc.perform(get(API_ROOT + "/list"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void userListAuthenticated() throws Exception {
        mockMvc.perform(get(API_ROOT + "/list"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].username", Matchers.is(USERNAME)));
    }

    @Test
    void resetPasswordNoAuthentication() throws Exception {
        mockMvc.perform(get(API_ROOT + "/resetPassword/" + one.getEmail()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void resetPasswordAuthenticated() throws Exception {
        mockMvc.perform(get(API_ROOT + "/resetPassword/" + one.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus", Matchers.is("OK")));
    }

    @Test
    void deleteUserNoAuthentication() throws Exception {
        mockMvc.perform(delete(API_ROOT + "/delete/" + USERNAME_OKOT))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"user:delete"})
    void deleteUserAdmin() throws Exception {
        mockMvc.perform(delete(API_ROOT + "/delete/" + USERNAME_OKOT))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "manager", authorities = {"user:read", "user:update"})
    void deleteUserAnonymousUser() throws Exception {
        mockMvc.perform(delete(API_ROOT + "/delete/" + USERNAME_OKOT))
                .andExpect(status().isForbidden());
    }

    @Test
    void getProfileImage() {
        //TODO
    }

    @Test
    void getTempProfileImage() {
        //TODO
    }

    @Test
    void updateProfileImage() {
        //TODO
    }
}