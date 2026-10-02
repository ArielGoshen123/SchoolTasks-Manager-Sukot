package com.example.test3;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class DetailsActivity extends AppCompatActivity {

    private TextView tvId, tvTitle, tvType, tvSubject, tvPriority, tvDueDate, tvExtra, tvStatus, tvPoints;
    private Button btnToggleDone, btnBack, btnDelete;

    private TaskStorage taskStorage;
    private Task currentTask;
    private int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);

        tvId = findViewById(R.id.tvId);
        tvTitle = findViewById(R.id.tvTitle);
        tvType = findViewById(R.id.tvType);
        tvSubject = findViewById(R.id.tvSubject);
        tvPriority = findViewById(R.id.tvPriority);
        tvDueDate = findViewById(R.id.tvDueDate);
        tvExtra = findViewById(R.id.tvExtra);
        tvStatus = findViewById(R.id.tvStatus);
        tvPoints = findViewById(R.id.tvPoints);

        btnToggleDone = findViewById(R.id.btnToggleDone);
        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);

        taskStorage = new TaskStorage(this);

        //Get task ID passed from S2
        taskId = getIntent().getIntExtra("TASK_ID", -1);
        if (taskId == -1) {
            Toast.makeText(this, "Error: Task not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        displayTaskDetails();
        setupListeners();
    }

    //Displays details
    private void displayTaskDetails() {
        currentTask = taskStorage.findById(taskId);
        if (currentTask == null) {
            finish();
            return;
        }

        tvId.setText("ID: " + currentTask.getId());
        tvTitle.setText("Title: " + currentTask.getTitle());
        tvType.setText("Type: " + currentTask.getTypeName());
        tvSubject.setText("Subject: " + currentTask.getSubject());
        tvPriority.setText("Priority: " + currentTask.getPriority());
        tvDueDate.setText("Due Date: " + currentTask.getDueDate());

        if (currentTask instanceof ExamTask) {
            tvExtra.setText("Type Detail: Exam");
        } else if (currentTask instanceof HomeworkTask) {
            tvExtra.setText("Type Detail: Homework");
        }

        tvStatus.setText("Status: " + (currentTask.isDone() ? "Completed" : "Pending"));
        tvPoints.setText("Points: " + currentTask.getPoints());
        btnToggleDone.setText(currentTask.isDone() ? "Mark as Pending" : "Mark as Completed");
    }

    private void setupListeners() {
        //Toggle status
        btnToggleDone.setOnClickListener(v -> {
            boolean newStatus = !currentTask.isDone();
            currentTask.setDone(newStatus);
            taskStorage.updateTask(currentTask);
            displayTaskDetails();
            Toast.makeText(this, newStatus ? "Marked as completed!" : "Marked as pending", Toast.LENGTH_SHORT).show();
        });

        //Delete | Dialog
        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(DetailsActivity.this)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        taskStorage.deleteById(taskId);
                        Toast.makeText(DetailsActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Back button
        btnBack.setOnClickListener(v -> finish());
    }
}