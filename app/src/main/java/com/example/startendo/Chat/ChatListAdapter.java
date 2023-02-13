package com.example.startendo.Chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.example.startendo.R;
import com.example.startendo.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> implements Filterable {
    ArrayList<ChatObject> ChatListFull;
    ArrayList<ChatObject> chatList;
    Context context;
   /* Filter myFilter = new Filter() {

        protected Filter.FilterResults performFiltering(CharSequence charSequence) {
            if (ChatListAdapter.this.ChatListFull.size() == 0) {
                ChatListAdapter.this.ChatListFull = new ArrayList<>(ChatListAdapter.this.chatList);
            }
            ArrayList arrayList = new ArrayList();
            if (charSequence.toString().isEmpty()) {
                arrayList.addAll(ChatListAdapter.this.ChatListFull);
            } else {
                String trim = charSequence.toString().toLowerCase().trim();
                Iterator<ChatObject> it = ChatListAdapter.this.ChatListFull.iterator();
                while (it.hasNext()) {
                    ChatObject next = it.next();
                    if (next.getName().toString().toLowerCase().contains(trim)) {
                        arrayList.add(next);
                    }
                }
            }
            Filter.FilterResults filterResults = new Filter.FilterResults();
            filterResults.values = arrayList;
            return filterResults;
        }

        public void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
            ChatListAdapter.this.chatList.clear();
            ChatListAdapter.this.chatList.addAll((ArrayList) filterResults.values);
            ChatListAdapter.this.notifyDataSetChanged();
        }
    };*/




    @Override
    public Filter getFilter() {
        return myFilter;
    }
    Filter myFilter= new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            if(ChatListFull.size()==0)  ChatListFull=new ArrayList<>(chatList) ;

            ArrayList<ChatObject> filteredList= new ArrayList<>();
            if (constraint.toString().isEmpty()){


                filteredList.addAll(ChatListFull);
            }
            else{
                String filterPattern= constraint.toString().toLowerCase().trim();
                for (ChatObject item:ChatListFull){
                    if (item.getName().toString().toLowerCase().contains(filterPattern))
                        filteredList.add(item);
                }
            }
            FilterResults results = new FilterResults();
            results.values=filteredList;

            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            chatList.clear();
            chatList.addAll((ArrayList<ChatObject>)results.values);
            notifyDataSetChanged();

        }
    };



    public ChatListAdapter(ArrayList<ChatObject> arrayList, Context context2) {
        this.chatList = arrayList;
        this.ChatListFull = new ArrayList<>(arrayList);
        this.context = context2;
    }

    public ChatListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat, (ViewGroup) null, false);
        inflate.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new ChatListViewHolder(inflate);
    }

    public void onBindViewHolder(final ChatListViewHolder chatListViewHolder, final int position) {
        DatabaseReference child = FirebaseDatabase.getInstance().getReference().child("chat").child(this.chatList.get(chatListViewHolder.getAdapterPosition()).getChatId()).child("messages");
        child.keepSynced(true);
        child.addChildEventListener(new ChildEventListener() {
            public void onCancelled(DatabaseError databaseError) {
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String str) {

            }

            public void onChildMoved(DataSnapshot dataSnapshot, String str) {
            }

            public void onChildAdded(DataSnapshot dataSnapshot, String str) {
                if (dataSnapshot.exists()) {
                    long j = 0;
                    String str2 = "";
                    String obj = dataSnapshot.child("text").getValue() != null ? dataSnapshot.child("text").getValue().toString() : str2;
                    if (dataSnapshot.child("timestamp").getValue(Long.class) != null) {
                        j = ((Long) dataSnapshot.child("timestamp").getValue(Long.class)).longValue();
                    }
                    if (dataSnapshot.child("creatorphone").getValue() != null) {
                        str2 = dataSnapshot.child("creatorphone").getValue().toString();
                    }
                    if (FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().equals(str2)) {
                        chatListViewHolder.mLastMessage.setText(obj);
                    } else {
                        TextView textView = chatListViewHolder.mLastMessage;
                        textView.setText(str2 + ":" + obj);
                    }
                    chatListViewHolder.mTime.setText(new SimpleDateFormat("hh:mm a").format(new Date(j)));
                }
            }

            public void onChildRemoved(DataSnapshot dataSnapshot) {
                ChatListAdapter.this.notifyDataSetChanged();
            }
        });
        DatabaseReference child2 = FirebaseDatabase.getInstance().getReference().child("chat").child(this.chatList.get(chatListViewHolder.getAdapterPosition()).getChatId()).child("info");
        child2.keepSynced(true);
        child2.addValueEventListener(new ValueEventListener() {
            public void onCancelled(DatabaseError databaseError) {
            }

            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.child("TeamName").getValue() != null) {
                    chatListViewHolder.mName.setText(dataSnapshot.child("TeamName").getValue().toString());
                }
            }
        });
        DatabaseReference child3 = FirebaseDatabase.getInstance().getReference().child("chat").child(this.chatList.get(chatListViewHolder.getAdapterPosition()).getChatId());
        child3.keepSynced(true);
        child3.addValueEventListener(new ValueEventListener() {
            public void onCancelled(DatabaseError databaseError) {
            }

            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 1) {
                    chatListViewHolder.mLastMessage.setText("Tap here for first Chat");
                    chatListViewHolder.mTime.setText("For first time");
                }
            }
        });
        chatListViewHolder.mLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ChatActivity.class);
                intent.putExtra("chatObject", ChatListAdapter.this.chatList.get(chatListViewHolder.getAdapterPosition()));
                view.getContext().startActivity(intent);
            }
        });
        chatListViewHolder.mLayout.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatListAdapter.this.context);
                builder.setTitle("Delete Team");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int which) {
                        ChatListAdapter.this.deleteTeam(position);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
                return true;
            }
        });
    }


    public void deleteTeam(final int position) {
        FirebaseDatabase.getInstance().getReference().child("chat").child(this.chatList.get(position).getChatId()).addValueEventListener(new ValueEventListener() {
            public void onCancelled(DatabaseError databaseError) {
            }

            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    return;
                }
                if (dataSnapshot.child("info").child("TeamCreatorId").getValue().equals(FirebaseAuth.getInstance().getUid())) {
                    for (DataSnapshot key : dataSnapshot.child("info").child("users").getChildren()) {
                        FirebaseDatabase.getInstance().getReference().child("user").child(key.getKey()).child("chat").child(dataSnapshot.child("info").child("id").getValue().toString()).removeValue();
                    }
                    dataSnapshot.getRef().removeValue();

                    chatList.remove(position);
                     notifyDataSetChanged();
                    Toast.makeText(ChatListAdapter.this.context, "Team is deleted", 0).show();
                    return;
                }
                Toast.makeText(ChatListAdapter.this.context, "This team will NOT be deleted as you are not creator", 0).show();
            }
        });
    }

    public int getItemCount() {
        return this.chatList.size();
    }

 /*   public Filter getFilter() {
        return this.myFilter;
    }*/

    public class ChatListViewHolder extends RecyclerView.ViewHolder {
        public TextView mLastMessage;
        public LinearLayout mLayout;
        public TextView mName;
        public TextView mTime;

        public ChatListViewHolder(View view) {
            super(view);
            this.mName = (TextView) view.findViewById(R.id.Name);
            this.mLastMessage = (TextView) view.findViewById(R.id.LastMessage);
            this.mTime = (TextView) view.findViewById(R.id.time);
            this.mLayout = (LinearLayout) view.findViewById(R.id.Layout);
        }
    }
}
