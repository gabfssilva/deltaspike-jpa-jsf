package com.wehavescience.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Gabriel Francisco  <gabfssilva@gmail.com>
 */
@Entity
@Table(name = "people")
public class Person {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private Integer age;
    private String email;
    private String information;

    public Person(Long id, String name, Integer age, String email, String information) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.information = information;
    }

    public Person() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", information='" + information + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (age != null ? !age.equals(person.age) : person.age != null) return false;
        if (email != null ? !email.equals(person.email) : person.email != null) return false;
        if (id != null ? !id.equals(person.id) : person.id != null) return false;
        if (information != null ? !information.equals(person.information) : person.information != null) return false;
        if (name != null ? !name.equals(person.name) : person.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (age != null ? age.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (information != null ? information.hashCode() : 0);
        return result;
    }
}
