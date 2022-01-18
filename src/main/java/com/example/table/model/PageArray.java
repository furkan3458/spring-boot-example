package com.example.table.model;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PageArray {

    private List<List<String>> data;
    private int recordsFiltered;
    private int recordsTotal;
    private int draw;
}