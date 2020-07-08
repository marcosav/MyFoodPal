package com.gmail.marcosav2010.myfitnesspal.logic.food;

import lombok.Getter;
import lombok.Setter;

public class ListElement {

    @Getter
    private final int id;
    @Getter
    @Setter
    private String name = "";
    @Getter
    @Setter
    private boolean checked = true;

    public ListElement() {
        this.id = (int) System.currentTimeMillis();
    }

    public ListElement(String name) {
        this.name = name;
        this.id = name.hashCode();
    }
}
