package com.example.chat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OnSalePage extends AppCompatActivity {

    TextView title;
    String category;
    int price;
    ListView list;
    ArrayAdapter adapter;
    private String TAG;
    SearchView searchView;
    private boolean already_existing_chat;

    private DatabaseReference rootRef, itemsRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    ArrayList<String> names, description, keys , prices, uids, uploader_names;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_sale_page);

        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        price = intent.getIntExtra("price", 0);
        Log.i("sell", category + price);

        searchView = (SearchView) findViewById(R.id.searchBar);
        searchView.setIconifiedByDefault(false);

        list = findViewById(R.id.itemsList);
        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        initialize();

        //make items clickable
        //TODO pass to chat or the item edit page
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentUserID.equals(uids.get(position)) ) {
                    Intent i = new Intent(OnSalePage.this, EditPageSell.class);
                    i.putExtra("name", names.get(position));
                    i.putExtra("description", description.get(position));
                    i.putExtra("key", keys.get(position));
                    startActivity(i);
                }
                else
                {

                    Intent chatIntent = new Intent(OnSalePage.this, ChatActivity.class);
                    chatIntent.putExtra("visit_user_id", uids.get(position));
                    chatIntent.putExtra("visit_user_name", uploader_names.get(position) );

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
                    startActivity(chatIntent);
                }
            }
        });
    }

    private void initialize() {
        names = new ArrayList<>();
        description = new ArrayList<>();
        prices = new ArrayList<>();
        keys = new ArrayList<>();
        uids = new ArrayList<>();
        uploader_names = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("SellPost")
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            if (snapshot.hasChild("category")) {
                                String cat = snapshot.child("category").getValue().toString();
                                Log.i("sell", cat);
                                if (category.equals(cat) || category.equals("all")) {
                                    if (snapshot.hasChild("price")) {
                                        String priceString = snapshot.child("price").getValue().toString();
                                        int itemPrice = Integer.parseInt(priceString);

                                        if (itemPrice <= price) {
                                            names.add(snapshot.child("name").getValue().toString());
                                            description.add(snapshot.child("tags").getValue().toString());
                                            prices.add(snapshot.child("price").getValue().toString());
                                            keys.add(snapshot.child("key").getValue().toString());
                                            uids.add(snapshot.child("uid").getValue().toString());
                                            uploader_names.add(snapshot.child("uploader_name").getValue().toString());

                                        }
                                    }
                                }
                            }
                        }
                        final Custom custom = new Custom(OnSalePage.this, names, description, prices, uids, uploader_names, keys );
                        list.setAdapter(custom);
                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {

                                if ( contains(names, query))
                                     custom.getFilter().filter(query);
                                else
                                     Toast.makeText(OnSalePage.this, "No match found...", Toast.LENGTH_SHORT).show();

                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {
                                custom.getFilter().filter(newText);
                                return false;
                            }
                        });
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    public boolean contains( ArrayList<String> list, String word) {

        for(int i = 0; i < list.size(); i++ ) {
            if( list.get(i).contains(word) )
                return true;
        }
        return false;

    }
}

