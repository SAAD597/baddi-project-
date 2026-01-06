package com.d3f4ult.kanbanapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBoards;
    private BoardAdapter boardAdapter;
    private List<Board> boardsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configuration de la toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Mes Projets");
        }

        // Initialisation du RecyclerView
        recyclerViewBoards = findViewById(R.id.recyclerViewBoards);
        recyclerViewBoards.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBoards.setHasFixedSize(true);

        // Chargement des données
        loadBoards();

        // Configuration de l'adapter avec le listener de click
        boardAdapter = new BoardAdapter(boardsList, new BoardAdapter.OnBoardClickListener() {
            @Override
            public void onBoardClick(Board board) {
                openProjectActivity(board);
            }
        });

        recyclerViewBoards.setAdapter(boardAdapter);
    }

    /**
     * Charge les boards (données dummy pour démo)
     * TODO: Remplacer par Firestore quand disponible
     */
    private void loadBoards() {
        boardsList = new ArrayList<>();

        // Création de données dummy pour la démo
        boardsList.add(new Board("1", "Projet Mobile App", new Date(), "En cours"));
        boardsList.add(new Board("2", "Site Web E-commerce", new Date(System.currentTimeMillis() - 86400000), "Terminé"));
        boardsList.add(new Board("3", "Application Desktop", new Date(System.currentTimeMillis() - 172800000), "En cours"));
        boardsList.add(new Board("4", "API Backend", new Date(System.currentTimeMillis() - 259200000), "En attente"));
        boardsList.add(new Board("5", "Dashboard Analytics", new Date(), "En cours"));
    }

    /**
     * Ouvre ProjectActivity avec les données du board sélectionné
     */
    private void openProjectActivity(Board board) {
        Intent intent = new Intent(MainActivity.this, ProjectActivity.class);
        intent.putExtra("projectId", board.getId());
        intent.putExtra("projectTitle", board.getTitle());
        intent.putExtra("projectStatus", board.getStatus());
        intent.putExtra("projectDate", board.getCreatedAt().getTime());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Gère la déconnexion de l'utilisateur
     */
    private void logout() {
        // TODO: Ajouter la déconnexion Firebase Auth quand disponible
        // FirebaseAuth.getInstance().signOut();

        Toast.makeText(this, "Déconnexion réussie", Toast.LENGTH_SHORT).show();

        // Redirection vers AuthActivity
        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}