package com.wehavescience.repositories;

import com.wehavescience.entities.Person;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;

import java.util.List;

/**
 * @author Gabriel Francisco  <gabfssilva@gmail.com>
 */
@Repository
public interface PersonRepository extends EntityRepository<Person, Long> {
    @Query("SELECT p FROM Person p where age = ?1")
    List<Person> findAllByAge(Integer age);
}
