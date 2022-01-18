package com.example.table.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Search {

    private String value;
    private String regexp;
}
