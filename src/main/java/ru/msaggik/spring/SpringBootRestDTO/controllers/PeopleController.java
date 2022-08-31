package ru.msaggik.spring.SpringBootRestDTO.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.msaggik.spring.SpringBootRestDTO.dto.PersonDTO;
import ru.msaggik.spring.SpringBootRestDTO.model.Person;
import ru.msaggik.spring.SpringBootRestDTO.services.PeopleService;
import ru.msaggik.spring.SpringBootRestDTO.util.PersonErrorResponse;
import ru.msaggik.spring.SpringBootRestDTO.util.PersonNotCreatedException;
import ru.msaggik.spring.SpringBootRestDTO.util.PersonNotFoundException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/people")
public class PeopleController {

    private final PeopleService peopleService;
    private final ModelMapper modelMapper;

    @Autowired
    public PeopleController(PeopleService peopleService, ModelMapper modelMapper, ModelMapper modelMapper1) {
        this.peopleService = peopleService;
        this.modelMapper = modelMapper1;
    }

    // метод возврата данных всех пользователей
    @GetMapping()
    public List<PersonDTO> getPeople() {
        // статус - 200
        // Jackson конвертирует возвращаемые объекты в JSON
//        return peopleService.findAll();
        return peopleService.findAll().stream().map(this::convertToPersonDTO)
                .collect(Collectors.toList());
    }
    // метод возврата данных одного пользователя по id
    @GetMapping("/{id}")
    public PersonDTO getPerson(@PathVariable("id") int id) {
        // статус - 200
        // Jackson конвертирует возвращаемый объект в JSON
//        return peopleService.findOne(id);
        return convertToPersonDTO(peopleService.findOne(id));
    }
    // метод обработки исключения
    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException e) {

        PersonErrorResponse response = new PersonErrorResponse(
                "Person with this id wasn't found!",
                System.currentTimeMillis()
        );
        // в HTTP ответе тело ответа (response) и статус в заголовке
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // NOT_FOUND - 404 статус
    }

    // метод создания нового пользователя
    // (с помощью @Valid и BindingResult проверяется валидность введённых данных)
    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid PersonDTO personDTO,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // если были введены не корректные данные, то приложение ответит в формате JSON об ошибке:
            // 1) создание объекта ошибки
            StringBuilder errorMsg = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getField())
                        .append(" - ").append(error.getDefaultMessage())
                        .append(";");
            }
            // 2) формирование исключения
            throw new PersonNotCreatedException(errorMsg.toString());
        }
        // сохранение в БД объекта метода конвертации personDTO в person
        peopleService.save(convertToPerson(personDTO));

        // обратная связь клиенту об успешности сохранения данных
        return ResponseEntity.ok(HttpStatus.OK);
    }

    // 3) метод обработки исключения
    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException e) {

        PersonErrorResponse response = new PersonErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        // в HTTP ответе тело ответа (response) и статус в заголовке
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // BAD_REQUEST - 400 статус
    }

    // метод конвертации personDTO в person
    private Person convertToPerson(PersonDTO personDTO) {
//        // копирование полей обычным способом
//        Person person = new Person();
//        person.setName(personDTO.getName());
//        person.setAge(personDTO.getAge());
//        person.setEmail(personDTO.getEmail());

        // копирование полей с помощью специального маппера
//        ModelMapper modelMapper = new ModelMapper(); // без внедрения данного объекта в main
        Person person = modelMapper.map(personDTO, Person.class);

        return person;
    }

    // метод обратной конвертации person в personDTO
    private PersonDTO convertToPersonDTO(Person person) {
        return modelMapper.map(person, PersonDTO.class);
    }
}
