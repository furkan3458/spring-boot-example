package com.example.comparator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.example.model.Teacher;
import com.example.table.model.Direction;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public class TeacherComparator {

	@EqualsAndHashCode
    @AllArgsConstructor
    @Getter
    static class Key {
        String name;
        Direction dir;
    }

    static Map<Key, Comparator<Teacher>> map = new HashMap<>();

    static {
        map.put(new Key("id", Direction.asc), Comparator.comparing(Teacher::getId));
        map.put(new Key("id", Direction.desc), Comparator.comparing(Teacher::getId)
                                                           .reversed());
        
        map.put(new Key("name", Direction.asc), Comparator.comparing(Teacher::getName));
        map.put(new Key("name", Direction.desc), Comparator.comparing(Teacher::getName)
                                                           .reversed());
    }

    public static Comparator<Teacher> getComparator(String name, Direction dir) {
        return map.get(new Key(name, dir));
    }
    
    public TeacherComparator() {
    }
}
