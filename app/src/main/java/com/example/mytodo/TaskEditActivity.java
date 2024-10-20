package com.example.mytodo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mytodo.database.DatabaseHelper;

public class TaskEditActivity extends AppCompatActivity {
    private EditText editTextTitle;
    private EditText editTextDescription;
    private Button buttonSaveTask;
    private DatabaseHelper databaseHelper;

    private int taskId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);

        // Initialize views
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonSaveTask = findViewById(R.id.buttonSaveTask);

        // Check if this is an edit operation
        Intent intent = getIntent();
        if (intent.hasExtra("taskId")) {
            taskId = intent.getIntExtra("taskId", -1);
            editTextTitle.setText(intent.getStringExtra("taskTitle"));
            editTextDescription.setText(intent.getStringExtra("taskDescription"));
        }

        databaseHelper = new DatabaseHelper(this);

        // Listen to save button on click
        buttonSaveTask.setOnClickListener(view -> saveTask());
    }

    private void saveTask() {
        // Get input values
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        // Validation
        if (title.isEmpty()) {
            editTextTitle.setError("Title is required");
            editTextTitle.requestFocus();
            return;
        }

        if (taskId == -1) {
            // Save a new task
            long result = databaseHelper.addTask(title, description);
            if (result != -1) {
                Toast.makeText(this, "Task saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error saving task", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Update an existing task
            int result = databaseHelper.updateTask(taskId, title, description, 0);
            if (result > 0) {
                Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error updating task", Toast.LENGTH_SHORT).show();
            }
        }

        Intent result = new Intent();
        result.putExtra("taskTitle", title);
        result.putExtra("taskDescription", description);

        setResult(RESULT_OK, result);

        // Finish the activity and go back to the previous screen
        finish();
    }
}