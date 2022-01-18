package com.example.comparator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.example.model.Student;
import com.example.table.model.Direction;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public class StudentComparator {

	@EqualsAndHashCode
    @AllArgsConstructor
    @Getter
    static class Key {
        String name;
        Direction dir;
    }

    static Map<Key, Comparator<Student>> map = new HashMap<>();

    static {
        map.put(new Key("id", Direction.asc), Comparator.comparing(Student::getId));
        map.put(new Key("id", Direction.desc), Comparator.comparing(Student::getId)
                                                           .reversed());
        
        map.put(new Key("name", Direction.asc), Comparator.comparing(Student::getName));
        map.put(new Key("name", Direction.desc), Comparator.comparing(Student::getName)
                                                           .reversed());
        
        map.put(new Key("number", Direction.asc), Comparator.comparing(Student::getNumber));
        map.put(new Key("number", Direction.desc), Comparator.comparing(Student::getNumber)
                                                           .reversed());
    }

    public static Comparator<Student> getComparator(String name, Direction dir) {
        return map.get(new Key(name, dir));
    }

    private StudentComparator() {
    }
}
