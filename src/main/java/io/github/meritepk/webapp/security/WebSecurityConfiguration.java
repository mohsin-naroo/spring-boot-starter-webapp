package io.github.meritepk.webapp.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfiguration implements WebMvcConfigurer {

    @Value("${webapp.security.ignored:/,/favicon.ico,/error,/ui/**,/webjars/**}")
    private String[] ignored = { "/", "/favicon.ico", "/error", "/ui/**", "/webjars/**" };

    @Value("${webapp.security.api:/api/**}")
    private String api = "/api/**";

    private final WebSecurityService webSecurityService;

    public WebSecurityConfiguration(WebSecurityService webSecurityService) {
        this.webSecurityService = webSecurityService;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().antMatchers(ignored);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // state less / no session
        http.sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // configure double submit cookie
        http.csrf(config -> config.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
        // bearer token authentication
        http.oauth2ResourceServer(config -> config.opaqueToken());
        http.logout().disable();
        // token end point permit all
        http.authorizeRequests(config -> config.mvcMatchers(HttpMethod.POST, WebSecurityService.TOKEN_URL).permitAll()
                .anyRequest().authenticated());
        // build and return
        return http.build();
    }

    @Bean
    public OpaqueTokenIntrospector opaqueTokenIntrospector() {
        return webSecurityService::introspectToken;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                    throws Exception {
                return webSecurityService.handlePreAuthorize(request, response, handler);
            }
        }).addPathPatterns(api);
    }
}
