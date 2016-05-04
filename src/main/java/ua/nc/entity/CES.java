package ua.nc.entity;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Ермоленко on 01.05.2016.
 */
public class CES implements Identified<Integer>{
    private Integer id;
    private Integer year;
    private Calendar startRegistrationDate;
    private Calendar endRegistrationDate;
    private Calendar startInterviewingDate;
    private Calendar endInterviewingDate;
    private Integer quota;
    private Integer reminders;
    private Integer statusId;
    private Integer interviewTimeForPerson;
    private Integer interviewTimeForDay;

    public CES(Integer year, Calendar startRegistrationDate, Calendar endRegistrationDate, Integer quota, Integer reminders, Integer statusId,
               Integer interviewTimeForPerson, Integer interviewTimeForDay){
        this.year = year;
        this.startRegistrationDate = startRegistrationDate;
        this.endRegistrationDate = endRegistrationDate;
        this.quota = quota;
        this.reminders = reminders;
        this.statusId = statusId;
        this.interviewTimeForPerson = interviewTimeForPerson;
        this.interviewTimeForDay = interviewTimeForDay;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Calendar getStartRegistrationDate() {
        return startRegistrationDate;
    }

    public void setStartRegistrationDate(Calendar startRegistrationDate) {
        this.startRegistrationDate = startRegistrationDate;
    }

    public Calendar getEndRegistrationDate() {
        return endRegistrationDate;
    }

    public void setEndRegistrationDate(Calendar endRegistrationDate) {
        this.endRegistrationDate = endRegistrationDate;
    }

    public Calendar getStartInterviewingDate() {
        return startInterviewingDate;
    }

    public void setStartInterviewingDate(Calendar startInterviewingDate) {
        this.startInterviewingDate = startInterviewingDate;
    }

    public Calendar getEndInterviewingDate() {
        return endInterviewingDate;
    }

    public void setEndInterviewingDate(Calendar endInterviewingDate) {
        this.endInterviewingDate = endInterviewingDate;
    }

    public Integer getQuota() {
        return quota;
    }

    public void setQuota(Integer quota) {
        this.quota = quota;
    }

    public Integer getReminders() {
        return reminders;
    }

    public void setReminders(Integer reminders) {
        this.reminders = reminders;
    }

    public Integer getInterviewTimeForPerson() {
        return interviewTimeForPerson;
    }

    public void setInterviewTimeForPerson(Integer interviewTimeForPerson) {
        this.interviewTimeForPerson = interviewTimeForPerson;
    }

    public Integer getInterviewTimeForDay() {
        return interviewTimeForDay;
    }

    public void setInterviewTimeForDay(Integer interviewTimeForDay) {
        this.interviewTimeForDay = interviewTimeForDay;
    }

    @Override
    public Integer getID() {
        return id;
    }

    protected void setID(int id){
        this.id = id;
    }
}

