package com.example.comparator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.example.model.Course;
import com.example.table.model.Direction;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public class CourseComparator {

	@EqualsAndHashCode
    @AllArgsConstructor
    @Getter
    static class Key {
        String name;
        Direction dir;
    }

    static Map<Key, Comparator<Course>> map = new HashMap<>();

    static {
        map.put(new Key("id", Direction.asc), Comparator.comparing(Course::getId));
        map.put(new Key("id", Direction.desc), Comparator.comparing(Course::getId)
                                                           .reversed());
        
        map.put(new Key("teacher.name", Direction.asc), Comparator.comparing(Course::getTeacherName));
        map.put(new Key("teacher.name", Direction.desc), Comparator.comparing(Course::getTeacherName)
                                                           .reversed());
        
        map.put(new Key("subject.subject", Direction.asc), Comparator.comparing(Course::getSubjectName));
        map.put(new Key("subject.subject", Direction.desc), Comparator.comparing(Course::getSubjectName)
                                                           .reversed());
    }

    public static Comparator<Course> getComparator(String name, Direction dir) {
        return map.get(new Key(name, dir));
    }

    private CourseComparator() {
    }
}
