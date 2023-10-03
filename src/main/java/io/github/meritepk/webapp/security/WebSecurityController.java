package io.github.meritepk.webapp.security;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSecurityController {

    private final WebSecurityService service;

    public WebSecurityController(WebSecurityService service) {
        this.service = service;
    }

    @PostMapping(WebSecurityService.TOKEN_URL)
    public ResponseEntity<Map<String, String>> token(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "password", required = false) String password,
            @CookieValue(name = WebSecurityService.ACCESS_TOKEN, required = false) String cookieToken) {
        Map<String, String> token = service.token(request, response, username, password, cookieToken);
        if (token != null) {
            return ResponseEntity.ok().body(token);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping(WebSecurityService.TOKEN_URL + "/revoke")
    public ResponseEntity<Void> tokenRevoke(HttpServletRequest request, HttpServletResponse response,
            Authentication auth) {
        service.tokenRevoke(request, response, auth);
        return ResponseEntity.ok().build();
    }
}
