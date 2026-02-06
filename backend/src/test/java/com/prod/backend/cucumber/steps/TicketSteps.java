package com.prod.backend.cucumber.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prod.backend.cucumber.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class TicketSteps {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private TestContext context;

    @When("the user creates a ticket with subject {string} and description {string}")
    public void theUserCreatesATicket(String subject, String description) throws Exception {
        assertThat(context.getAccessToken())
                .withFailMessage("Expected access token to be set before creating tickets")
                .isNotBlank();

        String body = "{\"subject\":\"" + subject + "\",\"description\":\"" + description + "\"}";
        MvcResult result = mockMvc.perform(post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + context.getAccessToken())
                        .content(body))
                .andReturn();

        context.setLastResult(result);
    }

    @When("the user requests their tickets")
    public void theUserRequestsTheirTickets() throws Exception {
        assertThat(context.getAccessToken())
                .withFailMessage("Expected access token to be set before fetching tickets")
                .isNotBlank();

        MvcResult result = mockMvc.perform(get("/tickets")
                        .header("Authorization", "Bearer " + context.getAccessToken()))
                .andReturn();

        context.setLastResult(result);
    }

    @Then("the ticket subject is {string}")
    public void theTicketSubjectIs(String expected) throws Exception {
        assertThat(context.getLastResult()).isNotNull();
        String response = context.getLastResult().getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(response);
        assertThat(node.get("subject").asText()).isEqualTo(expected);
    }

    @Then("the tickets list contains subject {string}")
    public void theTicketsListContainsSubject(String expected) throws Exception {
        assertThat(context.getLastResult()).isNotNull();
        String response = context.getLastResult().getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(response);
        boolean found = false;

        if (node.isArray()) {
            for (JsonNode item : node) {
                if (expected.equals(item.get("subject").asText())) {
                    found = true;
                    break;
                }
            }
        }

        assertThat(found)
                .withFailMessage("Expected tickets list to contain subject '%s'", expected)
                .isTrue();
    }
}
