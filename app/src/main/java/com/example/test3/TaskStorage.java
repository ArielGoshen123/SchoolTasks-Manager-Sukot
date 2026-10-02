package com.example.test3;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class TaskStorage {

    private static final String PREFS_NAME = "TaskPrefs";
    private final SharedPreferences sharedPreferences;
    private final Gson gson;
    private final Context context;

    public TaskStorage(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    private String getUserTaskKey(String username) {
        return "tasks_" + username.toLowerCase().trim();
    }

    private String getCurrentUserTaskKey() {
        SharedPreferences userPrefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String currentUsername = userPrefs.getString("user_name", "default_user");
        return getUserTaskKey(currentUsername);
    }

    public ArrayList<Task> loadAll() {
        String json = sharedPreferences.getString(getCurrentUserTaskKey(), null);

        if (json == null) {
            return new ArrayList<>();
        }

        Type type = new TypeToken<ArrayList<Task>>() {}.getType();
        ArrayList<Task> tasks = gson.fromJson(json, type);

        if (tasks == null) {
            return new ArrayList<>();
        }

        return tasks;
    }

    public void saveAll(ArrayList<Task> tasks) {
        String json = gson.toJson(tasks);
        sharedPreferences.edit().putString(getCurrentUserTaskKey(), json).apply();
    }

    public void addTask(Task task) {
        ArrayList<Task> tasks = loadAll();
        tasks.add(task);
        saveAll(tasks);
    }

    public Task findById(int id) {
        for (Task t : loadAll()) {
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    public void updateTask(Task updatedTask) {
        ArrayList<Task> tasks = loadAll();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == updatedTask.getId()) {
                tasks.set(i, updatedTask);
                break;
            }
        }
        saveAll(tasks);
    }

    public void deleteById(int id) {
        ArrayList<Task> tasks = loadAll();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == id) {
                tasks.remove(i);
                break;
            }
        }
        saveAll(tasks);
    }
    //Autogenerate available id
    public int nextId() {
        ArrayList<Task> tasks = loadAll();
        int maxId = 0;

        for (Task t : tasks) {
            if (t.getId() > maxId) {
                maxId = t.getId();
            }
        }
        return maxId + 1;
    }

    //Clears all tasks for a specific username
    public void clearUserData(String username) {
        if (username != null && !username.trim().isEmpty()) {
            sharedPreferences.edit().remove(getUserTaskKey(username)).apply();
        }
    }
}