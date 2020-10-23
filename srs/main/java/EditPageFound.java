package com.example.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class EditPageFound extends AppCompatActivity {
    String categorySelect;
    Button delete;
    Button add;
    EditText itemName;
    EditText description;
    String key;
    String tags;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_page_found);

        Intent i = getIntent();
        name = i.getStringExtra("name");
        tags = i.getStringExtra("description");
        key = i.getStringExtra("key");

        itemName = findViewById(R.id.editText);
        description = findViewById(R.id.description);

        itemName.setText(name);
        description.setText(tags);

        deleteButtonActions();
        addButtonActions();
    }

    //get item name
    public String getName() {
        return itemName.getText().toString();
    }

    public String getDescription() {
        return String.valueOf(description.getText());
    }


    private void addButtonActions() {
        add = (Button) findViewById(R.id.addItem);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String uid = getUID;
                DatabaseHelper.setLostPostName(key, getName());
                DatabaseHelper.setLostPostTags(key, getDescription());
                Intent added = new Intent(EditPageFound.this, LostFilter.class);
                startActivity(added);
            }
        });
    }

    private void deleteButtonActions() {
        delete = (Button) findViewById(R.id.deleteButton);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pass to the previous page
                DatabaseHelper.deleteLostPost(key);
                Intent deleted = new Intent(EditPageFound.this, LostFilter.class);
                startActivity(deleted);
            }
        });
    }


}