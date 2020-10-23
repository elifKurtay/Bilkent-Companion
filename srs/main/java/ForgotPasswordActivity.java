package com.example.chat;

//OLD
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordActivity extends AppCompatActivity {

    private String userAnswer, currentUserMail;
    private TextView questionView, attempts;
    private EditText answer;
    private Button enter;
    private int count = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        currentUserMail = getIntent().getStringExtra("mail");

        enter = (Button) findViewById(R.id.bEnter);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate( answer.getText().toString() );
            }
        });

        answer = (EditText) findViewById(R.id.etSQanswer);
        questionView = (TextView) findViewById(R.id.tvSQ);
        attempts = (TextView) findViewById(R.id.tvAttemps);

        getUserAnswer();
    }

    private void validate( String answer ) {

        if( answer.equals(userAnswer) ) {
            FirebaseAuth.getInstance().sendPasswordResetEmail( currentUserMail ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if( task.isSuccessful() ) {
                        Toast.makeText(ForgotPasswordActivity.this, "Please check your email account to reset your password", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(ForgotPasswordActivity.this, "Error occurred: " + error, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else {
            count--;
            if( count > 0 )
                attempts.setText("You have " + count + " more remaining attempts left.");
            else {
                enter.setEnabled(false);
                attempts.setText("You do not have remaining attempts. \nPlease create new account.");

                ProgressDialog loadingBar = new ProgressDialog(this);
                loadingBar.setTitle("Saving Account");
                loadingBar.setMessage("Saving account has been failed. Please create new account.");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

                Intent intent = new Intent( ForgotPasswordActivity.this, LoginActivity.class);
                intent.putExtra("SAVE_ACCOUNT", false);

                loadingBar.dismiss();

                startActivity(intent);
            }

        }
    }

    private void getUserAnswer() {

        FirebaseDatabase.getInstance().getReference().child("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if(snapshot.hasChild("email")) {
                                String mails = snapshot.child("email").getValue().toString();
                                if (mails.equals(currentUserMail)) {
                                    if ((snapshot.hasChild("answer")) && snapshot.hasChild("question")) {
                                        userAnswer = snapshot.child("answer").getValue().toString();
                                        String question = snapshot.child("question").getValue().toString();
                                        questionView.setText(question);
                                    } else {
                                        Toast.makeText(ForgotPasswordActivity.this, "You have not set up a security question. Please create a new account.", Toast.LENGTH_LONG).show();
                                    }

                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }
}
