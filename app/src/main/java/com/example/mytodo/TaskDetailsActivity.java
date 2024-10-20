package com.example.mytodo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mytodo.database.DatabaseHelper;

public class TaskDetailsActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private TextView textViewTitle;
    private TextView textViewDescription;
    private TextView textViewStatus;
    private Button buttonCompleteTask;

    private int taskId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        // Initialize views
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewStatus = findViewById(R.id.textViewStatus);
        buttonCompleteTask = findViewById(R.id.buttonCompleteTask);

        // Check if this is an edit operation
        Intent intent = getIntent();
        if (intent.hasExtra("taskId")) {
            taskId = intent.getIntExtra("taskId", -1);
            textViewTitle.setText(intent.getStringExtra("taskTitle"));
            textViewDescription.setText(intent.getStringExtra("taskDescription"));
            boolean taskCompleted = intent.getBooleanExtra("taskCompleted", false);
            if (taskCompleted){
                textViewStatus.setText("Completed");
                buttonCompleteTask.setText("Mark As Uncomplete");
            }else{
                textViewStatus.setText("Not Completed");
                buttonCompleteTask.setText("Mark As Complete");
            }

            // Listen to save button on click
            buttonCompleteTask.setOnClickListener(view -> completeTask(taskCompleted));
        }

        databaseHelper = new DatabaseHelper(this);

        // Enable toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void completeTask(boolean taskCompleted) {
        int result = databaseHelper.updateTaskComplete(taskId, !taskCompleted);
        if(result != -1){
            Toast.makeText(this, "Task Completion Updated", Toast.LENGTH_SHORT).show();
        }

        setResult(RESULT_OK, getIntent());

        // Go back to previous screen
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_edit){
            // Goto TaskEditActivity Screen
            Intent intent = new Intent(this, TaskEditActivity.class);
            intent.putExtras(getIntent().getExtras());
            startActivityForResult(intent, 1);
        }else if(id==R.id.action_delete){
            databaseHelper.deleteTask(taskId);
            Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK, getIntent());

            // Go back to previous screen
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null){
            textViewTitle.setText(data.getStringExtra("taskTitle"));
            textViewDescription.setText(data.getStringExtra("taskDescription"));
        }
    }
}