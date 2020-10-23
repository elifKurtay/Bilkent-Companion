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

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FoundAddItem extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner category;
    String categorySelect;
    Button delete;
    Button add;
    EditText itemName;
    EditText description;
    String stringDate, uid, user_name;
    Button choose;
    Uri uri;
    ImageView image;
    static final int GALLERY_INTENT = 2;

    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_add_item);

        //spinner
        category = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource( this, R.array.lostFoundCategories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        category.setOnItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();

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
                    Toast.makeText(FoundAddItem.this,"Upload successful", Toast.LENGTH_SHORT).show();
                    Log.i("URI", uri.toString() );
                }
            });
        }
        else
        {
            Toast.makeText(this, "No file selected",Toast.LENGTH_SHORT ).show();
        }
        Log.i("URI", uri.toString() );
    }

    //get item name
    public String getName() {
        itemName = findViewById(R.id.editText);
        return String.valueOf(itemName.getText());
    }

    //get Description and put into String[](by dividing)
    public String getDescription() {
        String descriptionFull;

        description = findViewById(R.id.description);
        descriptionFull = String.valueOf(description.getText());

        return descriptionFull;
    }

    private void addButtonActions() {
        add = (Button)findViewById(R.id.addItem);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the date
                Date date = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                stringDate  = formatter.format(date);

                //getting uploader user information for chat
                uid = mAuth.getCurrentUser().getUid();
                FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(uid).exists() && dataSnapshot.child(uid).child("name").exists() )
                            user_name = dataSnapshot.child(uid).child("name").getValue().toString();
                        String[] info = {getName(), uid, categorySelect, getDescription(),stringDate, user_name }; //databasehelper??
                        String s = DatabaseHelper.initialiseLostPost(info).toString();
                        uploadFile(s);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



                //pass the info to items page to see the item
                Intent added = new Intent(FoundAddItem.this, LostFilter.class);
                startActivity(added);
            }
        });
    }

    private void deleteButtonActions() {
        delete = (Button)findViewById(R.id.deleteButton);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete info TODO
                //pass to the previous page
                Intent deleted = new Intent(FoundAddItem.this, LostFilter.class);
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






