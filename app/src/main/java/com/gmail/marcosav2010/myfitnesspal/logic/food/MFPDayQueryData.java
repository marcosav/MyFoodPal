package com.gmail.marcosav2010.myfitnesspal.logic.food;

import java.util.Date;

import lombok.Data;

@Data
public class MFPDayQueryData {

    private String meals;
    private boolean buy;
    private Date date;
}
