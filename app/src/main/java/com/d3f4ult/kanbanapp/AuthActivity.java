package com.d3f4ult.kanbanapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AuthActivity extends AppCompatActivity {
    
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonRegister;
    private SharedPreferences sharedPreferences;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        
        // Vérifier si l'utilisateur est déjà connecté
        sharedPreferences = getSharedPreferences("KanbanPrefs", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            navigateToProject();
            return;
        }
        
        initializeViews();
        setupListeners();
    }
    
    private void initializeViews() {
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);
    }
    
    private void setupListeners() {
        buttonLogin.setOnClickListener(v -> handleLogin());
        buttonRegister.setOnClickListener(v -> handleRegister());
    }
    
    private void handleLogin() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Vérifier les identifiants (simulation simple)
        String savedPassword = sharedPreferences.getString(username, null);
        
        if (savedPassword != null && savedPassword.equals(password)) {
            // Connexion réussie
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.putString("currentUser", username);
            editor.apply();
            
            Toast.makeText(this, "Connexion réussie!", Toast.LENGTH_SHORT).show();
            navigateToProject();
        } else {
            Toast.makeText(this, "Identifiants incorrects", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void handleRegister() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (password.length() < 4) {
            Toast.makeText(this, "Le mot de passe doit contenir au moins 4 caractères", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Vérifier si l'utilisateur existe déjà
        if (sharedPreferences.contains(username)) {
            Toast.makeText(this, "Cet utilisateur existe déjà", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Enregistrer le nouvel utilisateur
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(username, password);
        editor.apply();
        
        Toast.makeText(this, "Inscription réussie! Vous pouvez vous connecter", Toast.LENGTH_SHORT).show();
        editTextPassword.setText("");
    }
    
    private void navigateToProject() {
        Intent intent = new Intent(AuthActivity.this, ProjectActivity.class);
        startActivity(intent);
        finish();
    }
}