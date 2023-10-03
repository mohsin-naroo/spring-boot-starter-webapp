package io.github.meritepk.webapp.language;

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
@RequestMapping("/api/v1/languages")
public class LanguageController {

    private LanguageService service;

    public LanguageController(LanguageService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Language.LanguageInfo>>> get() {
        List<Language> values = service.findAllRecords();
        List<Language.LanguageInfo> records = new ArrayList<>(values.size());
        for (Language value : values) {
            records.add(Language.from(value));
        }
        return ResponseEntity.ok(ApiResponse.of(records));
    }

    @PostMapping
    public ResponseEntity<Language.LanguageInfo> create(@RequestBody Language.LanguageInfo info) {
        Language created = service.create(Language.from(info));
        if (created != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(Language.from(created));
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}
