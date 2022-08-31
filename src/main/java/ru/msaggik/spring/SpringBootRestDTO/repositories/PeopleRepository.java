package ru.msaggik.spring.SpringBootRestDTO.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.msaggik.spring.SpringBootRestDTO.model.Person;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {
}
