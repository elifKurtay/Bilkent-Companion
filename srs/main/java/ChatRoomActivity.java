package com.example.chat;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;



public class ChatRoomActivity extends AppCompatActivity {
    private View privateChatsView;
    private RecyclerView chatsList;

    private DatabaseReference chatsRef, usersRef, contRef;
    private FirebaseAuth mAuth;
    private String currentUserID, messageReceiverID, messageReceiverName;
    private MessageAdapter messageAdapter;

    private View groupFragmentView;
    private ListView list_view;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_names = new ArrayList<String>();
    private ArrayList<String> list_of_uis = new ArrayList<String>();


    private NavigationView nav_view;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        addNavigationDrawer();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        chatsRef = usersRef.child( currentUserID).child("Contacts");

        list_view = (ListView) findViewById(R.id.list_view);


        chatsRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Set<String> setUI = new HashSet<>(), setName = new HashSet<>();

                int contactNum = Integer.parseInt(dataSnapshot.child("no_of_contacts").getValue().toString() );

                for( int i = 1; i <= contactNum; i++) {
                    setName.add( dataSnapshot.child("" + i ).child("name").getValue().toString() );
                    setUI.add( dataSnapshot.child("" + i ).child("id").getValue().toString());
                }
                list_of_names.clear();
                list_of_uis.clear();
                list_of_names.addAll( setName);
                list_of_uis.addAll( setUI);

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                        (ChatRoomActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, list_of_names);
                list_view.setAdapter(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();


                list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent chatIntent = new Intent( ChatRoomActivity.this, ChatActivity.class);
                        chatIntent.putExtra( "visit_user_name", list_of_names.get(position));
                        chatIntent.putExtra("visit_user_id", list_of_uis.get(position));
                        startActivity( chatIntent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView profileImage;
        TextView userStatus, userName;

        public ChatsViewHolder(@NonNull View itemView)
        {
            super( itemView);

            profileImage = itemView.findViewById(R.id.users_profile_image);
            userStatus = itemView.findViewById(R.id.user_status);
            userName = itemView.findViewById(R.id.user_profile_name);
        }
    }

    private void addNavigationDrawer() {
        dl = (DrawerLayout)findViewById(R.id.activity_main);
        t = new ActionBarDrawerToggle(ChatRoomActivity.this, dl,R.string.Open, R.string.Close);

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
                        Toast.makeText(ChatRoomActivity.this, "Logout",Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        startActivity( new Intent( ChatRoomActivity.this, LoginActivity.class));
                        break;
                    case R.id.buy_sell:
                        Toast.makeText(ChatRoomActivity.this, "Buy / Sell",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ChatRoomActivity.this, BuyFilter.class));
                        break;
                    case R.id.lost_found:
                        Toast.makeText(ChatRoomActivity.this, "Lost & Found",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ChatRoomActivity.this, LostFilter.class));
                        break;
                    case R.id.chats:
                        Toast.makeText(ChatRoomActivity.this, "Chats",Toast.LENGTH_SHORT).show();
                        dl.closeDrawers();
                        break;
                    case R.id.settings:
                        Toast.makeText(ChatRoomActivity.this, "Settings",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ChatRoomActivity.this, SettingsActivity.class));
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