package ru.investportfolio.integration.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.access.method.P;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.investportfolio.database.entity.Role;
import ru.investportfolio.database.entity.User;
import ru.investportfolio.integration.IntegrationTestBase;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RequiredArgsConstructor
@AutoConfigureMockMvc
class PortfolioControllerTest extends IntegrationTestBase {

    private final MockMvc mockMvc;
    private static final Long PORTFOLIO_ID = 3L;
    private static final Long BAD_PORTFOLIO_ID = 1_000_000L;
    private static final Long USER_ID = 2L;

    private static final User TEST_USER =
            new User("test@test.com", "test", true, Set.of(Role.USER));

    @BeforeAll
    public static void prepare() {
        TEST_USER.setId(USER_ID);
    }

    @Test
    public void getPortfolios() throws Exception {
        mockMvc.perform(get("/portfolios")
                        .with(user(TEST_USER))
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("portfolios/portfolios"))
                .andExpect(model().attributeExists("portfolios"));

    }

    @Test
    public void getPortfolio() throws Exception {
        mockMvc.perform(get("/portfolios/{id}", PORTFOLIO_ID)
                        .with(user(TEST_USER))
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("portfolios/portfolio"))
                .andExpect(model().attributeExists("portfolio"))
                .andExpect(model().attributeExists("userId"));

    }

    @Test
    public void createPortfolio() throws Exception {
        mockMvc.perform(post("/portfolios/create")
                        .with(user(TEST_USER))
                        .with(csrf())
                        .param("name", "newPortfolio")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/portfolios"));

    }

    @Test
    public void createPortfolioEmptyName() throws Exception {
        mockMvc.perform(post("/portfolios/create")
                        .with(user(TEST_USER))
                        .with(csrf())
                        .param("name", "")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("createErrors"))
                .andExpect(redirectedUrl("/portfolios"));

    }

    @Test
    public void createPortfolioCashError() throws Exception {
        mockMvc.perform(post("/portfolios/create")
                        .with(user(TEST_USER))
                        .with(csrf())
                        .param("name", "newPortfolio")
                        .param("cash", "")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("cashErrors"))
                .andExpect(redirectedUrl("/portfolios"));

    }

    @Test
    public void update() throws Exception {
        mockMvc.perform(post("/portfolios/{id}/update", PORTFOLIO_ID)
                        .with(user(TEST_USER))
                        .with(csrf())
                        .param("name", "newPortfolioName")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlTemplate("/portfolios/{id}", PORTFOLIO_ID));
    }

    @Test
    public void updateEmptyName() throws Exception {
        mockMvc.perform(post("/portfolios/{id}/update", PORTFOLIO_ID)
                        .with(user(TEST_USER))
                        .with(csrf())
                        .param("name", "")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("updateErrors"))
                .andExpect(redirectedUrlTemplate("/portfolios/{id}", PORTFOLIO_ID));
    }

    @Test
    public void delete() throws Exception {
        mockMvc.perform(post("/portfolios/{id}/delete", PORTFOLIO_ID)
                        .with(user(TEST_USER))
                        .with(csrf())
                        .param("name","testPortfolio")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("deletedPortfolioName"))
                .andExpect(flash().attributeExists("isDeleted"))
                .andExpect(redirectedUrl("/portfolios"));
    }

    @Test
    public void deleteFail() throws Exception {
        mockMvc.perform(post("/portfolios/{id}/delete", BAD_PORTFOLIO_ID)
                        .with(user(TEST_USER))
                        .with(csrf())
                        .param("name","testPortfolio")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("deletedPortfolioName"))
                .andExpect(flash().attributeExists("isDeleted"))
                .andExpect(redirectedUrlTemplate("/portfolios/{id}", BAD_PORTFOLIO_ID));
    }

    @Test
    public void changeCash() throws Exception {
        mockMvc.perform(post("/portfolios/{id}/cash", PORTFOLIO_ID)
                        .with(user(TEST_USER))
                        .with(csrf())
                        .param("cash","1000")
                        .param("cashAction","ADD")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlTemplate("/portfolios/{id}", PORTFOLIO_ID));
    }
    @Test
    public void changeCashValidationCashError() throws Exception {
        mockMvc.perform(post("/portfolios/{id}/cash", PORTFOLIO_ID)
                        .with(user(TEST_USER))
                        .with(csrf())
                        .param("cash","")
                        .param("cashAction","ADD")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("cashErrors"))
                .andExpect(redirectedUrlTemplate("/portfolios/{id}", PORTFOLIO_ID));
    }
    @Test
    public void changeCashTooMuchRemoved() throws Exception {
        mockMvc.perform(post("/portfolios/{id}/cash", PORTFOLIO_ID)
                        .with(user(TEST_USER))
                        .with(csrf())
                        .param("cash","1000000000")
                        .param("cashAction","REMOVE")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("cashErrors"))
                .andExpect(flash().attribute("cashErrors", "Cash balance can't become negative!"))
                .andExpect(redirectedUrlTemplate("/portfolios/{id}", PORTFOLIO_ID));
    }
    @Test
    public void deletePosition() throws Exception {
        mockMvc.perform(post("/portfolios/{id}/delete-position", PORTFOLIO_ID)
                        .with(user(TEST_USER))
                        .with(csrf())
                        .param("positionId","1")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlTemplate("/portfolios/{id}", PORTFOLIO_ID));
    }
}