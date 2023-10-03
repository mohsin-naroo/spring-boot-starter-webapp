package io.github.meritepk.webapp.language;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, Long> {

    Language findByCode(String code);

    List<Language> findByIdIsNotNullOrderByCode();
}
