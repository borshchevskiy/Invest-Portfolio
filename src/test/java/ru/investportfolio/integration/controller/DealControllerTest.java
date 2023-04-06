package ru.investportfolio.integration.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
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

@AutoConfigureMockMvc
@RequiredArgsConstructor
class DealControllerTest extends IntegrationTestBase {

    private final MockMvc mockMvc;
    private static final User TEST_USER =
            new User("test@test.com", "test", true, Set.of(Role.USER));
    private static final Long USER_ID = 2L;
    private static final Long PORTFOLIO_ID = 3L;

    @BeforeAll
    public static void prepare() {
        TEST_USER.setId(USER_ID);
    }


    @Test
    public void getNewDeal() throws Exception {
        mockMvc.perform(get("/portfolios/{id}/new-deal", PORTFOLIO_ID)
                        .with(user(TEST_USER))
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("deals/new-deal"));
    }

    @Test
    public void addNewDeal() throws Exception {
        mockMvc.perform(post("/portfolios/{id}/new-deal", PORTFOLIO_ID)
                        .with(csrf())
                        .with(user(TEST_USER))
                        .param("securityNameAndTicker", "ГАЗПРОМ ао (GAZP)")
                        .param("acquisitionPrice", "200")
                        .param("quantity", "5")
                        .param("dealType", "BUY")
                        .param("portfolioId", PORTFOLIO_ID.toString())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlTemplate("/portfolios/{id}", PORTFOLIO_ID));
    }
    @Test
    public void addNewDealBadTicker() throws Exception {
        mockMvc.perform(post("/portfolios/{id}/new-deal", PORTFOLIO_ID)
                        .with(csrf())
                        .with(user(TEST_USER))
                        .param("securityNameAndTicker", "ГАЗМЯС ао (GAZM)")
                        .param("acquisitionPrice", "200")
                        .param("quantity", "5")
                        .param("dealType", "BUY")
                        .param("portfolioId", PORTFOLIO_ID.toString())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("errors"))
                .andExpect(redirectedUrlTemplate("/portfolios/{id}/new-deal", PORTFOLIO_ID));
    }
    @Test
    public void addNewDealNotEnoughMoney() throws Exception {
        mockMvc.perform(post("/portfolios/{id}/new-deal", PORTFOLIO_ID)
                        .with(csrf())
                        .with(user(TEST_USER))
                        .param("securityNameAndTicker", "ГАЗПРОМ ао (GAZP)")
                        .param("acquisitionPrice", "2000000000")
                        .param("quantity", "5")
                        .param("dealType", "BUY")
                        .param("portfolioId", PORTFOLIO_ID.toString())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("errors"))
                .andExpect(redirectedUrlTemplate("/portfolios/{id}/new-deal", PORTFOLIO_ID));
    }
}