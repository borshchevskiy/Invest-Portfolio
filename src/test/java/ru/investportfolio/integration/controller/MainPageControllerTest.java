package ru.investportfolio.integration.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import ru.investportfolio.integration.IntegrationTestBase;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RequiredArgsConstructor
@AutoConfigureMockMvc
class MainPageControllerTest extends IntegrationTestBase {

    private final MockMvc mockMvc;
    @Test
    public void getMainPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("main"));
    }

}