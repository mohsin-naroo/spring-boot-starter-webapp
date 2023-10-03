package io.github.meritepk.webapp.country;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.meritepk.webapp.api.ApiResponse;

@RestController
@RequestMapping("/api/v1/countries")
public class CountryController {

    private CountryService service;

    public CountryController(CountryService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Country.CountryInfo>>> get() {
        List<Country> values = service.findAllRecords();
        List<Country.CountryInfo> records = new ArrayList<>(values.size());
        for (Country value : values) {
            records.add(Country.from(value));
        }
        return ResponseEntity.ok(ApiResponse.of(records));
    }

    @PostMapping
    public ResponseEntity<Country.CountryInfo> create(@RequestBody Country.CountryInfo info) {
        Country created = service.create(Country.from(info));
        if (created != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Country.from(created));
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}
