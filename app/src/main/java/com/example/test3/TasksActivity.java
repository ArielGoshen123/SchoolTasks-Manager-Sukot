package com.example.test3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TasksActivity extends AppCompatActivity {

    private TextView tvHello, tvStats, tvEmpty;
    private Spinner spFilter;
    private ListView lvTasks;
    private Button btnAdd, btnLogout;

    private TaskStorage taskStorage;
    private ArrayList<Task> allTasksList;
    private ArrayList<Task> filteredTasksList;
    private ArrayAdapter<Task> taskAdapter;
    private ArrayAdapter<String> filterAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


        tvHello = findViewById(R.id.tvHello);
        tvStats = findViewById(R.id.tvStats);
        tvEmpty = findViewById(R.id.tvEmpty);
        spFilter = findViewById(R.id.spFilter);
        lvTasks = findViewById(R.id.lvTasks);
        btnAdd = findViewById(R.id.btnAdd);
        btnLogout = findViewById(R.id.btnLogout);

        taskStorage = new TaskStorage(this);
        filteredTasksList = new ArrayList<>();

        setupUserHeader();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }
    //Welcome message
    private void setupUserHeader() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String name = prefs.getString("user_name", "Guest");
        tvHello.setText("Hello " + name + "!"); // [Screen 2, Item 1]
    }
    //Updates on-screen data
    private void refreshData() {
        allTasksList = taskStorage.loadAll();

        updateStats();
        setupFilterSpinner();
        applyFilter(spFilter.getSelectedItem() != null ? spFilter.getSelectedItem().toString() : "All");
    }
    //Calculates Stats
    private void updateStats() {
        int total = allTasksList.size();
        int completed = 0;
        int totalPoints = 0;

        for (Task task : allTasksList) {
            if (task.isDone()) {
                completed++;
                totalPoints += task.getPoints();
            }
        }

        tvStats.setText("Tasks: " + total + " | Done: " + completed + " | Points: " + totalPoints); // [Screen 2, Item 1]
    }
    //Setups the subject options
    private void setupFilterSpinner() {
        Set<String> subjects = new HashSet<>();
        subjects.add("All");
        for (Task task : allTasksList) {
            if (task.getSubject() != null && !task.getSubject().isEmpty()) {
                subjects.add(task.getSubject());
            }
        }

        ArrayList<String> filterOptions = new ArrayList<>(subjects);
        filterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filterOptions);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFilter.setAdapter(filterAdapter);
    }
    //Creates the filtered list
    private void applyFilter(String subjectFilter) {
        filteredTasksList.clear();

        if (subjectFilter.equals("All")) {
            filteredTasksList.addAll(allTasksList);
        } else {
            for (Task task : allTasksList) {
                if (subjectFilter.equalsIgnoreCase(task.getSubject())) {
                    filteredTasksList.add(task);
                }
            }
        }

        if (filteredTasksList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            lvTasks.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            lvTasks.setVisibility(View.VISIBLE);
        }

        taskAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredTasksList);
        lvTasks.setAdapter(taskAdapter);
    }
    //Pressing the spinner
    private void setupListeners() {
        spFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSubject = parent.getItemAtPosition(position).toString();
                applyFilter(selectedSubject);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //Clicking details S2-S4
        lvTasks.setOnItemClickListener((parent, view, position, id) -> {
            Task selectedTask = filteredTasksList.get(position);
            Intent intent = new Intent(TasksActivity.this, DetailsActivity.class);
            intent.putExtra("TASK_ID", selectedTask.getId());
            startActivity(intent);
        });

        //Long-Clicking delete | confirm dialog
        lvTasks.setOnItemLongClickListener((parent, view, position, id) -> {
            Task taskToDelete = filteredTasksList.get(position);
            new AlertDialog.Builder(TasksActivity.this)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete '" + taskToDelete.getTitle() + "'?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        taskStorage.deleteById(taskToDelete.getId());
                        Toast.makeText(TasksActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
                        refreshData();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();

            return true;
        });

        //Add button S2-S3
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(TasksActivity.this, AddActivity.class);
            startActivity(intent);
        });

        //Logout button S2-S1
        btnLogout.setOnClickListener(v -> finish());
    }
}