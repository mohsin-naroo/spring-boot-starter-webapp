package io.github.meritepk.webapp.person;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.meritepk.webapp.api.ApiResponse;
import io.github.meritepk.webapp.util.EntityUtils;

@RestController
@RequestMapping("/api/v1/persons")
public class PersonController {

    private final PersonService service;

    public PersonController(PersonService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Person.PersonInfo>>> get(HttpServletRequest request,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "100") int size) {
        List<Person> values = service.findAll(page, size).getContent();
        List<Person.PersonInfo> records = new ArrayList<>(values.size());
        for (Person value : values) {
            records.add(Person.from(value));
        }
        return ResponseEntity.ok(ApiResponse.of(records));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person.PersonInfo> get(HttpServletRequest request, @PathVariable("id") String id) {
        Person value = service.findById(EntityUtils.decode(id));
        if (value == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Person.from(value));
    }

    @PostMapping
    public ResponseEntity<Person.PersonInfo> create(@RequestBody Person.PersonInfo info) {
        Person value = service.create(Person.from(info));
        if (value != null) {
            return ResponseEntity.ok(Person.from(value));
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @PostMapping("/{id}")
    public ResponseEntity<Person.PersonInfo> update(@PathVariable("id") String id,
            @RequestBody Person.PersonInfo info) {
        Person value = Person.from(info);
        value.setId(EntityUtils.decode(id));
        Person updated = service.update(value);
        if (updated != null) {
            return ResponseEntity.ok(Person.from(updated));
        }
        return ResponseEntity.notFound().build();
    }
}
