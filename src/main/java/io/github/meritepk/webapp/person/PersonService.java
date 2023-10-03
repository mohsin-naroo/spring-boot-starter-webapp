package io.github.meritepk.webapp.person;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    @Value("${webapp.accounts.page.default:0}")
    public int pageDefault = 0;

    @Value("${webapp.accounts.page.size.default:100}")
    public int pageSizeDefault = 100;

    private PersonRepository repository;

    public PersonService(PersonRepository repository) {
        this.repository = repository;
    }

    public Page<Person> findAll(int page, int size) {
        if (page < pageDefault) {
            page = pageDefault;
        }
        if (size > pageSizeDefault || size < 1) {
            size = pageSizeDefault;
        }
        return repository.findAll(PageRequest.of(page, size));
    }

    public Person findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Person create(Person account) {
        Person existing = repository.findByEmail(account.getEmail());
        if (existing == null) {
            return repository.save(account);
        }
        return null;
    }

    public Person update(Person account) {
        Person existing = repository.findById(account.getId()).orElse(null);
        if (existing != null) {
            existing.setEmail(account.getEmail());
            existing.setFirstName(account.getFirstName());
            existing.setLastName(account.getLastName());
            existing.setDateOfBirth(account.getDateOfBirth());
            existing.setCountryId(account.getCountryId());
            existing.setLanguageId(account.getLanguageId());
            existing.setUserId(account.getUserId());
            return repository.save(existing);
        }
        return null;
    }
}
