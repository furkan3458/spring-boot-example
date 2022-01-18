package com.example.comparator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.example.model.CourseStudent;
import com.example.table.model.Direction;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public class CourseStudentComparator {

	@EqualsAndHashCode
    @AllArgsConstructor
    @Getter
    static class Key {
        String name;
        Direction dir;
    }

    static Map<Key, Comparator<CourseStudent>> map = new HashMap<>();

    static {
        map.put(new Key("id", Direction.asc), Comparator.comparing(CourseStudent::getId));
        map.put(new Key("id", Direction.desc), Comparator.comparing(CourseStudent::getId)
                                                           .reversed());
        
        map.put(new Key("student.name", Direction.asc), Comparator.comparing(CourseStudent::getStudentName));
        map.put(new Key("student.name", Direction.desc), Comparator.comparing(CourseStudent::getStudentName)
                                                           .reversed());
        
        map.put(new Key("student.number", Direction.asc), Comparator.comparing(CourseStudent::getStudentNumber));
        map.put(new Key("student.number", Direction.desc), Comparator.comparing(CourseStudent::getStudentNumber)
                                                           .reversed());

        map.put(new Key("subject.subject", Direction.asc), Comparator.comparing(CourseStudent::getSubjectName));
        map.put(new Key("subject.subject", Direction.desc), Comparator.comparing(CourseStudent::getSubjectName)
                                                           .reversed());
        
        map.put(new Key("teacher.name", Direction.asc), Comparator.comparing(CourseStudent::getTeacherName));
        map.put(new Key("teacher.name", Direction.desc), Comparator.comparing(CourseStudent::getTeacherName)
                                                           .reversed());
    }
    
    public static Comparator<CourseStudent> getComparator(String name, Direction dir) {
        return map.get(new Key(name, dir));
    }

    private CourseStudentComparator() {
    }
}
