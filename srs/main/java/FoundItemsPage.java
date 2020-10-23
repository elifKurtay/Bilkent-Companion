package com.example.chat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
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

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *creates a page that is the forum of the found items that people may browse
 */
public class FoundItemsPage extends AppCompatActivity {
    //properties

    TextView title;
    String category;
    int days;
    ListView list;
    ArrayAdapter adapter;
    private String TAG;
    SearchView searchView;
    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference cont2Ref, contRef, keyRef;
    private boolean already_existing_chat;

    ArrayList<String> names, description, keys , dates, uids, uploader_names;

    // methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_items_page);

        searchView = findViewById(R.id.searchBar);
        searchView.setIconifiedByDefault(false);


        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        days = intent.getIntExtra("days", 0);

        list = findViewById(R.id.itemsList);
        rootRef = FirebaseDatabase.getInstance().getReference();
        //contRef = rootRef.child("Users").child(currentUserID).child("Contacts");
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        initialize();

        //make items clickable
        //TODO pass to chat or the item edit page
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentUserID.equals(uids.get(position)) ) {
                    Intent i = new Intent(FoundItemsPage.this, EditPageFound.class);
                    i.putExtra("name", names.get(position));
                    i.putExtra("description", description.get(position));
                    i.putExtra("key", keys.get(position));
                    startActivity(i);
                }
                else
                {

                    Intent chatIntent = new Intent( FoundItemsPage.this, ChatActivity.class);
                    chatIntent.putExtra("visit_user_id", uids.get(position));
                    chatIntent.putExtra("visit_user_name", uploader_names.get(position));


                    final int pos = position;

                    FirebaseDatabase.getInstance().getReference().child("Users").child(uids.get(pos)).child("Contacts").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            int user_num_contact = Integer.parseInt( dataSnapshot.child("no_of_contacts").getValue().toString());
                            int i = 0;
                            for( DataSnapshot snapshot: dataSnapshot.getChildren() ){
                                i++;
                                if(i <= user_num_contact) {
                                    if (snapshot.child(i + "").child("name").getValue().toString() == uploader_names.get(pos))
                                        already_existing_chat = true;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    if( ! already_existing_chat) {
                        FirebaseDatabase.getInstance().getReference().child("Users").child(uids.get(pos)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int user_num_contact = Integer.parseInt(dataSnapshot.child("Contacts").child("no_of_contacts").getValue().toString());

                                rootRef.child("Users").child(uids.get(pos)).child("Contacts").child("no_of_contacts").setValue("" + (user_num_contact + 1));

                                rootRef.child("Users").child(currentUserID).child("Contacts").child("" + (user_num_contact + 1)).child("name").setValue(uploader_names.get(pos));
                                rootRef.child("Users").child(currentUserID).child("Contacts").child("" + (user_num_contact + 1)).child("id").setValue(uids.get(pos));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                int user_num_contact = Integer.parseInt(dataSnapshot.child("Contacts").child("no_of_contacts").getValue().toString());
                                rootRef.child("Users").child(currentUserID).child("Contacts").child("no_of_contacts").setValue("" + (user_num_contact + 1));

                                rootRef.child("Users").child(uids.get(pos)).child("Contacts").child("" + (user_num_contact + 1)).child("id").setValue(currentUserID);
                                rootRef.child("Users").child(uids.get(pos)).child("Contacts").child("" + (user_num_contact + 1)).child("name").setValue((String) dataSnapshot.child("name").getValue());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    startActivity( chatIntent);


                }

            }
        });

    }

    private void initialize() {
        names = new ArrayList<>();
        description = new ArrayList<>();
        dates = new ArrayList<>();
        keys = new ArrayList<>();
        uids = new ArrayList<>();
        uploader_names = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("LostPost")
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if( snapshot.hasChild("category") ) {
                                String cat = snapshot.child("category").getValue().toString();
                                if (category.equals(cat) || category.equals("all"))
                                {
                                    if(days == - 1)
                                    {
                                        names.add(snapshot.child("name").getValue().toString());
                                        description.add(snapshot.child("tags").getValue().toString());
                                        dates.add(snapshot.child("date").getValue().toString());
                                        keys.add(snapshot.child("key").getValue().toString());
                                        uids.add(snapshot.child("uid").getValue().toString());
                                        uploader_names.add(snapshot.child("uploader_name").getValue().toString());
                                    }
                                    else
                                    {
                                        if (snapshot.hasChild("date"))
                                        {
                                            String date = snapshot.child("date").getValue().toString();
                                            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                                            Date dateUpload = formatter.parse(date, new ParsePosition(0));

                                            Date dateToday = new Date();
                                            String dateString = formatter.format(dateToday);
                                            Log.i("days", dateString);

                                            dateToday = formatter.parse(dateString, new ParsePosition(0));
                                            long diff =dateToday.getTime() - dateUpload.getTime();
                                            float daysDifference = (diff / (1000*60*60*24));

                                            if(daysDifference <= days)
                                            {
                                                names.add(snapshot.child("name").getValue().toString());
                                                description.add(snapshot.child("tags").getValue().toString());
                                                dates.add(snapshot.child("date").getValue().toString());
                                                keys.add(snapshot.child("key").getValue().toString());
                                                uids.add(snapshot.child("uid").getValue().toString());
                                                uploader_names.add(snapshot.child("uploader_name").getValue().toString());
                                            }
                                        }
                                    }
                                }
                            }
                            final CustomFound custom = new CustomFound(FoundItemsPage.this, names, description, dates, uids, uploader_names, keys);
                            list.setAdapter(custom);

                            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit(String query) {
                                    for( String k : description) {
                                        if (k.contains(query))
                                            custom.getFilter().filter(query);
                                        else
                                            Toast.makeText(FoundItemsPage.this, "No match found...", Toast.LENGTH_SHORT).show();
                                    }
                                    return false;
                                }

                                @Override
                                public boolean onQueryTextChange(String newText) {
                                    custom.getFilter().filter(newText);
                                    return false;
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

}




