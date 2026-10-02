package com.example.test3;

public class Task implements Rewardable {

    private int id;
    private String title;
    private String subject;
    private String priority;
    private String dueDate;
    private boolean done;

    public Task(int id, String title, String subject, String priority, String dueDate) {
        this.id = id;
        this.title = title;
        this.subject = subject;
        this.priority = priority;
        this.dueDate = dueDate;
        this.done = false;
    }

    public String getTypeName() {
        return "Task";
    }

    @Override
    public int getPoints() {
        return getPriorityBonus();
    }

    protected int getPriorityBonus() {
        if (priority.equalsIgnoreCase("High")) {
            return 5;
        } else if (priority.equalsIgnoreCase("Medium")) {
            return 3;
        } else {
            return 1;
        }
    }

    @Override
    public String toString() {
        return "Task: " +
                id + ", " +
                title + ", " +
                subject + ", " +
                priority + ", " +
                dueDate + ", " +
                done;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubject() {
        return subject;
    }

    public String getPriority() {
        return priority;
    }

    public String getDueDate() {
        return dueDate;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}