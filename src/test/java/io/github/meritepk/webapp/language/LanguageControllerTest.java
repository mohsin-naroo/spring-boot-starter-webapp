package io.github.meritepk.webapp.language;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import io.github.meritepk.webapp.security.WebSecurityTest;

@SpringBootTest
@AutoConfigureMockMvc
public class LanguageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/languages")
                .header("Authorization", WebSecurityTest.withTestUser(mockMvc)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(Matchers.greaterThan(1))));
    }

    @Test
    public void testCreate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/languages")
                .header("Authorization", WebSecurityTest.withTestAdmin(mockMvc))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType("application/json")
                .content("{\"id\":\"03\",\"code\":\"ZHC\",\"name\":\"Chinese\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }
}
