package com.example.chat;

import android.content.Intent;
import android.support.annotation.NonNull;
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
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class LostFilter extends AppCompatActivity  implements AdapterView.OnItemSelectedListener {
    Spinner category;
    TextView timeText;
    SeekBar timeBar;
    Button find;
    FloatingActionButton add;
    String categorySelect;
    int progressValue;
    CheckBox selectAll;

    private FirebaseAuth mAuth;
    private NavigationView nav_view;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_filter);
        mAuth = FirebaseAuth.getInstance();

        //spinner
        category = findViewById(R.id.categorySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.lostFoundCategories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        category.setOnItemSelectedListener(this);
        selectAll = findViewById(R.id.selectAll);
        //seek bar
        seekBar();
        //add and delete buttons
        addButtonActions();
        findButtonActions();

        addNavigationDrawer();

    }

    public void findButtonActions() {
        find = findViewById(R.id.findButton);
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LostFilter.this, FoundItemsPage.class);
                intent.putExtra("category", categorySelect);
                intent.putExtra("days", progressValue);
                Log.i("intent", categorySelect + progressValue);
                startActivity(intent);
            }
        });
    }

    public void addButtonActions() {
        add = findViewById(R.id.floatingActionButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addingPage = new Intent(LostFilter.this, FoundAddItem.class);
                startActivity(addingPage);
            }
        });
    }

    public void seekBar() {
        timeBar = findViewById(R.id.timeSeekBar);
        timeText = findViewById(R.id.timeView);
        //to make the time bar max 1 month the max value is set to 30
        timeBar.setMax(30);
        timeBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progressValue = progress;
                        if (progressValue == 30) {
                            timeText.setText("Latest: 1 month");
                        } else if (progressValue == 0 || progressValue == 1) {
                            timeText.setText("Latest: 1 day");
                        } else {
                            timeText.setText("Latest: " + progressValue + " days");
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (progressValue == 30) {
                            timeText.setText("Latest: 1 month");
                        } else if (progressValue == 0 || progressValue == 1) {
                            timeText.setText("Latest: " + progressValue + " day");
                        } else {
                            timeText.setText("Latest: " + progressValue + " days");
                        }
                    }
                }
        );

        if (selectAll.isChecked())
        {
            timeBar.setEnabled(false);
            progressValue = -1;
        }
        else
        {
            timeBar.setEnabled(true);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        categorySelect = parent.getItemAtPosition(position).toString();
        //THE SELECTED CATEGORY INFORMATION
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void addNavigationDrawer() {
        dl = (DrawerLayout) findViewById(R.id.activity_main);
        t = new ActionBarDrawerToggle(LostFilter.this, dl, R.string.Open, R.string.Close);

        dl.addDrawerListener(t);
        t.syncState();

        nav_view = (NavigationView) findViewById(R.id.nv);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.logout:
                        Toast.makeText(LostFilter.this, "Logout", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        startActivity( new Intent( LostFilter.this, LoginActivity.class));
                        break;
                    case R.id.buy_sell:
                        Toast.makeText(LostFilter.this, "Buy / Sell", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LostFilter.this, BuyFilter.class));
                        finish();
                        break;
                    case R.id.lost_found:
                        Toast.makeText(LostFilter.this, "Lost & Found", Toast.LENGTH_SHORT).show();
                        dl.closeDrawers();
                        break;
                    case R.id.chats:
                        Toast.makeText(LostFilter.this, "Chats", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LostFilter.this, ChatRoomActivity.class));
                        finish();
                        break;
                    case R.id.settings:
                        Toast.makeText(LostFilter.this, "Settings", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LostFilter.this, SettingsActivity.class));
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


