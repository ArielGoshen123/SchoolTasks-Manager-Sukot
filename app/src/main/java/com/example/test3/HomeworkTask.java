package com.example.test3;

public class HomeworkTask extends Task {

    private int exercises;

    public HomeworkTask(int id, String title, String subject,
                        String priority, String dueDate, int exercises) {
        super(id, title, subject, priority, dueDate);
        this.exercises = exercises;
    }

    @Override
    public String getTypeName() {
        return "HomeworkTask";
    }

    @Override
    public int getPoints() {
        return exercises*2 + getPriorityBonus();
    }

    @Override
    public String toString() {
        return super.toString() + ", exercises: " + exercises;
    }
}