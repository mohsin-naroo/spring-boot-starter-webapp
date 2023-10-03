package io.github.meritepk.webapp.security;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerMapping;

import io.github.meritepk.webapp.user.User;
import io.github.meritepk.webapp.user.UserService;
import io.github.meritepk.webapp.util.DigestUtils;
import io.github.meritepk.webapp.util.LogUtils;

@Service
public class WebSecurityService implements UserDetailsService {

    public static final String ACCESS_TOKEN = "access_token";
    public static final String TOKEN_URL = "/api/v1/token";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public WebSecurityService(UserService userService) {
        this.userService = userService;
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        throw new UsernameNotFoundException("User " + username + " not verified");
    }

    public Map<String, String> token(HttpServletRequest request, HttpServletResponse response, String userName,
            String password, String cookieToken) {
        User user = null;
        if (StringUtils.hasText(userName) && StringUtils.hasText(password)) {
            logger.info("Security::User login: {}", LogUtils.sanitize(userName));
            User found = userService.findByUserName(userName);
            if (found != null && passwordEncoder.matches(password, found.getPassword())) {
                user = found;
            } else {
                logger.warn("Security::User login failed: {}", LogUtils.sanitize(userName));
            }
        } else {
            if (StringUtils.hasText(cookieToken)) {
                String token = decodeToken(cookieToken);
                user = verifyUserFromToken(token);
                if (user != null) {
                    logger.info("Security::User token: {}", user.getUserName());
                } else {
                    logger.warn("Security::User token failed: {}", token);
                }
            }
        }
        if (user != null) {
            String token = user.getUserName() + "-" + DigestUtils.sha256(user.getId() + "-" + UUID.randomUUID());
            user.setLoginAt(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
            user.setSessionId(DigestUtils.sha256(token + "-" + user.getLoginAt()));
            userService.updateLoginSession(user);
            String tokenCookie = buildTokenCookie(request, encodeToken(token), Duration.ofSeconds(-1));
            response.setHeader(HttpHeaders.SET_COOKIE, tokenCookie);
            return Map.of("token_type", "Bearer", ACCESS_TOKEN, token);
        }
        return null;
    }

    private String encodeToken(String value) {
        return value;
    }

    private String decodeToken(String value) {
        return value;
    }

    private String buildTokenCookie(HttpServletRequest request, String token, Duration maxAge) {
        String cookiePath = request.getContextPath();
        cookiePath = (StringUtils.hasText(cookiePath) ? cookiePath : "") + TOKEN_URL;
        return ResponseCookie.from(ACCESS_TOKEN, token)
                .httpOnly(true)
                .secure(request.isSecure())
                .path(cookiePath)
                .maxAge(maxAge)
                .build().toString();
    }

    private User verifyUserFromToken(String token) {
        int index = token.indexOf('-');
        if (index > 0) {
            User user = userService.findByUserName(token.substring(0, index));
            if (user != null) {
                return verifyUserLogin(token, user);
            }
        }
        return null;
    }

    private User verifyUserLogin(String token, User user) {
        user = userService.findById(user.getId());
        if (user != null && user.getLoginAt() != null && user.getSessionId() != null
                && user.getSessionId().equals(DigestUtils.sha256(token + "-" + user.getLoginAt()))) {
            return user;
        }
        return null;
    }

    public DefaultOAuth2User introspectToken(String token) {
        User user = verifyUserFromToken(token);
        if (user != null) {
            String[] permissions = user.getPermissions().split(",");
            return new DefaultOAuth2User(AuthorityUtils.createAuthorityList(permissions),
                    Map.of("sub", user.getUserName(), "user", user, "permissions", Set.of(permissions)), "sub");
        } else {
            logger.warn("Security::User token expired: {}", token);
        }
        throw new CredentialsExpiredException("Credentials Expired");
    }

    public boolean handlePreAuthorize(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        boolean authorized = false;
        String uriPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestMethod = request.getMethod();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = "";
        if (auth != null && uriPattern != null) {
            userName = auth.getName();
            if (auth instanceof AnonymousAuthenticationToken
                    || uriPattern.startsWith(WebSecurityService.TOKEN_URL)) {
                authorized = true;
            } else if (auth.getPrincipal() instanceof DefaultOAuth2User) {
                DefaultOAuth2User oauth = (DefaultOAuth2User) auth.getPrincipal();
                Set<String> permissions = oauth.getAttribute("permissions");
                if (permissions != null) {
                    authorized = permissions.contains(uriPattern)
                            || permissions.contains(requestMethod + " " + uriPattern);
                }
            }
        }
        if (!authorized) {
            logger.warn("Security::User forbidden: {}, {} {}", userName, requestMethod, uriPattern);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
        return authorized;
    }

    public void tokenRevoke(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
        logger.info("Security::User logout: {}", LogUtils.sanitize(auth.getName()));
        if (auth.getPrincipal() instanceof DefaultOAuth2User) {
            DefaultOAuth2User oauth = (DefaultOAuth2User) auth.getPrincipal();
            User user = oauth.getAttribute("user");
            if (user != null) {
                user.setSessionId(null);
                userService.updateLoginSession(user);
            }
        }
        String tokenCookie = buildTokenCookie(request, "", Duration.ofSeconds(0));
        response.setHeader(HttpHeaders.SET_COOKIE, tokenCookie);
    }
}
