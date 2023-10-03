package io.github.meritepk.webapp.person;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.jayway.jsonpath.JsonPath;

import io.github.meritepk.webapp.security.WebSecurityTest;

@SpringBootTest
@AutoConfigureMockMvc
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private String id;

    @Test
    public void testGetAll() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/persons")
                .param("page", "-1")
                .param("size", "-1")
                .header("Authorization", WebSecurityTest.withTestUser(mockMvc)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(Matchers.greaterThan(1))));
    }

    @Test
    public void testGetOne() throws Exception {
        get("01", "1");
    }

    private void get(String id, String keyid) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/persons/" + keyid)
                .header("Authorization", WebSecurityTest.withTestUser(mockMvc)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.equalTo(keyid)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", Matchers.is("First" + id)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", Matchers.is("Last" + id)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is("first" + id + "@last" + id)));
    }

    @Test
    public void testGetNotFound() throws Exception {
        notFound("07");
    }

    private void notFound(String id) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/persons/" + id)
                .header("Authorization", WebSecurityTest.withTestUser(mockMvc)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testCreate() throws Exception {
        create("03");
    }

    private void create(String id) throws Exception {
        String content = "{\"id\":\"" + id + "\",\"email\":\"first" + id + "@last" + id + "\",\"firstName\":\"First"
                + id
                + "\",\"lastName\":\"Last" + id
                + "\",\"dateOfBirth\":\"2017-01-01\",\"countryId\":\"1\",\"languageId\":\"1\",\"userId\":\"1\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/persons")
                .header("Authorization", WebSecurityTest.withTestUser(mockMvc))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType("application/json")
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/persons")
                .header("Authorization", WebSecurityTest.withTestAdmin(mockMvc))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType("application/json")
                .content(content))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andDo(result -> {
                    String json = result.getResponse().getContentAsString();
                    this.id = JsonPath.compile("$.id").read(json).toString();
                });
        get(id, this.id);
    }

    @Test
    public void testCreateExistingShouldFail() throws Exception {
        String id = "02";
        String content = "{\"id\":\"" + id + "\",\"email\":\"first" + id + "@last" + id + "\",\"firstName\":\"First"
                + id + "\",\"lastName\":\"Last" + id
                + "\",\"dateOfBirth\":\"2017-01-01\",\"countryId\":\"1\",\"languageId\":\"1\",\"userId\":\"1\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/persons")
                .header("Authorization", WebSecurityTest.withTestAdmin(mockMvc))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType("application/json")
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void testUpdate() throws Exception {
        String id = "04";
        create(id);
        String content = "{\"id\":\"04\",\"email\":\"first44@last44\",\"firstName\":\"First44\",\"lastName\":\"Last44\""
                + ",\"dateOfBirth\":\"2017-01-01\",\"countryId\":\"1\",\"languageId\":\"1\",\"userId\":\"1\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/persons/" + this.id)
                .header("Authorization", WebSecurityTest.withTestAdmin(mockMvc))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType("application/json")
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(result -> {
                    String json = result.getResponse().getContentAsString();
                    this.id = JsonPath.compile("$.id").read(json).toString();
                });
        try {
            get(id, this.id);
            Assertions.fail("testUpdate() failed");
        } catch (AssertionError e) {
        }
    }

    @Test
    public void testUpdateNonExistingShouldFail() throws Exception {
        String id = "10";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/persons/" + id)
                .header("Authorization", WebSecurityTest.withTestAdmin(mockMvc))
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType("application/json")
                .content("{\"id\":\"04\",\"email\":\"first44@last44\",\"firstName\":\"First44\",\"lastName\":\"Last44\""
                        + ",\"dateOfBirth\":\"2017-01-01\",\"countryId\":\"1\",\"languageId\":\"1\",\"userId\":\"1\"}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
