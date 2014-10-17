package com.wehavescience.service;

import com.wehavescience.entities.Person;

/**
 * @author Gabriel Francisco  <gabfssilva@gmail.com>
 */
public interface PersonService {
    void save(Person person);
    Person fetch(Long id);
    Long size();
}
