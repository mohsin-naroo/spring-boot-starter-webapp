package io.github.meritepk.webapp.country;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.github.meritepk.webapp.util.EntityUtils;

@Entity
@Table(name = "countries")
public class Country {

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

    public record CountryInfo(String id, String code, String name) {
        public CountryInfo(Long id, String code, String name) {
            this(EntityUtils.encode(id), code, name);
        }
    }

    public static CountryInfo from(Country value) {
        return new CountryInfo(value.id, value.code, value.name);
    }

    public static Country from(CountryInfo value) {
        Country country = new Country();
        country.id = EntityUtils.decode(value.id);
        country.code = value.code;
        country.name = value.name;
        return country;
    }
}
