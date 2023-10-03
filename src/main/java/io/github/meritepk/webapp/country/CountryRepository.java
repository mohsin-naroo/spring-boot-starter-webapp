package io.github.meritepk.webapp.country;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {

    Country findByCode(String code);

    List<Country> findByIdIsNotNullOrderByCode();
}
