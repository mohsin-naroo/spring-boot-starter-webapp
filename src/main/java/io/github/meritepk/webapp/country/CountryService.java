package io.github.meritepk.webapp.country;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CountryService {

    private CountryRepository repository;

    public CountryService(CountryRepository repository) {
        this.repository = repository;
    }

    public List<Country> findAllRecords() {
        return repository.findByIdIsNotNullOrderByCode();
    }

    public Country create(Country country) {
        Country existing = repository.findByCode(country.getCode());
        if (existing == null) {
            return repository.save(country);
        }
        return null;
    }
}
