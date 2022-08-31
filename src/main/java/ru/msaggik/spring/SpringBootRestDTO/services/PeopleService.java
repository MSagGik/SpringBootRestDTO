package ru.msaggik.spring.SpringBootRestDTO.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.msaggik.spring.SpringBootRestDTO.model.Person;
import ru.msaggik.spring.SpringBootRestDTO.repositories.PeopleRepository;
import ru.msaggik.spring.SpringBootRestDTO.util.PersonNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService {

    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public List<Person> findAll() {
        return peopleRepository.findAll();
    }

    public Person findOne(int id) {
        Optional<Person> foundPerson = peopleRepository.findById(id);
        return foundPerson.orElseThrow(PersonNotFoundException::new);
    }

    // метод приёма объекта из контроллера и сохранения его в БД
    @Transactional
    public void save(Person person) {
        // назначение дополнительных полей объекту Person новым методом
        enrichPerson(person);
        peopleRepository.save(person);
    }
    // метод автоматического присваивания значений дополнительным полям
    private void enrichPerson(Person person) {
        person.setCreatedAt(LocalDateTime.now()); // текущее время в миллисекундах
        person.setUpdatedAt(LocalDateTime.now()); // текущее время в миллисекундах
        person.setCreatedWho("ADMIN"); // пример одного админа
    }
}
