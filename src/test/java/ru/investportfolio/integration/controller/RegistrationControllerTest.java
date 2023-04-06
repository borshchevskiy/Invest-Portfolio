package ru.investportfolio.integration.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import ru.investportfolio.integration.IntegrationTestBase;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RequiredArgsConstructor
@AutoConfigureMockMvc
class RegistrationControllerTest extends IntegrationTestBase {

    private final MockMvc mockMvc;
    private static final String TEST_EMAIL = "test1@test.com";
    private static final String TEST_EXISTING_EMAIL = "test@test.com";
    private static final String TEST_PASSWORD = "testPassword";
    private static final String TEST_PASSWORD_CONFIRM = TEST_PASSWORD;
    private static final String TEST_PASSWORD_BAD_CONFIRM = "badConfirmation";

    @Test
    public void getRegistration() throws Exception {
        mockMvc.perform(get("/registration"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    public void register() throws Exception {
        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("email", TEST_EMAIL)
                        .param("password", TEST_PASSWORD)
                        .param("passwordConfirm", TEST_PASSWORD_CONFIRM)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void registerUserExists() throws Exception {
        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("email", TEST_EXISTING_EMAIL)
                        .param("password", TEST_PASSWORD)
                        .param("passwordConfirm", TEST_PASSWORD_CONFIRM)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registration"))
                .andExpect(flash().attributeExists("user"))
                .andExpect(flash().attribute("user", hasProperty("email", equalTo(TEST_EXISTING_EMAIL))))
                .andExpect(flash().attribute("user", hasProperty("password", equalTo(TEST_PASSWORD))))
                .andExpect(flash().attributeExists("errors"));
    }

    @Test
    public void registerConfirmationIsEmpty() throws Exception {
        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("email", TEST_EMAIL)
                        .param("password", TEST_PASSWORD)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registration"))
                .andExpect(flash().attributeExists("user"))
                .andExpect(flash().attribute("user", hasProperty("email", equalTo(TEST_EMAIL))))
                .andExpect(flash().attribute("user", hasProperty("password", equalTo(TEST_PASSWORD))))
                .andExpect(flash().attributeExists("errors"));
    }
    @Test
    public void registerPasswordAndConfirmDifferent() throws Exception {
        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .param("email", TEST_EMAIL)
                        .param("password", TEST_PASSWORD)
                        .param("passwordConfirm", TEST_PASSWORD_BAD_CONFIRM)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registration"));
    }

}