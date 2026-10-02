package com.example.test3;

public class ExamTask extends Task {

    private int topics;

    public ExamTask(int id, String title, String subject,
                    String priority, String dueDate, int topics) {
        super(id, title, subject, priority, dueDate);
        this.topics = topics;
    }

    @Override
    public String getTypeName() {
        return "ExamTask";
    }

    @Override
    public int getPoints() {
        return 5 * topics + 10 + getPriorityBonus();
    }

    @Override
    public String toString() {
        return super.toString() + ", topics: " + topics;
    }
}