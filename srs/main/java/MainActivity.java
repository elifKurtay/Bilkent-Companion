package com.example.chat;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
{
    private NavigationView nav_view;

    private Button buySellButton, lostFoundButton, settingsButton, chatsButton;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();

        buySellButton = (Button) findViewById(R.id.buy_sell);
        lostFoundButton = (Button) findViewById(R.id.lost_found);
        settingsButton = (Button) findViewById(R.id.settings);
        chatsButton = (Button) findViewById(R.id.chats);

        buySellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BuyFilter.class));
            }
        });

        lostFoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LostFilter.class));
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

        chatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ChatRoomActivity.class));
            }
        });

        addNavigationDrawer();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if ( currentUser == null)
        {
            SendUserToLoginActivity();
        }
        else
        {
            if( getIntent().hasExtra("newUser") ) {
                if (getIntent().getBooleanExtra("newUser", true))
                    updateDatabase();
            }
            else
                VerifyUserExistence();
        }
    }

    private void VerifyUserExistence()
    {
        String currentUserID = mAuth.getCurrentUser().getUid();

        rootRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if ( (dataSnapshot.child("name").exists()) || (dataSnapshot.child("status").exists()))
                {
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //SendUserToLoginActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);

        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);

    }


    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent( MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity( loginIntent);
        finish();
    }

    private void SendUserToSettingsActivity()
    {
        Intent settingIntent = new Intent( MainActivity.this, SettingsActivity.class);
        settingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity( settingIntent);
        finish();
    }


    private void updateDatabase() {

        String currentUserID = mAuth.getCurrentUser().getUid();
        String[] info = getIntent().getStringArrayExtra("databaseInfo");

        HashMap<String,String> profileMap = new HashMap<>();
        profileMap.put("uid", currentUserID);
        profileMap.put("name", info[0]);
        profileMap.put("email", info[1]);
        profileMap.put("question", info[2]);
        profileMap.put("answer", info[3]);

        rootRef.child("Users").child(currentUserID).setValue(profileMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            String message = task.getException().toString();
                            Toast.makeText(MainActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addNavigationDrawer() {
        dl = (DrawerLayout)findViewById(R.id.activity_main);
        t = new ActionBarDrawerToggle(MainActivity.this, dl,R.string.Open, R.string.Close);

        dl.addDrawerListener(t);
        t.syncState();

        nav_view = (NavigationView)findViewById(R.id.nv);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.logout:
                        Toast.makeText(MainActivity.this, "Logout",Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        SendUserToLoginActivity();
                        break;
                    case R.id.buy_sell:
                        Toast.makeText(MainActivity.this, "Buy / Sell",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, BuyFilter.class));
                        break;
                    case R.id.lost_found:
                        Toast.makeText(MainActivity.this, "Lost & Found",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, LostFilter.class));
                        break;
                    case R.id.chats:
                        Toast.makeText(MainActivity.this, "Chats",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, ChatRoomActivity.class));
                        break;
                    case R.id.settings:
                        Toast.makeText(MainActivity.this, "Settings",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        break;
                    default:
                        dl.closeDrawers();
                        return true;
                }

                return true;
            }
        });

    }


}
