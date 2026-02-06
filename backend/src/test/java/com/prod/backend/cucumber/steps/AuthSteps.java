package com.prod.backend.cucumber.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prod.backend.cucumber.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class AuthSteps {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private TestContext context;

    @Given("a new user with password {string}")
    public void aNewUserWithPassword(String password) {
        context.setEmail(randomEmail());
        context.setPassword(password);
        context.setAccessToken(null);
        context.setSignedUp(false);
    }

    @Given("the user is authenticated")
    public void theUserIsAuthenticated() throws Exception {
        if (context.getEmail() == null) {
            context.setEmail(randomEmail());
            context.setPassword("Pass123!");
        }

        if (!context.isSignedUp()) {
            signUp();
        }

        if (context.getAccessToken() == null) {
            extractAccessToken(context.getLastResult());
        }
    }

    @When("the user signs up")
    public void theUserSignsUp() throws Exception {
        signUp();
    }

    @When("the user logs in")
    public void theUserLogsIn() throws Exception {
        if (!context.isSignedUp()) {
            signUp();
        }

        String body = "{\"email\":\"" + context.getEmail() + "\",\"password\":\"" + context.getPassword() + "\"}";
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn();

        context.setLastResult(result);
        extractAccessToken(result);
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int status) {
        assertThat(context.getLastResult()).isNotNull();
        assertThat(context.getLastResult().getResponse().getStatus()).isEqualTo(status);
    }

    @Then("the response contains an access token")
    public void theResponseContainsAccessToken() throws Exception {
        String token = extractAccessToken(context.getLastResult());
        assertThat(token).isNotBlank();
    }

    private void signUp() throws Exception {
        String body = "{\"email\":\"" + context.getEmail() + "\",\"password\":\"" + context.getPassword() + "\"}";
        MvcResult result = mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn();

        context.setLastResult(result);
        context.setSignedUp(true);
        extractAccessToken(result);
    }

    private String extractAccessToken(MvcResult result) throws Exception {
        if (result == null) {
            return null;
        }

        int status = result.getResponse().getStatus();
        if (status < 200 || status >= 300) {
            return null;
        }

        String response = result.getResponse().getContentAsString();
        if (response == null || response.isBlank()) {
            return null;
        }

        JsonNode node = objectMapper.readTree(response);
        JsonNode tokenNode = node.get("accessToken");
        if (tokenNode == null || tokenNode.asText().isBlank()) {
            return null;
        }

        String token = tokenNode.asText();
        context.setAccessToken(token);
        return token;
    }

    private String randomEmail() {
        return "user-" + UUID.randomUUID() + "@example.com";
    }
}
