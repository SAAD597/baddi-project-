package com.d3f4ult.kanbanapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * MainActivity - User Dashboard
 * Displays all Kanban boards (projects) for the authenticated user.
 * Users can create new boards, view existing ones, and logout.
 */
public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBoards;
    private FloatingActionButton fabCreateBoard;
    private ShimmerFrameLayout shimmerLayout;
    private TextView textViewGreeting;
    private TextView textViewEmptyState;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private BoardAdapter boardAdapter;
    private List<Board> boardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        // Check if user is logged in
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            // Redirect to AuthActivity if not logged in
            Intent intent = new Intent(MainActivity.this, AuthActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Initialize UI components
        initializeViews();

        // Display greeting message
        displayGreeting(currentUser);

        // Setup RecyclerView
        setupRecyclerView();

        // Setup FAB for creating new board
        setupFAB();

        // Load boards from Firestore
        loadBoards(currentUser.getUid());
    }

    /**
     * Initialize all UI components
     */
    private void initializeViews() {
        recyclerViewBoards = findViewById(R.id.recyclerViewBoards);
        fabCreateBoard = findViewById(R.id.fabCreateBoard);
        shimmerLayout = findViewById(R.id.shimmerLayout);
        textViewGreeting = findViewById(R.id.textViewGreeting);
        textViewEmptyState = findViewById(R.id.textViewEmptyState);
        progressBar = findViewById(R.id.progressBar);
    }

    /**
     * Display welcome greeting with user email
     */
    private void displayGreeting(FirebaseUser user) {
        String email = user.getEmail();
        String greetingText = "Welcome, " + (email != null ? email : "User") + "!";
        textViewGreeting.setText(greetingText);
    }

    /**
     * Setup RecyclerView with custom adapter
     */
    private void setupRecyclerView() {
        boardList = new ArrayList<>();
        boardAdapter = new BoardAdapter(boardList, new BoardAdapter.OnBoardClickListener() {
            @Override
            public void onBoardClick(Board board) {
                // Navigate to ProjectActivity with board details
                Intent intent = new Intent(MainActivity.this, ProjectActivity.class);
                intent.putExtra("projectId", board.getProjectId());
                intent.putExtra("projectTitle", board.getTitle());
                startActivity(intent);
            }
        });

        recyclerViewBoards.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBoards.setAdapter(boardAdapter);
    }

    /**
     * Setup FloatingActionButton for creating new board
     */
    private void setupFAB() {
        fabCreateBoard.setOnClickListener(v -> showCreateBoardDialog());
    }

    /**
     * Show dialog to create a new board
     */
    private void showCreateBoardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Board");
        builder.setMessage("Enter board name:");

        // Create EditText for board name
        final EditText input = new EditText(this);
        input.setHint("Board name");
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String boardName = input.getText().toString().trim();
            if (!boardName.isEmpty()) {
                createBoardInFirestore(boardName);
            } else {
                showToast("Please enter a board name");
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    /**
     * Create a new board and save to Firestore
     */
    private void createBoardInFirestore(String boardName) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();

        // Create new Board object
        Board newBoard = new Board();
        newBoard.setTitle(boardName);
        newBoard.setOwnerId(userId);
        newBoard.setCreatedAt(new Date());

        // Save to Firestore - auto-generate document ID
        firebaseFirestore.collection("projects")
                .add(newBoard)
                .addOnSuccessListener(documentReference -> {
                    String projectId = documentReference.getId();
                    newBoard.setProjectId(projectId);
                    showToast("Board created successfully!");
                })
                .addOnFailureListener(e -> {
                    showToast("Error creating board: " + e.getMessage());
                });
    }

    /**
     * Load all boards from Firestore with real-time updates
     */
    private void loadBoards(String userId) {
        showLoading(true);

        FirebaseFirestore db = firebaseFirestore;
        CollectionReference projectsRef = db.collection("projects");

        // Query: get all projects where ownerId == userId, sorted by createdAt (newest first)
        Query query = projectsRef
                .whereEqualTo("ownerId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING);

        // Real-time listener for automatic UI updates
        query.addSnapshotListener((querySnapshot, error) -> {
            showLoading(false);

            if (error != null) {
                showToast("Error loading boards: " + error.getMessage());
                return;
            }

            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                boardList.clear();

                // Convert Firestore documents to Board objects
                for (int i = 0; i < querySnapshot.getDocuments().size(); i++) {
                    Board board = querySnapshot.getDocuments().get(i).toObject(Board.class);
                    if (board != null) {
                        board.setProjectId(querySnapshot.getDocuments().get(i).getId());
                        boardList.add(board);
                    }
                }

                // Sort by creation date (newest first)
                Collections.sort(boardList, (b1, b2) -> {
                    if (b1.getCreatedAt() == null || b2.getCreatedAt() == null) {
                        return 0;
                    }
                    return b2.getCreatedAt().compareTo(b1.getCreatedAt());
                });

                boardAdapter.notifyDataSetChanged();
                updateEmptyState(false);
            } else {
                boardList.clear();
                boardAdapter.notifyDataSetChanged();
                updateEmptyState(true);
            }
        });
    }

    /**
     * Update empty state visibility
     */
    private void updateEmptyState(boolean isEmpty) {
        if (isEmpty) {
            textViewEmptyState.setVisibility(android.view.View.VISIBLE);
            recyclerViewBoards.setVisibility(android.view.View.GONE);
        } else {
            textViewEmptyState.setVisibility(android.view.View.GONE);
            recyclerViewBoards.setVisibility(android.view.View.VISIBLE);
        }
    }

    /**
     * Show or hide loading indicator
     */
    private void showLoading(boolean isLoading) {
        if (isLoading) {
            shimmerLayout.startShimmer();
            shimmerLayout.setVisibility(android.view.View.VISIBLE);
        } else {
            shimmerLayout.stopShimmer();
            shimmerLayout.setVisibility(android.view.View.GONE);
        }
    }

    /**
     * Show simple toast message
     */
    private void showToast(String message) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show();
    }

    /**
     * Setup menu for logout option
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Handle menu item selections
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Logout the current user and redirect to AuthActivity
     */
    private void logout() {
        firebaseAuth.signOut();
        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
        startActivity(intent);
        finish();
    }
}
