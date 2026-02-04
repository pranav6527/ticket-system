package com.prod.backend.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @Test
    void signupReturnsAccessTokenAndRefreshCookie() throws Exception {
        String email = randomEmail();
        String body = "{\"email\":\"" + email + "\",\"password\":\"Pass123!\"}";

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(cookie().exists("refreshToken"));
    }

    @Test
    void loginReturnsAccessTokenForValidCredentials() throws Exception {
        String email = randomEmail();
        signup(email, "Pass123!");

        String body = "{\"email\":\"" + email + "\",\"password\":\"Pass123!\"}";

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(cookie().exists("refreshToken"));
    }

    @Test
    void loginFailsWithInvalidCredentials() throws Exception {
        String email = randomEmail();
        signup(email, "Pass123!");

        String body = "{\"email\":\"" + email + "\",\"password\":\"WrongPass!\"}";

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refreshIssuesNewAccessTokenAndCookie() throws Exception {
        String email = randomEmail();
        String password = "Pass123!";

        MvcResult login = login(email, password);

        mockMvc.perform(post("/auth/refresh")
                        .cookie(login.getResponse().getCookie("refreshToken")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(cookie().exists("refreshToken"));
    }

    @Test
    void logoutClearsRefreshCookie() throws Exception {
        String email = randomEmail();
        String password = "Pass123!";

        MvcResult login = login(email, password);

        mockMvc.perform(post("/auth/logout")
                        .cookie(login.getResponse().getCookie("refreshToken")))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", containsString("Max-Age=0")));
    }

    private void signup(String email, String password) throws Exception {
        String body = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    private MvcResult login(String email, String password) throws Exception {
        signup(email, password);
        String body = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
        return mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("refreshToken"))
                .andReturn();
    }

    private String randomEmail() {
        return "user-" + UUID.randomUUID() + "@example.com";
    }
}
