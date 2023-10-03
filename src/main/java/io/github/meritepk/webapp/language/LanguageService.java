package io.github.meritepk.webapp.language;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class LanguageService {

    private LanguageRepository repository;

    public LanguageService(LanguageRepository repository) {
        this.repository = repository;
    }

    public List<Language> findAllRecords() {
        return repository.findByIdIsNotNullOrderByCode();
    }

    public Language create(Language language) {
        Language existing = repository.findByCode(language.getCode());
        if (existing == null) {
            return repository.save(language);
        }
        return null;
    }
}
