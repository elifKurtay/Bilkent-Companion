package com.example.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditPageSell extends AppCompatActivity {
    String categorySelect;
    Button delete;
    Button add;
    EditText itemName;
    EditText description;
    EditText priceItem;
    String key;
    String tags;
    String name, price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_page_sell);
        Intent i = getIntent();
        name = i.getStringExtra("name");
        tags = i.getStringExtra("description");
        price = i.getStringExtra("price");
        key = i.getStringExtra("key");

        itemName = findViewById(R.id.editText);
        description = findViewById(R.id.description);
        priceItem = findViewById(R.id.descriptionSell);

        itemName.setText(name);
        description.setText(tags);
        priceItem.setText(price);

        deleteButtonActions();
        addButtonActions();
    }

    public String getName() {
        return itemName.getText().toString();
    }

    public String getDescription() {
        return String.valueOf(description.getText());
    }

    public String getPrice() {
        return String.valueOf(priceItem.getText());
    }

    private void addButtonActions() {
        add = (Button) findViewById(R.id.addItem);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String uid = getUID;
                DatabaseHelper.setSellPostName(key, getName());
                DatabaseHelper.setSellPostTags(key, getDescription());
                DatabaseHelper.setSellPostPrice(key, getPrice());
                Intent added = new Intent(EditPageSell.this, BuyFilter.class);
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
                DatabaseHelper.deleteSellPost(key);
                Intent deleted = new Intent(EditPageSell.this, BuyFilter.class);
                startActivity(deleted);
            }
        });
    }

}