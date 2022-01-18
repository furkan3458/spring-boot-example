package com.example.comparator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.example.model.Subject;
import com.example.table.model.Direction;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public class SubjectComparator {

	@EqualsAndHashCode
    @AllArgsConstructor
    @Getter
    static class Key {
        String name;
        Direction dir;
    }

    static Map<Key, Comparator<Subject>> map = new HashMap<>();

    static {
        map.put(new Key("id", Direction.asc), Comparator.comparing(Subject::getId));
        map.put(new Key("id", Direction.desc), Comparator.comparing(Subject::getId)
                                                           .reversed());
        
        map.put(new Key("subject", Direction.asc), Comparator.comparing(Subject::getSubject));
        map.put(new Key("subject", Direction.desc), Comparator.comparing(Subject::getSubject)
                                                           .reversed());
    }

    public static Comparator<Subject> getComparator(String name, Direction dir) {
        return map.get(new Key(name, dir));
    }
    
    public SubjectComparator() {
    }
}
