package com.example.dilumdesilva.scheduler22;

/**
 * @DevelopedBy Dilum De Silva 2016142 | w16266371
 *
 */
public class Appointment {

    private String date;
    private String time;
    private String title;
    private String details;


    /**
     * Constructor of the Appointment class
     * @param date
     * @param time
     * @param title
     * @param details
     */
    public Appointment(String date, String time, String title, String details) {
        this.date = date;
        this.time = time;
        this.title = title;
        this.details = details;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }
}
