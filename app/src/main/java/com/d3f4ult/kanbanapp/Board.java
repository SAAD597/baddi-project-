package com.d3f4ult.kanbanapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProjectActivity extends AppCompatActivity {

    private TextView textProjectTitle;
    private TextView textProjectId;
    private TextView textProjectStatus;
    private TextView textProjectDate;

    private String projectId;
    private String projectTitle;
    private String projectStatus;
    private long projectDateMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        // Initialisation des vues
        initViews();

        // Récupération des données de l'intent
        getIntentData();

        // Affichage des données
        displayProjectData();

        // Configuration de la toolbar avec bouton retour
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(projectTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViews() {
        textProjectTitle = findViewById(R.id.textProjectTitle);
        textProjectId = findViewById(R.id.textProjectId);
        textProjectStatus = findViewById(R.id.textProjectStatus);
        textProjectDate = findViewById(R.id.textProjectDate);
    }

    /**
     * Récupère les données passées par l'intent
     */
    private void getIntentData() {
        Intent intent = getIntent();

        if (intent != null) {
            projectId = intent.getStringExtra("projectId");
            projectTitle = intent.getStringExtra("projectTitle");
            projectStatus = intent.getStringExtra("projectStatus");
            projectDateMillis = intent.getLongExtra("projectDate", System.currentTimeMillis());
        } else {
            // Valeurs par défaut si pas de données
            projectId = "N/A";
            projectTitle = "Projet sans titre";
            projectStatus = "Inconnu";
            projectDateMillis = System.currentTimeMillis();
        }
    }

    /**
     * Affiche les données du projet à l'écran
     */
    private void displayProjectData() {
        textProjectTitle.setText(projectTitle);
        textProjectId.setText("ID: " + projectId);
        textProjectStatus.setText("Statut: " + projectStatus);

        // Formatage de la date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy à HH:mm", Locale.FRENCH);
        String formattedDate = dateFormat.format(new Date(projectDateMillis));
        textProjectDate.setText("Créé le: " + formattedDate);

        // Couleur du statut
        int statusColor = getStatusColor(projectStatus);
        textProjectStatus.setTextColor(statusColor);
    }

    /**
     * Retourne la couleur selon le statut
     */
    private int getStatusColor(String status) {
        switch (status) {
            case "En cours":
                return 0xFF4CAF50; // Vert
            case "Terminé":
                return 0xFF2196F3; // Bleu
            case "En attente":
                return 0xFFFF9800; // Orange
            default:
                return 0xFF9E9E9E; // Gris
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Bouton retour
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Gère la déconnexion
     */
    private void logout() {
        Toast.makeText(this, "Déconnexion réussie", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(ProjectActivity.this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
