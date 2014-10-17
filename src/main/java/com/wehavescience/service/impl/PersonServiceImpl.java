package com.wehavescience.service.impl;

import com.wehavescience.entities.Person;
import com.wehavescience.repositories.PersonRepository;
import com.wehavescience.service.PersonService;
import org.apache.deltaspike.jpa.api.transaction.Transactional;

import javax.inject.Inject;

/**
 * @author Gabriel Francisco  <gabfssilva@gmail.com>
 */
public class PersonServiceImpl implements PersonService {
    @Inject
    private PersonRepository personRepository;

    @Override
    @Transactional
    public void save(Person person) {
        personRepository.save(person);
    }

    @Override
    public Person fetch(Long id){
        return personRepository.findBy(id);
    }

    @Override
    public Long size(){
        return personRepository.count();
    }
}
