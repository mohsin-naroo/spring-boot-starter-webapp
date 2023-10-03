package io.github.meritepk.webapp.security;

import java.util.Map;

import javax.servlet.http.Cookie;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class WebSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    private static String test_user;
    private static String test_admin;

    @Test
    public void testWebRootSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    public void testAuthenticationEnabled() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/index.html"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testAuthenticationSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/index.html")
                .with(SecurityMockMvcRequestPostProcessors.user("test")))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testCsrfEnabled() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/token")
                .param("username", "test")
                .param("password", "test123"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testLoginSuccess() throws Exception {
        Assertions.assertNotNull(WebSecurityTest.withTestUser(mockMvc));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/token")
                .cookie(new Cookie(WebSecurityService.ACCESS_TOKEN, WebSecurityTest.withTestUser(mockMvc).substring(7)))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk());
        test_user = null;
    }

    @Test
    public void testLogoutSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/token/revoke")
                .header("Authorization", WebSecurityTest.withTestUser(mockMvc))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk());
        test_user = null;
    }

    @Test
    public void testLoginFails() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/token")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        mockMvc.perform(MockMvcRequestBuilders.get("/login")
                .with(SecurityMockMvcRequestPostProcessors.user("test")))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testLogoutFails() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/token/revoke")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        mockMvc.perform(MockMvcRequestBuilders.post("/logout")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        mockMvc.perform(MockMvcRequestBuilders.post("/logout")
                .with(SecurityMockMvcRequestPostProcessors.user("test"))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testErrorSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/error"))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError());
    }

    public static String withTestUser(MockMvc mockMvc) throws Exception {
        if (test_user == null) {
            test_user = token(mockMvc, "test", "test123", "test-");
        }
        return test_user;
    }

    public static String withTestAdmin(MockMvc mockMvc) throws Exception {
        if (test_admin == null) {
            test_admin = token(mockMvc, "admin", "admin123", "admin-");
        }
        return test_admin;
    }

    public static String token(MockMvc mockMvc, String username, String password, String startsWith) throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/token")
                .param("username", username)
                .param("password", password)
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("token_type", Matchers.equalTo("Bearer")))
                .andExpect(MockMvcResultMatchers.jsonPath("access_token", Matchers.startsWith(startsWith)))
                .andReturn().getResponse().getContentAsString();
        Map<String, Object> json = JsonParserFactory.getJsonParser().parseMap(response);
        return json.get("token_type") + " " + json.get("access_token");
    }
}
