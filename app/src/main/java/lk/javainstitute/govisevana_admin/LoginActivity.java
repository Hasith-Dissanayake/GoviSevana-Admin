package lk.javainstitute.govisevana_admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;
import lk.javainstitute.govisevana_admin.model.AdminModel;
import lk.javainstitute.govisevana_admin.model.SharedPreferenceHelper;

public class LoginActivity extends AppCompatActivity {
    private EditText phoneNumberInput;
    private Button loginButton;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private CountryCodePicker countryCodePicker;
    private SharedPreferenceHelper preferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = FirebaseFirestore.getInstance();
        preferenceHelper = new SharedPreferenceHelper(this);
        if (preferenceHelper.isLoggedIn()) {
            navigateToMainActivity();
            return;
        }

        countryCodePicker = findViewById(R.id.countryCodePicker);
        phoneNumberInput = findViewById(R.id.loginMobileNumberTextView);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.resetProgressBar1);
        countryCodePicker.registerCarrierNumberEditText(phoneNumberInput);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        String phoneNumber = getPhoneNumber();
        if (phoneNumber == null) {
            return;
        }
        checkAdminExists(phoneNumber);
    }

    private String getPhoneNumber() {
        if (phoneNumberInput.getText() == null || phoneNumberInput.getText().toString().trim().isEmpty()) {
            phoneNumberInput.setError("Enter your phone number");
            return null;
        }
        try {
            String fullPhoneNumber = countryCodePicker.getFullNumberWithPlus();
            if (!countryCodePicker.isValidFullNumber()) {
                phoneNumberInput.setError("Invalid phone number");
                return null;
            }
            return fullPhoneNumber;
        } catch (Exception e) {
            Toast.makeText(this, "Error parsing phone number", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void checkAdminExists(String phoneNumber) {
        setInProgress(true);
        db.collection("admins").document(phoneNumber).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        setInProgress(false);
                        if (documentSnapshot.exists()) {
                            AdminModel admin = documentSnapshot.toObject(AdminModel.class);
                            if (admin != null && "true".equalsIgnoreCase(admin.getIsActive())) {
                                preferenceHelper.saveAdminSession(admin.getPhone(), admin.getRole(), admin.getIsActive());
                                proceedToOtp(phoneNumber);
                            } else {
                                Toast.makeText(LoginActivity.this, "Admin account is inactive!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Admin not found!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        setInProgress(false);
                        Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void proceedToOtp(String phoneNumber) {
        Intent intent = new Intent(LoginActivity.this, LoginOtpActivity.class);
        intent.putExtra("phone", phoneNumber);
        startActivity(intent);
        finish();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void setInProgress(boolean inProgress) {
        if (inProgress) {
            loginButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            loginButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }
}
