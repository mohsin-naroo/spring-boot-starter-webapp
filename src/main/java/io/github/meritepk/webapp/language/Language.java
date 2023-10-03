package io.github.meritepk.webapp.language;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.github.meritepk.webapp.util.EntityUtils;

@Entity
@Table(name = "languages")
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public record LanguageInfo(String id, String code, String name) {
        public LanguageInfo(Long id, String code, String name) {
            this(EntityUtils.encode(id), code, name);
        }
    }

    public static LanguageInfo from(Language value) {
        return new LanguageInfo(value.id, value.code, value.name);
    }

    public static Language from(LanguageInfo value) {
        Language language = new Language();
        language.id = EntityUtils.decode(value.id);
        language.code = value.code;
        language.name = value.name;
        return language;
    }
}
