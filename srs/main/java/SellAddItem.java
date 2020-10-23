package com.example.chat;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SellAddItem extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner category;
    String categorySelect;
    Button delete;
    Button add;
    EditText itemName;
    EditText priceText;
    String stringDate, user_name;
    EditText description;
    Button choose;
    Uri uri;
    ImageView image;
    static final int GALLERY_INTENT = 2;
    FirebaseAuth mAuth;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_add_item);

        //spinner
        category = findViewById(R.id.buySellSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.buySellCategories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        category.setOnItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();

        choose = findViewById(R.id.choose);
        //upload = findViewById(R.id.upload);
        image = findViewById(R.id.imageView);

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        mAuth = FirebaseAuth.getInstance();

        //delete and add buttons
        deleteButtonActions();
        addButtonActions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            Picasso.get().load(uri).into(image);
        }
    }

    private void uploadFile(String key)
    {
        if (uri != null)
        {
            StorageReference fileRef = FirebaseStorage.getInstance().getReference().child(key);
            fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(SellAddItem.this,"Upload successful",Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            Toast.makeText(this, "No file selected",Toast.LENGTH_SHORT ).show();
        }
    }

    //get item name
    public String getName() {
        itemName = findViewById(R.id.editText);
        return itemName.getText().toString();
    }

    //get price
    public int getPrice() {
        int price;
        priceText = findViewById(R.id.description);
        price = Integer.parseInt(String.valueOf(priceText.getText()));
        if (price > 1000 || price < 0) {
            Toast.makeText(getApplicationContext(), "Enter a price between 0 and 1000", Toast.LENGTH_SHORT).show();
            return -1;
        } else {
            return price;
        }
    }

    public String getDescription() {
        String descriptionFull;
        description = findViewById(R.id.descriptionSell);
        descriptionFull = String.valueOf(description.getText());
        return descriptionFull;
    }


    private void addButtonActions() {
        add = (Button) findViewById(R.id.addItem);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               //TODO
                uid = mAuth.getCurrentUser().getUid();
                FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(uid).exists() && dataSnapshot.child(uid).child("name").exists() )
                            user_name = dataSnapshot.child(uid).child("name").getValue().toString();
                        String[] info = {getName(), uid, categorySelect, getDescription(),"" + getPrice(), user_name };
                        String s = DatabaseHelper.initialiseSellPost(info).toString();
                        uploadFile(s);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



                Intent added = new Intent(SellAddItem.this, BuyFilter.class);
                startActivity(added);
            }
        });
    }

    private void deleteButtonActions() {
        delete = (Button) findViewById(R.id.deleteButton);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete info
                //pass to the previous page
                Intent deleted = new Intent(SellAddItem.this, BuyFilter.class);
                startActivity(deleted);
            }
        });
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
}



