package io.github.meritepk.webapp.api;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.ServletWebRequest;

public class ApiAppTest {

    @Test
    public void testApiErrorHandler() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn("/test");
        ApiErrorHandler apiErrorHandler = new ApiErrorHandler();
        ResponseEntity<Map<String, Object>> response = apiErrorHandler
                .handleException(new IllegalStateException("test"), new ServletWebRequest(request));
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(500, response.getBody().get("status"));
        Assertions.assertEquals("test", response.getBody().get("error"));
        Assertions.assertEquals("/test", response.getBody().get("path"));
        Assertions.assertNotNull(response.getBody().get("timestamp"));
    }
}
