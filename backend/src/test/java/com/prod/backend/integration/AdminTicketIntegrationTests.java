package com.prod.backend.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminTicketIntegrationTests {

    private static final String ADMIN_EMAIL = "admin@test.com";
    private static final String ADMIN_PASSWORD = "Admin123!";

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void adminCanListAllTickets() throws Exception {
        String userEmail = randomEmail();
        String userToken = signupAndGetToken(userEmail, "Pass123!");

        int ticketId = createTicket(userToken, "User ticket");

        String adminToken = loginAndGetToken(ADMIN_EMAIL, ADMIN_PASSWORD);

        MvcResult result = mockMvc.perform(get("/admin/tickets")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode tickets = objectMapper.readTree(result.getResponse().getContentAsString());
        boolean found = false;

        for (JsonNode node : tickets) {
            if (node.get("id").asInt() == ticketId) {
                assertEquals(userEmail, node.get("userEmail").asText());
                found = true;
                break;
            }
        }

        assertTrue(found, "Expected admin list to include user ticket");
    }

    @Test
    void adminCanUpdateTicketAndUserStillSeesIt() throws Exception {
        String userEmail = randomEmail();
        String userToken = signupAndGetToken(userEmail, "Pass123!");

        int ticketId = createTicket(userToken, "Original subject");

        String adminToken = loginAndGetToken(ADMIN_EMAIL, ADMIN_PASSWORD);

        String updateBody = "{\"subject\":\"Admin updated\",\"description\":\"Updated by admin\",\"status\":\"IN_PROGRESS\"}";

        mockMvc.perform(put("/admin/tickets/" + ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminToken)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subject").value("Admin updated"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.userEmail").value(userEmail));

        MvcResult userTickets = mockMvc.perform(get("/tickets")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode tickets = objectMapper.readTree(userTickets.getResponse().getContentAsString());
        boolean found = false;
        for (JsonNode node : tickets) {
            if (node.get("id").asInt() == ticketId) {
                assertEquals("Admin updated", node.get("subject").asText());
                assertEquals("IN_PROGRESS", node.get("status").asText());
                found = true;
                break;
            }
        }

        assertTrue(found, "Expected user to still see the updated ticket");
    }

    @Test
    void nonAdminCannotAccessAdminEndpoints() throws Exception {
        String userToken = signupAndGetToken(randomEmail(), "Pass123!");
        int ticketId = createTicket(userToken, "User ticket");

        mockMvc.perform(get("/admin/tickets")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());

        String updateBody = "{\"subject\":\"Hack\",\"description\":\"Nope\",\"status\":\"CLOSED\"}";
        mockMvc.perform(put("/admin/tickets/" + ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + userToken)
                        .content(updateBody))
                .andExpect(status().isForbidden());
    }

    private int createTicket(String token, String subject) throws Exception {
        String body = "{\"subject\":\"" + subject + "\",\"description\":\"Test description\"}";
        String response = mockMvc.perform(post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(response);
        return node.get("id").asInt();
    }

    private String signupAndGetToken(String email, String password) throws Exception {
        String body = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
        String response = mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(response);
        return node.get("accessToken").asText();
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        String body = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(response);
        return node.get("accessToken").asText();
    }

    private String randomEmail() {
        return "user-" + UUID.randomUUID() + "@example.com";
    }
}
