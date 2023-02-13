package com.example.startendo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.startendo.Chat.ChatObject;
import com.example.startendo.Chat.MediaAdapter;
import com.example.startendo.Chat.MessageAdapter;
import com.example.startendo.Chat.MessageObject;

import com.example.startendo.databinding.ActivityChatBinding;
import com.example.startendo.databinding.ActivityChatPageBinding;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    ArrayList<UserObject> CreaterList;

    int PICK_IMAGE_INTENT = 1;
    TextView m1heading;
    TextView m2heading;
    TextView m3heading;
    TextView m4heading;
    private RecyclerView mChat;
    private RecyclerView mChat1;
    private RecyclerView mChat2;
    private RecyclerView mChat3;
    private RecyclerView mChat4;
    public MessageAdapter mChatAdapter;
    public MessageAdapter mChatAdapter1;
    public MessageAdapter mChatAdapter2;
    public MessageAdapter mChatAdapter3;
    public MessageAdapter mChatAdapter4;

    public RecyclerView.LayoutManager mChatLayoutManager;

    public RecyclerView.LayoutManager mChatLayoutManager1;

    public RecyclerView.LayoutManager mChatLayoutManager2;

    public RecyclerView.LayoutManager mChatLayoutManager3;

    public RecyclerView.LayoutManager mChatLayoutManager4;
    ChatObject mChatObject;
    private RecyclerView mMedia;
    private MediaAdapter mMediaAdapter;
    private RecyclerView.LayoutManager mMediaLayoutManager;
    EditText mMessage;
    DatabaseReference mMessageDb;
    DatabaseReference mUserinfoDb;
    ArrayList<String> mediaIdList = new ArrayList<>();
    ArrayList<String> mediaUriList = new ArrayList<>();
    ArrayList<MessageObject> messageList;
    ArrayList<MessageObject> messageList1;
    ArrayList<MessageObject> messageList2;
    ArrayList<MessageObject> messageList3;
    ArrayList<MessageObject> messageList4;
    int totalMediaUploaded = 0;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {

        super.onCreate(bundle);
        Fresco.initialize(this);
        setContentView(R.layout.activity_chat);
        this.mChatObject = (ChatObject) getIntent().getSerializableExtra("chatObject");
        this.mMessageDb = FirebaseDatabase.getInstance().getReference().child("chat").child(this.mChatObject.getChatId()).child("messages");
        this.mUserinfoDb = FirebaseDatabase.getInstance().getReference().child("chat").child(this.mChatObject.getChatId()).child("info").child("users");
        this.messageList = new ArrayList<>();
        this.messageList1 = new ArrayList<>();
        this.messageList2 = new ArrayList<>();
        this.messageList3 = new ArrayList<>();
        this.messageList4 = new ArrayList<>();
        this.CreaterList = new ArrayList<>();



        getUserArraylist();
        ((Button) findViewById(R.id.Executionbutton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this.getApplicationContext(), DrawingActivity.class);
                intent.putExtra("BOARD_ID", ChatActivity.this.mChatObject.getChatId());
                ChatActivity.this.startActivity(intent);
            }
        });
        ((Button) findViewById(R.id.sendbutton)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ChatActivity.this.sendMessage();
                ChatActivity.this.mChatAdapter.notifyDataSetChanged();
                ChatActivity.this.mChatAdapter1.notifyDataSetChanged();
                ChatActivity.this.mChatAdapter2.notifyDataSetChanged();
                ChatActivity.this.mChatAdapter3.notifyDataSetChanged();
                ChatActivity.this.mChatAdapter4.notifyDataSetChanged();
            }
        });
        ((Button) findViewById(R.id.addMedia)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ChatActivity.this.openGallery();
            }
        });
        initializeRecyclerView();
        initializeMediaView();
        initializeRecyclerView1();
        initializeRecyclerView2();
        initializeRecyclerView3();
        initializeRecyclerView4();
        getChatMessages();
    }

    public void initializeMediaView() {
        this.mediaUriList = new ArrayList<>();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mediaList);
        this.mMedia = recyclerView;
        recyclerView.setNestedScrollingEnabled(false);
        this.mMedia.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), 0, false);
        this.mMediaLayoutManager = linearLayoutManager;
        this.mMedia.setLayoutManager(linearLayoutManager);
        MediaAdapter mediaAdapter = new MediaAdapter(getApplicationContext(), this.mediaUriList);
        this.mMediaAdapter = mediaAdapter;
        this.mMedia.setAdapter(mediaAdapter);
    }


    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra("android.intent.extra.ALLOW_MULTIPLE", true);
        intent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(Intent.createChooser(intent, "Select Picture(s)"), this.PICK_IMAGE_INTENT);
    }


    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1 && i == this.PICK_IMAGE_INTENT) {
            if (intent.getClipData() == null) {
                this.mediaUriList.add(intent.getData().toString());
            } else {
                for (int i3 = 0; i3 < intent.getClipData().getItemCount(); i3++) {
                    this.mediaUriList.add(intent.getClipData().getItemAt(i3).getUri().toString());
                }
            }
            this.mMediaAdapter.notifyDataSetChanged();
        }
    }

    private void getUserArraylist() {
        this.mUserinfoDb.addValueEventListener(new ValueEventListener() {
            public void onCancelled(DatabaseError databaseError) {
            }

            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot key : dataSnapshot.getChildren()) {
                    UserObject userObject = new UserObject(key.getKey().toString());
                    if (!userObject.getuId().equals(FirebaseAuth.getInstance().getUid())) {
                        ChatActivity.this.CreaterList.add(userObject);
                    }
                }
            }
        });
    }

    private void getChatMessages() {
        this.mMessageDb.addChildEventListener(new ChildEventListener() {
            public void onCancelled(DatabaseError databaseError) {
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String str) {
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String str) {
            }

            public void onChildAdded(DataSnapshot dataSnapshot, String str) {
                if (dataSnapshot.exists()) {
                    ArrayList arrayList = new ArrayList();
                    String str2 = "";
                    String obj = dataSnapshot.child("text").getValue() != null ? dataSnapshot.child("text").getValue().toString() : str2;
                    String obj2 = dataSnapshot.child("creator").getValue() != null ? dataSnapshot.child("creator").getValue().toString() : str2;
                    if (dataSnapshot.child("creatorphone").getValue() != null) {
                        str2 = dataSnapshot.child("creatorphone").getValue().toString();
                    }
                    String str3 = str2;
                    long longValue = dataSnapshot.child("timestamp").getValue(Long.class) != null ? ((Long) dataSnapshot.child("timestamp").getValue(Long.class)).longValue() : 0;
                    if (dataSnapshot.child("media").getChildrenCount() > 0) {
                        for (DataSnapshot value : dataSnapshot.child("media").getChildren()) {
                            arrayList.add(value.getValue().toString());
                        }
                    }
                    MessageObject messageObject = new MessageObject(dataSnapshot.getKey(), obj2, obj, arrayList, longValue);
                    messageObject.setSenderphone(str3);
                    if (obj2.equals(FirebaseAuth.getInstance().getUid())) {
                        ChatActivity.this.messageList.add(messageObject);
                    } else if (obj2.equals(ChatActivity.this.CreaterList.get(0).getuId().toString())) {
                        ChatActivity.this.messageList1.add(messageObject);
                    } else if (obj2.equals(ChatActivity.this.CreaterList.get(1).getuId().toString())) {
                        ChatActivity.this.messageList2.add(messageObject);
                    } else if (obj2.equals(ChatActivity.this.CreaterList.get(2).getuId().toString())) {
                        ChatActivity.this.messageList3.add(messageObject);
                    } else if (obj2.equals(ChatActivity.this.CreaterList.get(3).getuId().toString())) {
                        ChatActivity.this.messageList4.add(messageObject);
                    }
                    FirebaseDatabase.getInstance().getReference().child("user").addValueEventListener(new ValueEventListener() {
                        public void onCancelled(DatabaseError databaseError) {
                        }

                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ChatActivity.this.m1heading = (TextView) ChatActivity.this.findViewById(R.id.messalist1heading);
                            ChatActivity.this.m2heading = (TextView) ChatActivity.this.findViewById(R.id.mlist2);
                            ChatActivity.this.m3heading = (TextView) ChatActivity.this.findViewById(R.id.mlist3);
                            ChatActivity.this.m4heading = (TextView) ChatActivity.this.findViewById(R.id.mlist4);
                            ChatActivity.this.m1heading.setText(dataSnapshot.child(ChatActivity.this.CreaterList.get(0).getuId()).child("phone").getValue().toString());
                            ChatActivity.this.m2heading.setText(dataSnapshot.child(ChatActivity.this.CreaterList.get(1).getuId()).child("phone").getValue().toString());
                            ChatActivity.this.m3heading.setText(dataSnapshot.child(ChatActivity.this.CreaterList.get(2).getuId()).child("phone").getValue().toString());
                            ChatActivity.this.m4heading.setText(dataSnapshot.child(ChatActivity.this.CreaterList.get(3).getuId()).child("phone").getValue().toString());
                        }
                    });
                    ChatActivity.this.mChatLayoutManager.scrollToPosition(ChatActivity.this.messageList.size() - 1);
                    ChatActivity.this.mChatLayoutManager1.scrollToPosition(ChatActivity.this.messageList1.size() - 1);
                    ChatActivity.this.mChatLayoutManager2.scrollToPosition(ChatActivity.this.messageList2.size() - 1);
                    ChatActivity.this.mChatLayoutManager3.scrollToPosition(ChatActivity.this.messageList3.size() - 1);
                    ChatActivity.this.mChatLayoutManager4.scrollToPosition(ChatActivity.this.messageList4.size() - 1);
                }
            }

            public void onChildRemoved(DataSnapshot dataSnapshot) {
                ChatActivity.this.mChatAdapter.notifyDataSetChanged();
                ChatActivity.this.mChatAdapter1.notifyDataSetChanged();
                ChatActivity.this.mChatAdapter2.notifyDataSetChanged();
                ChatActivity.this.mChatAdapter3.notifyDataSetChanged();
                ChatActivity.this.mChatAdapter4.notifyDataSetChanged();
            }
        });
    }


   private void sendMessage() {
        this.mMessage = (EditText) findViewById(R.id.message);
        String key = this.mMessageDb.push().getKey();
        final DatabaseReference child = this.mMessageDb.child(key);
        Date date = new Date();
        final HashMap hashMap = new HashMap();
        if (!this.mMessage.getText().toString().isEmpty()) {
            hashMap.put("text", this.mMessage.getText().toString());
        }
        hashMap.put("timestamp", Long.valueOf(date.getTime()));
        hashMap.put("creator", FirebaseAuth.getInstance().getUid());
        hashMap.put("creatorphone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        if (!this.mediaUriList.isEmpty()) {
            Iterator<String> it = this.mediaUriList.iterator();
            while (it.hasNext()) {
                String key2 = child.child("media").push().getKey();
                this.mediaIdList.add(key2);
                final StorageReference child2 = FirebaseStorage.getInstance().getReference().child("chat").child(this.mChatObject.getChatId()).child(key).child(key2);
                child2.putFile(Uri.parse(it.next())).addOnSuccessListener((OnSuccessListener) new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        child2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            public void onSuccess(Uri uri) {
                                Map map = hashMap;
                                map.put("/media/" + ChatActivity.this.mediaIdList.get(ChatActivity.this.totalMediaUploaded) + "/", uri.toString());
                                ChatActivity chatActivity = ChatActivity.this;
                                chatActivity.totalMediaUploaded = chatActivity.totalMediaUploaded + 1;
                                if (ChatActivity.this.totalMediaUploaded == ChatActivity.this.mediaUriList.size()) {
                                    ChatActivity.this.updateDatabaseWithNewMessage(child, hashMap);
                                }
                            }
                        });
                    }
                });
            }
        } else if (!this.mMessage.getText().toString().isEmpty()) {
            updateDatabaseWithNewMessage(child, hashMap);
        }
    }

    /* access modifiers changed from: private */
    public void updateDatabaseWithNewMessage(DatabaseReference databaseReference, Map map) {
        databaseReference.updateChildren(map);
        String obj = map.get("text") != null ? map.get("text").toString() : "Sent Media";
        Iterator<UserObject> it = this.mChatObject.getUserObjectArrayList().iterator();
        while (it.hasNext()) {
            UserObject next = it.next();
            if (!next.getuId().equals(FirebaseAuth.getInstance().getUid())) {

            }
        }
        this.mMessage.setText((CharSequence) null);
        this.mediaUriList.clear();
        this.mediaIdList.clear();
        this.mMediaAdapter.notifyDataSetChanged();
    }

    public void initializeRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.myChatView);
        this.mChat = recyclerView;
        recyclerView.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), 1, false);
        this.mChatLayoutManager = linearLayoutManager;
        this.mChat.setLayoutManager(linearLayoutManager);
        MessageAdapter messageAdapter = new MessageAdapter(this.messageList, this, this.mChatObject.getChatId());
        this.mChatAdapter = messageAdapter;
        this.mChat.setAdapter(messageAdapter);
        this.mChat.getRecycledViewPool().setMaxRecycledViews(0, 0);
    }

    public void initializeRecyclerView1() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.chatView1);
        this.mChat1 = recyclerView;
        recyclerView.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), 1, false);
        this.mChatLayoutManager1 = linearLayoutManager;
        this.mChat1.setLayoutManager(linearLayoutManager);
        MessageAdapter messageAdapter = new MessageAdapter(this.messageList1, this, this.mChatObject.getChatId());
        this.mChatAdapter1 = messageAdapter;
        this.mChat1.setAdapter(messageAdapter);
        this.mChat1.getRecycledViewPool().setMaxRecycledViews(0, 0);
    }

    public void initializeRecyclerView2() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.chatView2);
        this.mChat2 = recyclerView;
        recyclerView.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), 1, false);
        this.mChatLayoutManager2 = linearLayoutManager;
        this.mChat2.setLayoutManager(linearLayoutManager);
        MessageAdapter messageAdapter = new MessageAdapter(this.messageList2, this, this.mChatObject.getChatId());
        this.mChatAdapter2 = messageAdapter;
        this.mChat2.setAdapter(messageAdapter);
        this.mChat2.getRecycledViewPool().setMaxRecycledViews(0, 0);
    }

    public void initializeRecyclerView3() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.chatView33);
        this.mChat3 = recyclerView;
        recyclerView.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), 1, false);
        this.mChatLayoutManager3 = linearLayoutManager;
        this.mChat3.setLayoutManager(linearLayoutManager);
        MessageAdapter messageAdapter = new MessageAdapter(this.messageList3, this, this.mChatObject.getChatId());
        this.mChatAdapter3 = messageAdapter;
        this.mChat3.setAdapter(messageAdapter);
        this.mChat3.getRecycledViewPool().setMaxRecycledViews(0, 0);
    }

    public void initializeRecyclerView4() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.chatView44);
        this.mChat4 = recyclerView;
        recyclerView.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), 1, false);
        this.mChatLayoutManager4 = linearLayoutManager;
        this.mChat4.setLayoutManager(linearLayoutManager);
        MessageAdapter messageAdapter = new MessageAdapter(this.messageList4, this, this.mChatObject.getChatId());
        this.mChatAdapter4 = messageAdapter;
        this.mChat4.setAdapter(messageAdapter);
        this.mChat4.getRecycledViewPool().setMaxRecycledViews(0, 0);
    }
}
