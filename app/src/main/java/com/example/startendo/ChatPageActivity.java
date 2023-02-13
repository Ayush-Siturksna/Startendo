package com.example.startendo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.startendo.Utils.CountryToPhonePrefix;
import com.example.startendo.databinding.ActivityChatPageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ChatPageActivity extends AppCompatActivity {


    private RecyclerView mUserList,mSelectedList;

    private RecyclerView.LayoutManager mUserListLayoutManager ,mSelectedListLayoutManager;
    public UserListAdapter mUserListAdapter ;
    public SelectedlistAdapter mSelectedListAdapter ;

    ArrayList<UserObject> userList,contactList,chattersList,Selectedlist;

    ActivityChatPageBinding binding ;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth=FirebaseAuth.getInstance();

        userList = new ArrayList<>();
        Selectedlist = new ArrayList<>();

        Button mCreate= findViewById(R.id.CreateTeam);

        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.Teamname.getText().toString().isEmpty()){
                    binding.Teamname.setError("Enter your TeamName");
                    return;
                }
                createChat();
            }
        });



        contactList = new ArrayList<>();
        getContactList();
        for (UserObject mUser2:userList)
        {    UserObject p= new UserObject(mUser2.getuId(),mUser2.getName(),mUser2.getPhone());

            Selectedlist.add(p);
            mSelectedListAdapter.notifyDataSetChanged();
        }
        initializeRecyclerView();

        initializeRecyclerViewSelected();








    }

    public void createChat() {
        String key = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();
        DatabaseReference child = FirebaseDatabase.getInstance().getReference().child("chat").child(key).child("info");
        DatabaseReference child2 = FirebaseDatabase.getInstance().getReference().child("user");
        DatabaseReference child3 = FirebaseDatabase.getInstance().getReference().child("boardmetas").child(key);
        String obj = this.binding.Teamname.getText().toString();
        String uid = FirebaseAuth.getInstance().getUid();
        HashMap hashMap = new HashMap();
        hashMap.put("id", key);
        hashMap.put("TeamName", obj);
        hashMap.put("TeamCreatorId", uid);
        hashMap.put("users/" + uid, true);
        HashMap hashMap2 = new HashMap();
        hashMap2.put("createdAt", ServerValue.TIMESTAMP);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        hashMap2.put("width", Integer.valueOf(displayMetrics.widthPixels));
        hashMap2.put("height", Integer.valueOf(displayMetrics.heightPixels));
        Boolean bool = false;
        if (this.Selectedlist.size() == 4) {
            bool = true;
        }
        if (!bool.booleanValue()) {
            Toast.makeText(this, "Team Creation failed", 0).show();
            this.mUserListAdapter.notifyDataSetChanged();
        }
        if (bool.booleanValue()) {
            Iterator<UserObject> it = this.Selectedlist.iterator();
            while (it.hasNext()) {
                UserObject next = it.next();
                hashMap.put("users/" + next.getuId(), true);
                child2.child(next.getuId()).child("chat").child(key).setValue(true);
            }
            child.updateChildren(hashMap);
            child3.updateChildren(hashMap2);
            child2.child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).setValue(true);
            Toast.makeText(this, "Team Creation Success ", 0).show();
            startActivity(new Intent(this, ChatmainActivity.class));
        }
    }



    private void  getContactList()   {

        String ISOprefix = getCountryISO();
        Cursor phones= getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,
                null,null);
        while (phones.moveToNext()) {



            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            phone = phone.replace(" ", "");
            phone = phone.replace("-", "");
            phone = phone.replace("(", "");
            phone = phone.replace(")", "");
            if (!String.valueOf(phone.charAt(0)).equals("+"))
                phone = ISOprefix + phone;


            UserObject mContact = new UserObject("",name, phone);
            contactList.add(mContact);

            getUserDetails(mContact);




        }

    }

    private void getUserDetails(UserObject mContact) {

        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user");
        mUserDB.keepSynced(true);
        Query query =mUserDB.orderByChild("phone").equalTo(mContact.getPhone());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    String phone= "" ,
                            name="" ;


                    int count=0;

                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {

                        if (childSnapshot.child("phone").getValue() != null)
                            phone = childSnapshot.child("phone").getValue().toString();

                        name=mContact.getName();




                        UserObject mUser = new UserObject(childSnapshot.getKey(),name , phone);




                        for (UserObject phone1:userList)
                        {
                            if (phone1.getPhone().equals(phone))
                                count++;

                        }

                        auth=FirebaseAuth.getInstance();
                        if (count==0)
                        {  if(!auth.getCurrentUser().getPhoneNumber().equals(phone))
                            userList.add(mUser);}
                        mUserListAdapter.notifyDataSetChanged();
                        mSelectedListAdapter.notifyDataSetChanged();
                        return;


                    }





                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }




    private String getCountryISO() {
        String iso1 =null;

        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if (telephonyManager.getNetworkCountryIso()!=null)
            if (!telephonyManager.getNetworkCountryIso().toString().equals(""))
                iso1= telephonyManager.getNetworkCountryIso().toString();




        return CountryToPhonePrefix.getPhone(iso1);

    }


    public void  initializeRecyclerView() {
        mUserList =findViewById(R.id.UserList) ;
        mUserList.setNestedScrollingEnabled(false);
        mUserList.setHasFixedSize(false);
        mUserListLayoutManager=  new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        mUserList.setLayoutManager(mUserListLayoutManager);
        mUserListAdapter = new UserListAdapter(userList,ChatPageActivity.this);
        mUserList.setAdapter(mUserListAdapter);
    }
    public void  initializeRecyclerViewSelected() {
        mSelectedList =findViewById(R.id.selectedlist) ;
        mSelectedList.setNestedScrollingEnabled(false);
        mSelectedList.setHasFixedSize(false);
        mSelectedListLayoutManager=  new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
        mSelectedList.setLayoutManager(mSelectedListLayoutManager);
        mSelectedListAdapter = new SelectedlistAdapter(Selectedlist,this);
        mSelectedList.setAdapter(mSelectedListAdapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mymenu ,menu);
        MenuItem menuItem= menu.findItem(R.id.search_icon);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                mUserListAdapter.getFilter().filter(newText);
                mUserListAdapter.notifyDataSetChanged();


                return true;
            }
        });
        return true;
    }


    public void makeselection(View view, int i) {
        if (!this.userList.get(i).getSelected().booleanValue()) {
            this.Selectedlist.add(this.userList.get(i));
            this.userList.get(i).setSelected(true);
            ArrayList<UserObject> arrayList = this.userList;
            arrayList.remove(arrayList.get(i));
        }
        if (this.Selectedlist.size() != 0) {
            this.mSelectedList.setVisibility(View.VISIBLE);
            this.mSelectedListAdapter.notifyDataSetChanged();
        }
        this.mSelectedListAdapter.notifyDataSetChanged();
        this.mUserListAdapter.notifyDataSetChanged();
    }

    public void deleteselecteditem(View view, int i) {
        if (this.Selectedlist.get(i).getSelected().booleanValue()) {
            this.Selectedlist.get(i).setSelected(false);
            this.userList.add(this.Selectedlist.get(i));
            ArrayList<UserObject> arrayList = this.Selectedlist;
            arrayList.remove(arrayList.get(i));
            if (this.Selectedlist.size() == 0) {
                this.mSelectedList.setVisibility(View.GONE);
                this.mSelectedListAdapter.notifyDataSetChanged();
            }
            this.mSelectedListAdapter.notifyDataSetChanged();
            this.mUserListAdapter.notifyDataSetChanged();
        }
    }


}