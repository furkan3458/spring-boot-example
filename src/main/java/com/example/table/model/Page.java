package com.example.table.model;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Page<T> {

    public Page(List<T> data) {
        this.data = data;
    }

    private List<T> data;
    private int recordsFiltered;
    private int recordsTotal;
    private int draw;

}
