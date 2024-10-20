package com.example.mytodo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytodo.adapters.TaskAdapter;
import com.example.mytodo.database.DatabaseHelper;
import com.example.mytodo.models.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<Task> completedTaskList, uncompletedTaskList;
    TaskAdapter completedTaskAdapter, uncompletedTaskAdapter;
    TextView emptyMessageView1, emptyMessageView2;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emptyMessageView1 = findViewById(R.id.emptyMessage1);
        emptyMessageView2 = findViewById(R.id.emptyMessage2);
        databaseHelper = new DatabaseHelper(this);

        // For completed task list
        completedTaskList = new ArrayList<>();
        completedTaskAdapter = new TaskAdapter(this, completedTaskList);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewCompletedTasks);
        recyclerView.setAdapter(completedTaskAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // For uncompleted task list
        uncompletedTaskList = new ArrayList<>();
        uncompletedTaskAdapter = new TaskAdapter(this, uncompletedTaskList);
        RecyclerView recyclerView2 = findViewById(R.id.recyclerViewUncompletedTasks);
        recyclerView2.setAdapter(uncompletedTaskAdapter);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));

        // Get Add Button and attach onclick listener to it
        FloatingActionButton fabAddTask = findViewById(R.id.floatingActionButton);
        fabAddTask.setOnClickListener(view -> addTask());

        loadTasks();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadTasks() {
        Cursor cursor = databaseHelper.getAllTasks();
        completedTaskList.clear();
        uncompletedTaskList.clear();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                boolean isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("is_completed")) == 1;

                if (isCompleted) {
                    completedTaskList.add(new Task(id, title, description, true));
                } else {
                    uncompletedTaskList.add(new Task(id, title, description, false));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        completedTaskAdapter.notifyDataSetChanged();
        uncompletedTaskAdapter.notifyDataSetChanged();

        // Update the visibility of the empty message
        updateEmptyMessage();
    }

    private void updateEmptyMessage() {
        if (uncompletedTaskList.isEmpty()) {
            emptyMessageView1.setVisibility(View.VISIBLE);
        } else {
            emptyMessageView1.setVisibility(View.GONE);
        }

        if (completedTaskList.isEmpty()) {
            emptyMessageView2.setVisibility(View.VISIBLE);
        } else {
            emptyMessageView2.setVisibility(View.GONE);
        }
    }

    private void addTask() {
        // Open Task Detail Screen
        Intent intent = new Intent(MainActivity.this, TaskEditActivity.class);
        startActivityForResult(intent, 1);
    }
}