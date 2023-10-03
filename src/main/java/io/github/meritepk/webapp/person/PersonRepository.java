package io.github.meritepk.webapp.person;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Person findByEmail(String email);
}
