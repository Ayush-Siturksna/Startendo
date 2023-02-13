package com.example.startendo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.startendo.Chat.ChatListAdapter;
import com.example.startendo.Chat.ChatObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Iterator;

public class ChatmainActivity extends AppCompatActivity  {
    ArrayList<ChatObject> chatList;
    private RecyclerView mChatList;
    public ChatListAdapter mChatListAdapter;
    private RecyclerView.LayoutManager mChatListLayoutManager;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.chatList = new ArrayList<>();
        setContentView(R.layout.activity_chatmain);
        initializeRecyclerView();
        getUserChatList();
    }



    private void getUserChatList() {
        FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat").addValueEventListener(new ValueEventListener() {
            public void onCancelled(DatabaseError databaseError) {
            }

            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot key : dataSnapshot.getChildren()) {
                        ChatObject chatObject = new ChatObject(key.getKey(), (String) null, (String) null, (String) null, 0);
                        boolean z = false;
                        Iterator<ChatObject> it = ChatmainActivity.this.chatList.iterator();
                        while (it.hasNext()) {
                            if (it.next().getChatId().equals(chatObject.getChatId())) {
                                z = true;
                            }
                        }
                        if (!z) {
                            ChatmainActivity.this.getChatData(chatObject.getChatId());
                            ChatmainActivity.this.chatList.add(chatObject);
                            ChatmainActivity.this.mChatListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    /* access modifiers changed from: private */
    private void getChatData(String chatId) {
        DatabaseReference mChatDB = FirebaseDatabase.getInstance().getReference().child("chat").child(chatId).child("info");
        mChatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String chatId="";
                    String NameId="";

                    if (snapshot.child("id").getValue()!=null&&snapshot.child("TeamName").getValue()!=null){
                        chatId=snapshot.child("id").getValue().toString();
                        NameId=snapshot.child("TeamName").getValue().toString();


                        String text = "",phoneid="";
                        long ts1=0;
                        if (snapshot.child("text").getValue()!=null)
                            text= snapshot.child("text").getValue().toString();
                        if (snapshot.child("timestamp").getValue(Long.class)!=null)
                            ts1= snapshot.child("timestamp").getValue(Long.class);



                        if (snapshot.child("creatorphone").getValue()!=null)
                            phoneid= snapshot.child("creatorphone").getValue().toString();


                        for (DataSnapshot userSnapshot: snapshot.child("users").getChildren()){
                            for (ChatObject mChat: chatList){
                                if (mChat.getChatId().equals(chatId)){

                                    mChat.setName(NameId);


                                    UserObject mUser=new UserObject(userSnapshot.getKey(),null,null);
                                    mChat.addUserToArrayList(mUser);
                                    // getUserData(mUser);

                                }
                            }

                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserData(UserObject userObject) {
        FirebaseDatabase.getInstance().getReference().child("user").child(userObject.getuId()).addValueEventListener(new ValueEventListener() {
            public void onCancelled(DatabaseError databaseError) {
            }

            public void onDataChange(DataSnapshot dataSnapshot) {
                UserObject userObject = new UserObject(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), dataSnapshot.child("phone").getValue().toString());
                if (dataSnapshot.child("notificationkey").getValue() != null) {
                    userObject.setNotificationkey(dataSnapshot.child("notificationkey").getValue().toString());
                }
                Iterator<ChatObject> it = ChatmainActivity.this.chatList.iterator();
                while (it.hasNext()) {
                    Iterator<UserObject> it2 = it.next().getUserObjectArrayList().iterator();
                    while (it2.hasNext()) {
                        UserObject next = it2.next();
                        if (next.getuId().equals(userObject.getuId())) {
                            next.setNotificationkey(userObject.getNotificationkey());
                        }
                    }
                }
                ChatmainActivity.this.mChatListAdapter.notifyDataSetChanged();
            }
        });
    }

    public void initializeRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.chatList);
        this.mChatList = recyclerView;
        recyclerView.setNestedScrollingEnabled(false);
        this.mChatList.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), 1, false);
        this.mChatListLayoutManager = linearLayoutManager;
        this.mChatList.setLayoutManager(linearLayoutManager);
        ChatListAdapter chatListAdapter = new ChatListAdapter(this.chatList, this);
        this.mChatListAdapter = chatListAdapter;
        this.mChatList.setAdapter(chatListAdapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu2, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_icon2).getActionView();
        searchView.setQueryHint("Search Team");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String str) {
                return false;
            }

            public boolean onQueryTextChange(String str) {
                ChatmainActivity.this.mChatListAdapter.getFilter().filter(str);
                ChatmainActivity.this.mChatListAdapter.notifyDataSetChanged();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.NewTeam) {
            startActivity(new Intent(getApplicationContext(), ChatPageActivity.class));
            return true;
        } else if (itemId == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        } else if (itemId != R.id.settings) {
            return true;
        } else {
            Toast.makeText(this, "Settings clicked", 0).show();
            return true;
        }
    }
}
