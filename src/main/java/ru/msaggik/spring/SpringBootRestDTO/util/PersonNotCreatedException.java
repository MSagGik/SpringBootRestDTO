package ru.msaggik.spring.SpringBootRestDTO.util;

public class PersonNotCreatedException extends RuntimeException{
    // переопределение дефолтного конструктора
    public PersonNotCreatedException(String msg) {
        super(msg);
    }
}
