package ru.investportfolio.integration.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import ru.investportfolio.database.entity.Role;
import ru.investportfolio.database.entity.User;
import ru.investportfolio.integration.IntegrationTestBase;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RequiredArgsConstructor
@AutoConfigureMockMvc
class UserControllerTest extends IntegrationTestBase {

    private final MockMvc mockMvc;
    private static final User TEST_USER =
            new User("test@test.com", "test", true, Set.of(Role.USER));
    private static final String NEW_EMAIL = "newMail@test.com";
    private static final String NEW_FIRSTNAME = "newFirstname";
    private static final String NEW_LASTNAME = "newLastname";
    private static final String NEW_BAD_EMAIL = "";
    private static final String NEW_PASSWORD = "newPassword";
    private static final String NEW_PASSWORD_CONFIRM = NEW_PASSWORD;
    private static final String NEW_PASSWORD_CONFIRM_BAD = "";
    @Test
    public void getProfile() throws Exception {
        mockMvc.perform(get("/profile").with(user(TEST_USER)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("profile/profile"))
                .andExpect(model().attributeExists("user"));
    }
    @Test
    public void getProfileUpdate() throws Exception {
        mockMvc.perform(get("/profile/update")
                        .with(user(TEST_USER)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("profile/updateProfile"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    public void updateProfile() throws Exception {
        mockMvc.perform(post("/profile/update")
                        .with(csrf())
                        .with(user(TEST_USER))
                        .param("email", NEW_EMAIL)
                        .param("firstname", NEW_FIRSTNAME)
                        .param("lastname", NEW_LASTNAME)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));
    }
    @Test
    public void updateProfileFailure() throws Exception {
        mockMvc.perform(post("/profile/update")
                        .with(csrf())
                        .with(user(TEST_USER))
                        .param("email", NEW_BAD_EMAIL)
                        .param("firstname", NEW_FIRSTNAME)
                        .param("lastname", NEW_LASTNAME)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("errors"))
                .andExpect(redirectedUrl("/profile/update"));
    }
    @Test
    public void getPasswordUpdate() throws Exception {
        mockMvc.perform(get("/profile/update/password")
                        .with(user(TEST_USER)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("profile/updatePassword"));
    }
    @Test
    public void updatePassword() throws Exception {
        mockMvc.perform(post("/profile/update/password")
                        .with(csrf())
                        .with(user(TEST_USER))
                        .param("password", NEW_PASSWORD)
                        .param("passwordConfirm", NEW_PASSWORD_CONFIRM)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));
    }
    @Test
    public void updatePasswordFailure() throws Exception {
        mockMvc.perform(post("/profile/update/password")
                        .with(csrf())
                        .with(user(TEST_USER))
                        .param("password", NEW_PASSWORD)
                        .param("passwordConfirm", NEW_PASSWORD_CONFIRM_BAD)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("errors"))
                .andExpect(redirectedUrl("/profile/update/password"));
    }
}