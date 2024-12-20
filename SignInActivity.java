package com.example.finalterm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {

    private Button login;
    private Button SigninButton;
    private ImageButton withGoogleButton;

    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private EditText etPhone;
    private EditText firstName;
    private EditText lastName;
    private EditText userName;

    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        login = findViewById(R.id.login);
        SigninButton = findViewById(R.id.SigninButton);
        withGoogleButton = findViewById(R.id.with_google);

        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        userName = findViewById(R.id.userName);

        withGoogleButton.setOnClickListener(v -> openUrl("https://www.google.com/"));

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

        SigninButton.setOnClickListener(view -> validateInputs());
    }

    // Open a URL in the default browser
    private void openUrl(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    private void validateInputs() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String firstNameText = firstName.getText().toString().trim();
        String lastNameText = lastName.getText().toString().trim();
        String username = userName.getText().toString().trim();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Provide a valid email");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty() || !password.equals(confirmPassword)) {
            etPassword.setError("Passwords must match");
            etPassword.requestFocus();
            return;
        }

        if (phone.isEmpty() || firstNameText.isEmpty() || lastNameText.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        SigninUser(email, password, phone, firstNameText, lastNameText, username);
    }

    private void SigninUser(String email, String password, String phone, String firstName, String lastName, String username) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            String userId = user.getUid();
                            User newUser = new User(firstName, lastName, username, email, phone);
                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(userId)
                                    .setValue(newUser)
                                    .addOnCompleteListener(databaseTask -> {
                                        if (databaseTask.isSuccessful()) {
                                            Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SignInActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(this, "Error saving user data", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Registration failed.";
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
