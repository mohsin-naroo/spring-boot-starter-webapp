# spring-boot-starter-webapp

![Build Workflow](https://github.com/mohsin-naroo/spring-boot-starter-webapp/actions/workflows/maven-build.yml/badge.svg)

Spring Boot starter web application for backend(REST API) as well as frontend (HTML5, JavaScript, CSS)

## Frontend

- [HTML](https://www.w3schools.com/html/)
- [JavaScript](https://www.w3schools.com/js/)
- [CSS](https://www.w3schools.com/css/)
- [AngularJS](https://www.w3schools.com/angular/)
- [Bootstrap](https://www.w3schools.com/bootstrap5/)

## Backend

- [Spring Boot](https://start.spring.io/)
    - Spring Web MVC - REST Controller
    - Spring AOP - Exception Handling
    - Spring @Cacheable - Transparently adding caching
    - Spring @Scheduled - Scheduling tasks
    - Spring @Async - Asynchronous task execution support 
    - Spring Security - Token based authentication and authorization
    - Spring Data JPA - A data access layer based on JPA
    - Spring Boot Auto Configuration - Automatically configure application based on the dependencies
    - Spring Boot Logging - SLF4J, LOGBACK, configuration and logging on console and file
    - Spring Boot JSON/YAML - Consuming and Producing JSON
    - Spring Boot Actuator - Production-ready Features
    - [Liquibase](https://docs.liquibase.com/change-types/home.html) - Version control for database scripts

## Code Quality

- [CheckStyle](https://checkstyle.sourceforge.io/) - Coding standard
- [PMD](https://pmd.github.io/) - Static code analyzer
- [SpotBugs](https://spotbugs.github.io/) - Find common and security bugs
- [JUnit5](https://www.baeldung.com/junit-5) - Developer-side testing
- [JaCoCo](https://www.eclemma.org/jacoco/trunk/index.html) - Java Code Coverage
- [OWASP](https://cheatsheetseries.owasp.org/IndexTopTen.html) - Detect publicly disclosed vulnerabilities in dependencies

## Other

- [springdoc-openapi v1](https://springdoc.org/v1/) - Automatically generate REST API documentation
    - Comment springdoc-openapi-ui dependency scope in `pom.xml` i.e. `<!-- <scope>test</scope> -->`
- [Spring Boot Admin](https://docs.spring-boot-admin.com/2.7.11/) - Visualize actuators information to manage and monitor the applications
    - Comment spring-boot-admin-starter-server, spring-boot-admin-starter-client dependencies scope in `pom.xml` i.e. `<!-- <scope>test</scope> -->`
    - Uncomment `@EnableAdminServer` and resolve import in `src\main\java\io\github\meritepk\webapp\api\ApiAppConfiguration.java`

## Requirements

- [Java 17](https://www.oracle.com/pk/java/technologies/downloads/#java17)
- [Maven 3](https://maven.apache.org)

## Running the application locally

- Comment or remove H2 database dependency scope in `pom.xml` i.e. `<!-- <scope>test</scope> -->`

- Execute the `main` method of `io.github.meritepk.webapp.Application` class from IDE

or

- Use [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like `mvn spring-boot:run`

Open [http://localhost:8080/webapp/](http://localhost:8080/webapp/) in web browser
