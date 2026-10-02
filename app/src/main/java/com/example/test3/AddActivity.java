package com.example.test3;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddActivity extends AppCompatActivity {

    private Spinner spType, spSubject, spPriority;
    private EditText etTitle, etDueDate, etAmount;
    private Button btnSave, btnCancel;

    private TaskStorage taskStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        spType = findViewById(R.id.spType);
        spSubject = findViewById(R.id.spSubject);
        spPriority = findViewById(R.id.spPriority);
        etTitle = findViewById(R.id.etTitle);
        etDueDate = findViewById(R.id.etDueDate);
        etAmount = findViewById(R.id.etAmount);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        taskStorage = new TaskStorage(this);

        setupSpinners();
        setupListeners();
    }

    private void setupSpinners() {
        //Task Type
        String[] types = {"Homework", "Exam"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(typeAdapter);

        //Subject
        String[] subjects = {"Math", "English", "History", "Science", "Computer Science"};
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjects);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSubject.setAdapter(subjectAdapter);

        //Priority
        String[] priorities = {"High", "Medium", "Low"};
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priorities);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPriority.setAdapter(priorityAdapter);
    }

    private void setupListeners() {
        //Change hint based on task type
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = spType.getSelectedItem().toString();
                if (selectedType.equals("Exam")) {
                    etAmount.setHint("Number of Topics");
                } else {
                    etAmount.setHint("Number of Exercises");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //Save Button
        btnSave.setOnClickListener(v -> saveTask());

        //Cancel Button
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveTask() {
        String title = etTitle.getText().toString().trim();
        String dueDate = etDueDate.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String type = spType.getSelectedItem().toString();
        String subject = spSubject.getSelectedItem().toString();
        String priority = spPriority.getSelectedItem().toString();

        //If empty fields
        if (title.isEmpty() || dueDate.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        //Checks for negative or too high numbers
        long parsedAmount = Long.parseLong(amountStr);
        if (parsedAmount <= 0 || parsedAmount > Integer.MAX_VALUE) {
            Toast.makeText(this, "Amount must be between 1 and 2,147,483,647", Toast.LENGTH_SHORT).show();
            return;
        }
        int amount = (int)parsedAmount;

        int newId = taskStorage.nextId();
        Task newTask;

        //Creates corresponding class based on selection
        if (type.equals("Exam")) {
            newTask = new ExamTask(newId, title, subject, priority, dueDate, amount);
        } else {
            newTask = new HomeworkTask(newId, title, subject, priority, dueDate, amount);
        }

        taskStorage.addTask(newTask);
        Toast.makeText(this, "Task saved successfully!", Toast.LENGTH_SHORT).show();
        finish(); //S3-S2
    }
}