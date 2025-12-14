package com.d3f4ult.kanbanapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ProjectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        // TODO: here later you can read the extras:
        // String projectId = getIntent().getStringExtra("projectId");
        // String projectTitle = getIntent().getStringExtra("projectTitle");
    }
}
