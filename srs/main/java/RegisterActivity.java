package com.example.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private Button createAccountButton;
    private String userName;
    private EditText userEmail, userPassword, userPasswordCheck, userSecurityQuestion, userSecurityAnswer;
    private TextView alreadyHaveAccountLink;

    private FirebaseAuth mAuth;
    private DatabaseReference rootRef, newRef;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        InitializeFields();
        alreadyHaveAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });
    }

    private void CreateNewAccount()
    {
        final String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        if (TextUtils.isEmpty( email))
            Toast.makeText(this, "Please enter email...", Toast.LENGTH_SHORT).show();

        else if (TextUtils.isEmpty( password) || TextUtils.isEmpty(userPasswordCheck.getText().toString()) )
            Toast.makeText(this, "Please enter password...", Toast.LENGTH_SHORT).show();

        else if( ! userPasswordCheck.getText().toString().equals(password) )
            Toast.makeText(this, "Please check second password...", Toast.LENGTH_SHORT).show();

        else if (TextUtils.isEmpty( userSecurityQuestion.getText().toString()) || TextUtils.isEmpty( userSecurityAnswer.getText().toString()) )
            Toast.makeText(this, "Please enter a security question and answer...", Toast.LENGTH_SHORT).show();

        else
        {
            if ( validate(email, password) ) {
                loadingBar.setTitle("creating new account");
                loadingBar.setMessage("Please wait while we are creating new account for you");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            //e-mail verification
                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //task for sending e-mail
                                    if (task.isSuccessful() ) {
                                        Toast.makeText(RegisterActivity.this, "Please check your email for verification", Toast.LENGTH_SHORT).show();
                                        //verifying if the e-mail is verified
                                        if(mAuth.getCurrentUser().isEmailVerified() ) {
                                            //String currentUserID = mAuth.getCurrentUser().getUid();
                                            SendUserToLoginActivity();
                                        } else
                                            Toast.makeText(RegisterActivity.this, "Please verify your email address.", Toast.LENGTH_SHORT).show();
                                    } else
                                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(RegisterActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
                loadingBar.dismiss();
            }
        }
    }

    private void InitializeFields() {
        createAccountButton = (Button) findViewById(R.id.register_button);
        userEmail = (EditText) findViewById(R.id.register_email);
        userPassword = (EditText) findViewById(R.id.register_password);
        alreadyHaveAccountLink = (TextView) findViewById(R.id.already_have_an_account_link);
        userPasswordCheck = (EditText) findViewById(R.id.register_password2);
        userSecurityQuestion = (EditText) findViewById(R.id.sec_question);
        userSecurityAnswer = (EditText) findViewById(R.id.answer);

        loadingBar = new ProgressDialog(this);
    }

    private void SendUserToLoginActivity() {
        String[] info = new String[4];
        info[0] = userName;
        info[1] = userEmail.getText().toString();
        info[2] = userSecurityQuestion.getText().toString();
        info[3] = userSecurityAnswer.getText().toString();

        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        loginIntent.putExtra("newUser", true);
        loginIntent.putExtra("signUpInfo", info);
        startActivity(loginIntent);
        finish();
    }

    private boolean validate( String userMail, String userPassword ) {
        String[] mail = userMail.split("@");
        userName = mail[0];
        //userName = names[0] + " " + names[1];
        String bilkent = mail[1];

        if( bilkent.equals("ug.bilkent.edu.tr") ) {
            if(userPassword.length() < 8)
                Toast.makeText(RegisterActivity.this, "Password needs to have at least 8 characters...", Toast.LENGTH_SHORT).show();
            else
                return true;
        } else
            Toast.makeText(RegisterActivity.this, "Bilkent (student) mail must be used...", Toast.LENGTH_SHORT).show();
        return false;
    }


}
