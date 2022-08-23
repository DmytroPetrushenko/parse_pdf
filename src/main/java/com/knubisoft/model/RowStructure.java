package com.knubisoft.model;

import com.knubisoft.annotation.Lookup;
import lombok.Data;

@Data
public class RowStructure {

    @Lookup(regex = "[A-Z][a-z]+( [A-Z][a-z]+)*")
    private String category;

    @Lookup(regex = "([0-9]+,[0-9]+\\h[A-Z]{3})")
    private String budget;

    @Lookup(regex = "([0-9]+,[0-9]+\\h[A-Z]{3})")
    private String actual;

}
