// ============================================
// MainActivity.java - Required Updates
// ============================================

package com.d3f4ult.kanbanapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;  // CHANGED: Import GridLayoutManager
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

        // Configuration de la toolbar (optional - no ActionBar in new design)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();  // Hide action bar, we have custom header
        }

        // Initialisation du RecyclerView
        recyclerViewBoards = findViewById(R.id.recyclerViewBoards);

        // CHANGED: Use GridLayoutManager with 2 columns instead of LinearLayoutManager
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerViewBoards.setLayoutManager(layoutManager);
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

    private void loadBoards() {
        boardsList = new ArrayList<>();
        // Sample data
        boardsList.add(new Board("1", "Projet Mobile App", new Date(), "En cours"));
        boardsList.add(new Board("2", "Site Web E-commerce", new Date(System.currentTimeMillis() - 86400000), "Terminé"));
        boardsList.add(new Board("3", "Application Desktop", new Date(System.currentTimeMillis() - 172800000), "En cours"));
        boardsList.add(new Board("4", "API Backend", new Date(System.currentTimeMillis() - 259200000), "En attente"));
        boardsList.add(new Board("5", "Dashboard Analytics", new Date(), "En cours"));
    }

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

    private void logout() {
        Toast.makeText(this, "Déconnexion réussie", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

// ============================================
// BoardAdapter.java - Required Updates
// ============================================

package com.d3f4ult.kanbanapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.BoardViewHolder> {

    private List<Board> boardsList;
    private OnBoardClickListener listener;
    private SimpleDateFormat dateFormat;

    public interface OnBoardClickListener {
        void onBoardClick(Board board);
    }

    public BoardAdapter(List<Board> boardsList, OnBoardClickListener listener) {
        this.boardsList = boardsList;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.FRENCH);
    }

    @NonNull
    @Override
    public BoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_board, parent, false);
        return new BoardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BoardViewHolder holder, int position) {
        Board board = boardsList.get(position);

        // CHANGED: Use new view IDs from updated layout
        holder.textTitle.setText(board.getTitle());
        holder.textDate.setText(dateFormat.format(board.getCreatedAt()));
        holder.textStatus.setText(board.getStatus());

        // Couleur du statut
        int statusColor = getStatusColor(board.getStatus());
        holder.textStatus.setTextColor(statusColor);

        // NEW: Handle more button click
        holder.buttonMore.setOnClickListener(v -> {
            // Add menu or options functionality here
        });

        // Gestion du clic sur la carte
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBoardClick(board);
            }
        });
    }

    @Override
    public int getItemCount() {
        return boardsList != null ? boardsList.size() : 0;
    }

    private int getStatusColor(String status) {
        switch (status) {
            case "En cours":
                return 0xFF27AE60; // Green
            case "Terminé":
                return 0xFF5B9FED; // Blue
            case "En attente":
                return 0xFFF39C12; // Orange
            default:
                return 0xFF95A5A6; // Gray
        }
    }

    public void updateBoards(List<Board> newBoards) {
        this.boardsList = newBoards;
        notifyDataSetChanged();
    }

    static class BoardViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textTitle;
        TextView textDate;
        TextView textStatus;
        ImageButton buttonMore;  // NEW

        public BoardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewBoard);
            textTitle = itemView.findViewById(R.id.textBoardTitle);
            textDate = itemView.findViewById(R.id.textBoardDate);
            textStatus = itemView.findViewById(R.id.textBoardStatus);
            buttonMore = itemView.findViewById(R.id.buttonMore);  // NEW
        }
    }
}

// ============================================
// ProjectActivity.java - Required Updates
// ============================================

package com.d3f4ult.kanbanapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProjectActivity extends AppCompatActivity {

    private TextView textProjectTitle;
    private ImageButton buttonBack;
    private ImageButton buttonMenu;
    private RecyclerView recyclerViewTodo;
    private RecyclerView recyclerViewInProgress;
    private RecyclerView recyclerViewDone;
    private FloatingActionButton fabAddTask;

    private TextView textCountTodo;
    private TextView textCountInProgress;
    private TextView textCountDone;

    private String projectId;
    private String projectTitle;
    private String projectStatus;
    private long projectDateMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        // Hide action bar, we have custom toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize views
        initViews();

        // Get data from intent
        getIntentData();

        // Display project data
        displayProjectData();

        // Setup RecyclerViews
        setupRecyclerViews();

        // Setup click listeners
        setupListeners();
    }

    private void initViews() {
        textProjectTitle = findViewById(R.id.textProjectTitle);
        buttonBack = findViewById(R.id.buttonBack);
        buttonMenu = findViewById(R.id.buttonMenu);

        recyclerViewTodo = findViewById(R.id.recyclerViewTodo);
        recyclerViewInProgress = findViewById(R.id.recyclerViewInProgress);
        recyclerViewDone = findViewById(R.id.recyclerViewDone);

        textCountTodo = findViewById(R.id.textCountTodo);
        textCountInProgress = findViewById(R.id.textCountInProgress);
        textCountDone = findViewById(R.id.textCountDone);

        fabAddTask = findViewById(R.id.fabAddTask);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            projectId = intent.getStringExtra("projectId");
            projectTitle = intent.getStringExtra("projectTitle");
            projectStatus = intent.getStringExtra("projectStatus");
            projectDateMillis = intent.getLongExtra("projectDate", System.currentTimeMillis());
        } else {
            projectId = "N/A";
            projectTitle = "Projet sans titre";
            projectStatus = "Inconnu";
            projectDateMillis = System.currentTimeMillis();
        }
    }

    private void displayProjectData() {
        textProjectTitle.setText(projectTitle);
        // Update counts (example values)
        textCountTodo.setText("3");
        textCountInProgress.setText("2");
        textCountDone.setText("4");
    }

    private void setupRecyclerViews() {
        // Setup each RecyclerView with LinearLayoutManager
        recyclerViewTodo.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewInProgress.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDone.setLayoutManager(new LinearLayoutManager(this));

        // TODO: Set adapters with task data
    }

    private void setupListeners() {
        buttonBack.setOnClickListener(v -> onBackPressed());

        buttonMenu.setOnClickListener(v -> {
            // Show menu options
        });

        fabAddTask.setOnClickListener(v -> {
            // Show add task dialog
            Toast.makeText(this, "Add new task", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

// ============================================
// SUMMARY OF CHANGES
// ============================================

/*
MAIN CHANGES NEEDED:

1. MainActivity.java:
   - Import GridLayoutManager
   - Change from LinearLayoutManager to GridLayoutManager(this, 2)
   - Hide ActionBar (optional)

2. BoardAdapter.java:
   - Update view IDs: textBoardTitle, textBoardDate, textBoardStatus
   - Add buttonMore ImageButton handling
   - Update colors to match new design

3. ProjectActivity.java:
   - Hide ActionBar
   - Add buttonBack and buttonMenu handling
   - Initialize all column RecyclerViews
   - Update count TextViews
   - Setup click listeners for back button and menu

4. TaskAdapter.java:
   - Update view IDs to match new item_task.xml
   - Handle priority badges display
   - Handle assignee avatars
   - Update action button listeners

TESTING:
- Run app and verify grid layout on MainActivity
- Check board cards display correctly
- Verify navigation to ProjectActivity works
- Test horizontal scrolling in Kanban view
- Verify all buttons respond to clicks
*/