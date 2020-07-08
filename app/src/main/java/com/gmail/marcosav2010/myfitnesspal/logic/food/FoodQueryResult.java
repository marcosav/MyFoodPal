package com.gmail.marcosav2010.myfitnesspal.logic.food;

import com.gmail.marcosav2010.myfitnesspal.R;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "from")
public class FoodQueryResult {

    @Getter
    private final Type type;
    @Getter
    private final List<ListElement> result;

    @NoArgsConstructor
    @AllArgsConstructor
    public enum Type {
        UNKNOWN_ERROR(R.string.food_result_unknown_error),
        IO_ERROR(R.string.result_io_error),
        NO_SESSION(R.string.food_result_no_session_error),
        NO_INTERNET_ERROR(R.string.result_no_internet_error),
        SUCCESS;

        @Getter
        int msg;
    }
}