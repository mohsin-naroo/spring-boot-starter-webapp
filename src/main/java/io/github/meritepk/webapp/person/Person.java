package io.github.meritepk.webapp.person;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.github.meritepk.webapp.util.EntityUtils;

@Entity
@Table(name = "persons")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Long countryId;
    private Long languageId;
    private Long userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Long languageId) {
        this.languageId = languageId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public record PersonInfo(String id, String email, String firstName, String lastName, LocalDate dateOfBirth,
            String countryId, String languageId, String userId) {
        public PersonInfo(Long id, String email, String firstName, String lastName, LocalDate dateOfBirth,
                Long countryId, Long languageId, Long userId) {
            this(EntityUtils.encode(id), email, firstName, lastName, dateOfBirth, EntityUtils.encode(countryId),
                    EntityUtils.encode(languageId), EntityUtils.encode(userId));
        }
    }

    public static Person from(PersonInfo value) {
        Person account = new Person();
        account.id = EntityUtils.decode(value.id);
        account.email = value.email;
        account.firstName = value.firstName;
        account.lastName = value.lastName;
        account.dateOfBirth = value.dateOfBirth;
        account.countryId = EntityUtils.decode(value.countryId);
        account.languageId = EntityUtils.decode(value.languageId);
        account.userId = EntityUtils.decode(value.userId);
        return account;
    }

    public static PersonInfo from(Person value) {
        return new PersonInfo(value.id, value.email, value.firstName, value.lastName, value.dateOfBirth,
                value.countryId, value.languageId, value.userId);
    }
}
