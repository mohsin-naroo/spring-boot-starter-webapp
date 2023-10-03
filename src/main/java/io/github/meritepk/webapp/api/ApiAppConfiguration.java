package io.github.meritepk.webapp.api;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

//@EnableAdminServer
@EnableAsync
@EnableScheduling
@EnableCaching
@Configuration
public class ApiAppConfiguration {

}
