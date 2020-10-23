package com.example.chat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BuyFilter extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner category;
    TextView priceText;
    SeekBar priceBar;
    Button find;
    FloatingActionButton add;
    String categorySelect;
    int progressValue;

    private NavigationView nav_view;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_filter);
        mAuth = FirebaseAuth.getInstance();

        //spinner
        category = findViewById(R.id.buySellSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.buySellCategories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        category.setOnItemSelectedListener(this);

        //seek bar
        seekBar();

        //add and delete buttons
        addButtonActions();
        findButtonActions();

        addNavigationDrawer();
    }

    public void findButtonActions() {
        find = findViewById(R.id.buyButton);
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BuyFilter.this, OnSalePage.class);
                intent.putExtra("category", categorySelect);
                intent.putExtra("price", progressValue);
                startActivity(intent);
            }
        });
    }

    public void addButtonActions() {
        add = findViewById(R.id.floatingActionButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addingPage = new Intent(BuyFilter.this, SellAddItem.class);
                startActivity(addingPage);
            }
        });
    }

    public void seekBar() {
        priceBar = findViewById(R.id.priceBar);
        priceText = findViewById(R.id.priceTag);
        //to make the time bar max 1 month the max value is set to 30
        priceBar.setMax(1000);
        priceBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progressValue = progress;
                        priceText.setText("Upper Limit: " + progressValue + " TL");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        priceText.setText("Upper Limit: " + progressValue + " TL");
                    }
                }
        );
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        categorySelect = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    private void addNavigationDrawer() {
        dl = (DrawerLayout) findViewById(R.id.activity_main);
        t = new ActionBarDrawerToggle(BuyFilter.this, dl, R.string.Open, R.string.Close);

        dl.addDrawerListener(t);
        t.syncState();

        nav_view = (NavigationView) findViewById(R.id.nv);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.logout:
                        Toast.makeText(BuyFilter.this, "Logout", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        startActivity( new Intent( BuyFilter.this, LoginActivity.class));
                        break;
                    case R.id.buy_sell:
                        Toast.makeText(BuyFilter.this, "Buy / Sell", Toast.LENGTH_SHORT).show();
                        dl.closeDrawers();
                        break;
                    case R.id.lost_found:
                        Toast.makeText(BuyFilter.this, "Lost & Found", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(BuyFilter.this, LostFilter.class));
                        finish();
                        break;
                    case R.id.chats:
                        Toast.makeText(BuyFilter.this, "Chats", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(BuyFilter.this, ChatRoomActivity.class));
                        finish();
                        break;
                    case R.id.settings:
                        Toast.makeText(BuyFilter.this, "Settings", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(BuyFilter.this, SettingsActivity.class));
                        finish();
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
