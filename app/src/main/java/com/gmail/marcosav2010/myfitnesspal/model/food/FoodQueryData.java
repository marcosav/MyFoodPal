package com.gmail.marcosav2010.myfitnesspal.model.food;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FoodQueryData implements Parcelable {

    private String meals;
    private boolean buy;
    private Date date;
    private Date toDate;

    public static final Creator<FoodQueryData> CREATOR = new Creator<FoodQueryData>() {
        @Override
        public FoodQueryData createFromParcel(Parcel in) {
            return new FoodQueryData(in);
        }

        @Override
        public FoodQueryData[] newArray(int size) {
            return new FoodQueryData[size];
        }
    };

    public FoodQueryData() {
    }

    protected FoodQueryData(Parcel in) {
        meals = in.readString();
        buy = in.readByte() != 0;
        date = new Date(in.readLong());
        toDate = new Date(in.readLong());
    }

    public Set<Date> getDates() {
        Set<Date> datesInRange = new HashSet<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(buy ? toDate : date);
        endCalendar.set(Calendar.HOUR_OF_DAY, 23);

        while (calendar.before(endCalendar)) {
            Date result = calendar.getTime();
            datesInRange.add(result);
            calendar.add(Calendar.DATE, 1);
        }

        return datesInRange;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(meals);
        parcel.writeByte((byte) (buy ? 1 : 0));
        parcel.writeLong(date.getTime());
        parcel.writeLong(toDate.getTime());
    }
}
