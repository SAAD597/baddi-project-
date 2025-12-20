package com.d3f4ult.kanbanapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {
    
    private RecyclerView recyclerTodo;
    private RecyclerView recyclerInProgress;
    private RecyclerView recyclerDone;
    
    private TaskAdapter adapterTodo;
    private TaskAdapter adapterInProgress;
    private TaskAdapter adapterDone;
    
    private List<Task> allTasks;
    private FloatingActionButton fabAddTask;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        
        // Configuration de la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        gson = new Gson();
        sharedPreferences = getSharedPreferences("KanbanPrefs", MODE_PRIVATE);
        
        initializeViews();
        setupRecyclerViews();
        loadTasks();
        updateKanbanBoard();
    }
    
    private void initializeViews() {
        recyclerTodo = findViewById(R.id.recyclerViewTodo);
        recyclerInProgress = findViewById(R.id.recyclerViewInProgress);
        recyclerDone = findViewById(R.id.recyclerViewDone);
        fabAddTask = findViewById(R.id.fabAddTask);
        
        fabAddTask.setOnClickListener(v -> showCreateTaskDialog());
    }
    
    private void setupRecyclerViews() {
        // Adaptateur pour "À faire"
        adapterTodo = new TaskAdapter(this);
        recyclerTodo.setLayoutManager(new LinearLayoutManager(this));
        recyclerTodo.setAdapter(adapterTodo);
        
        // Adaptateur pour "En cours"
        adapterInProgress = new TaskAdapter(this);
        recyclerInProgress.setLayoutManager(new LinearLayoutManager(this));
        recyclerInProgress.setAdapter(adapterInProgress);
        
        // Adaptateur pour "Terminé"
        adapterDone = new TaskAdapter(this);
        recyclerDone.setLayoutManager(new LinearLayoutManager(this));
        recyclerDone.setAdapter(adapterDone);
    }
    
    private void loadTasks() {
        String tasksJson = sharedPreferences.getString("tasks", null);
        
        if (tasksJson != null) {
            Type type = new TypeToken<List<Task>>(){}.getType();
            allTasks = gson.fromJson(tasksJson, type);
        } else {
            allTasks = new ArrayList<>();
            // Ajouter quelques tâches d'exemple
            addSampleTasks();
        }
    }
    
    private void addSampleTasks() {
        Task task1 = new Task("Créer l'interface", "Concevoir l'UI du tableau Kanban");
        task1.setPriority(3);
        task1.setStatus(Task.TaskStatus.DONE);
        
        Task task2 = new Task("Implémenter la logique", "Coder les fonctionnalités du Kanban");
        task2.setPriority(3);
        task2.setStatus(Task.TaskStatus.IN_PROGRESS);
        
        Task task3 = new Task("Tests unitaires", "Écrire les tests pour l'application");
        task3.setPriority(2);
        task3.setStatus(Task.TaskStatus.TODO);
        
        allTasks.add(task1);
        allTasks.add(task2);
        allTasks.add(task3);
        
        saveTasks();
    }
    
    private void saveTasks() {
        String tasksJson = gson.toJson(allTasks);
        sharedPreferences.edit().putString("tasks", tasksJson).apply();
    }
    
    private void updateKanbanBoard() {
        // Filtrer les tâches par statut
        List<Task> todoTasks = allTasks.stream()
                .filter(t -> t.getStatus() == Task.TaskStatus.TODO)
                .collect(Collectors.toList());
        
        List<Task> inProgressTasks = allTasks.stream()
                .filter(t -> t.getStatus() == Task.TaskStatus.IN_PROGRESS)
                .collect(Collectors.toList());
        
        List<Task> doneTasks = allTasks.stream()
                .filter(t -> t.getStatus() == Task.TaskStatus.DONE)
                .collect(Collectors.toList());
        
        // Mettre à jour les adaptateurs
        adapterTodo.setTasks(todoTasks);
        adapterInProgress.setTasks(inProgressTasks);
        adapterDone.setTasks(doneTasks);
    }
    
    private void showCreateTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_task, null);
        
        EditText editTitle = dialogView.findViewById(R.id.editTaskTitle);
        EditText editDescription = dialogView.findViewById(R.id.editTaskDescription);
        EditText editAssignedTo = dialogView.findViewById(R.id.editTaskAssignedTo);
        RadioGroup radioPriority = dialogView.findViewById(R.id.radioPriority);
        
        builder.setView(dialogView)
                .setTitle("Créer une nouvelle tâche")
                .setPositiveButton("Créer", (dialog, which) -> {
                    String title = editTitle.getText().toString().trim();
                    String description = editDescription.getText().toString().trim();
                    String assignedTo = editAssignedTo.getText().toString().trim();
                    
                    if (title.isEmpty()) {
                        Toast.makeText(this, "Le titre est obligatoire", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    // Récupérer la priorité
                    int priority = 2; // Moyenne par défaut
                    int selectedId = radioPriority.getCheckedRadioButtonId();
                    if (selectedId == R.id.radioPriorityLow) {
                        priority = 1;
                    } else if (selectedId == R.id.radioPriorityHigh) {
                        priority = 3;
                    }
                    
                    Task newTask = new Task(title, description);
                    newTask.setPriority(priority);
                    if (!assignedTo.isEmpty()) {
                        newTask.setAssignedTo(assignedTo);
                    }
                    
                    allTasks.add(newTask);
                    saveTasks();
                    updateKanbanBoard();
                    
                    Toast.makeText(this, "Tâche créée avec succès", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Annuler", null);
        
        builder.create().show();
    }
    
    private void showEditTaskDialog(Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_task, null);
        
        EditText editTitle = dialogView.findViewById(R.id.editTaskTitle);
        EditText editDescription = dialogView.findViewById(R.id.editTaskDescription);
        EditText editAssignedTo = dialogView.findViewById(R.id.editTaskAssignedTo);
        RadioGroup radioPriority = dialogView.findViewById(R.id.radioPriority);
        
        // Pré-remplir avec les données actuelles
        editTitle.setText(task.getTitle());
        editDescription.setText(task.getDescription());
        editAssignedTo.setText(task.getAssignedTo());
        
        switch (task.getPriority()) {
            case 1:
                radioPriority.check(R.id.radioPriorityLow);
                break;
            case 3:
                radioPriority.check(R.id.radioPriorityHigh);
                break;
            default:
                radioPriority.check(R.id.radioPriorityMedium);
        }
        
        builder.setView(dialogView)
                .setTitle("Modifier la tâche")
                .setPositiveButton("Enregistrer", (dialog, which) -> {
                    task.setTitle(editTitle.getText().toString().trim());
                    task.setDescription(editDescription.getText().toString().trim());
                    task.setAssignedTo(editAssignedTo.getText().toString().trim());
                    
                    int priority = 2;
                    int selectedId = radioPriority.getCheckedRadioButtonId();
                    if (selectedId == R.id.radioPriorityLow) {
                        priority = 1;
                    } else if (selectedId == R.id.radioPriorityHigh) {
                        priority = 3;
                    }
                    task.setPriority(priority);
                    
                    saveTasks();
                    updateKanbanBoard();
                    
                    Toast.makeText(this, "Tâche mise à jour", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Annuler", null);
        
        builder.create().show();
    }
    
    @Override
    public void onTaskClick(Task task) {
        // Afficher les détails de la tâche
        showTaskDetails(task);
    }
    
    @Override
    public void onTaskLongClick(Task task) {
        // Action sur appui long (optionnel)
    }
    
    @Override
    public void onDeleteClick(Task task) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer la tâche")
                .setMessage("Êtes-vous sûr de vouloir supprimer cette tâche ?")
                .setPositiveButton("Supprimer", (dialog, which) -> {
                    allTasks.remove(task);
                    saveTasks();
                    updateKanbanBoard();
                    Toast.makeText(this, "Tâche supprimée", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Annuler", null)
                .show();
    }
    
    @Override
    public void onEditClick(Task task) {
        showEditTaskDialog(task);
    }
    
    @Override
    public void onStatusChangeClick(Task task) {
        task.moveToNextStatus();
        saveTasks();
        updateKanbanBoard();
        Toast.makeText(this, "Statut changé: " + task.getStatus().getDisplayName(), Toast.LENGTH_SHORT).show();
    }
    
    private void showTaskDetails(Task task) {
        String details = "Titre: " + task.getTitle() + "\n\n" +
                        "Description: " + task.getDescription() + "\n\n" +
                        "Statut: " + task.getStatus().getDisplayName() + "\n" +
                        "Priorité: " + task.getPriorityText();
        
        if (task.getAssignedTo() != null && !task.getAssignedTo().isEmpty()) {
            details += "\nAssigné à: " + task.getAssignedTo();
        }
        
        new AlertDialog.Builder(this)
                .setTitle("Détails de la tâche")
                .setMessage(details)
                .setPositiveButton("OK", null)
                .show();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_project, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_logout) {
            logout();
            return true;
        } else if (id == R.id.action_clear_all) {
            clearAllTasks();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Déconnexion")
                .setMessage("Voulez-vous vraiment vous déconnecter ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    sharedPreferences.edit()
                            .putBoolean("isLoggedIn", false)
                            .remove("currentUser")
                            .apply();
                    
                    Intent intent = new Intent(ProjectActivity.this, AuthActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Non", null)
                .show();
    }
    
    private void clearAllTasks() {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer toutes les tâches")
                .setMessage("Êtes-vous sûr de vouloir supprimer TOUTES les tâches ?")
                .setPositiveButton("Supprimer", (dialog, which) -> {
                    allTasks.clear();
                    saveTasks();
                    updateKanbanBoard();
                    Toast.makeText(this, "Toutes les tâches ont été supprimées", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Annuler", null)
                .show();
    }
}