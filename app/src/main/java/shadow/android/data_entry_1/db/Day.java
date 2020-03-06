package shadow.android.data_entry_1.db;

import java.util.Date;

public class Day {
    private long id;
    private int index;
    private Date day;
    private double breakfast;
    private double lunch;
    private double dinner;
    private String breakfastInfo;
    private String lunchInfo;
    private String dinnerInfo;
    private long period;

    public Day(long id, Date day, double breakfast, double lunch, double dinner, long period) {
        this.id = id;
        this.day = day;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
        this.period = period;
    }

    public Day() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Date getDay() {
        return day;
    }

    public void setDay(long day) {
        this.day = new Date(day);
    }

    public double getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(double breakfast) {
        this.breakfast = breakfast;
    }

    public double getLunch() {
        return lunch;
    }

    public void setLunch(double lunch) {
        this.lunch = lunch;
    }

    public double getDinner() {
        return dinner;
    }

    public void setDinner(double dinner) {
        this.dinner = dinner;
    }

    public String getBreakfastInfo() {
        return breakfastInfo;
    }

    public void setBreakfastInfo(String breakfastInfo) {
        this.breakfastInfo = breakfastInfo;
    }

    public String getLunchInfo() {
        return lunchInfo;
    }

    public void setLunchInfo(String lunchInfo) {
        this.lunchInfo = lunchInfo;
    }

    public String getDinnerInfo() {
        return dinnerInfo;
    }

    public void setDinnerInfo(String dinnerInfo) {
        this.dinnerInfo = dinnerInfo;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }
}
