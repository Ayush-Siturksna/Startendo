package com.example.startendo.Chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;

import com.example.startendo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stfalcon.frescoimageviewer.ImageViewer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> implements Filterable {
    String chatid;
    Context context;
    ArrayList<MessageObject> messageList;
    ArrayList<MessageObject> messageListFull;
    Filter myFilter = new Filter() {
        /* access modifiers changed from: protected */
        public Filter.FilterResults performFiltering(CharSequence charSequence) {
            if (MessageAdapter.this.messageListFull.size() == 0) {
                MessageAdapter.this.messageListFull = new ArrayList<>(MessageAdapter.this.messageList);
            }
            ArrayList arrayList = new ArrayList();
            if (charSequence.toString().isEmpty()) {
                arrayList.addAll(MessageAdapter.this.messageListFull);
            } else {
                charSequence.toString().toLowerCase().trim();
                Iterator<MessageObject> it = MessageAdapter.this.messageListFull.iterator();
                while (it.hasNext()) {
                    arrayList.add(it.next());
                }
            }
            Filter.FilterResults filterResults = new Filter.FilterResults();
            filterResults.values = arrayList;
            return filterResults;
        }

        /* access modifiers changed from: protected */
        public void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
            MessageAdapter.this.messageList.clear();
            MessageAdapter.this.messageList.addAll((ArrayList) filterResults.values);
            MessageAdapter.this.notifyDataSetChanged();
        }
    };

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mLayout;
        TextView mMessage;
        TextView mSender;
        TextView mTime;
        Button mViewMedia = ((Button) this.itemView.findViewById(R.id.viewMediaBtn));

        public MessageViewHolder(View view) {
            super(view);
            this.mMessage = (TextView) view.findViewById(R.id.messagemine);
            this.mSender = (TextView) view.findViewById(R.id.senderNumber);
            this.mTime = (TextView) view.findViewById(R.id.timemessage);
            this.mLayout = (LinearLayout) view.findViewById(R.id.msglayout);
        }
    }

    public MessageAdapter(ArrayList<MessageObject> arrayList, Context context2, String str) {
        this.messageList = arrayList;
        this.messageListFull = new ArrayList<>(arrayList);
        this.context = context2;
        this.chatid = str;
    }

    public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message, (ViewGroup) null, false);
        inflate.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new MessageViewHolder(inflate);
    }

    public void onBindViewHolder(final MessageViewHolder messageViewHolder, final int position) {
        FirebaseDatabase.getInstance().getReference().child("chat").child(this.chatid).child("messages").addChildEventListener(new ChildEventListener() {
            public void onCancelled(DatabaseError databaseError) {
            }

            public void onChildAdded(DataSnapshot dataSnapshot, String str) {
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String str) {
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String str) {
            }

            public void onChildRemoved(DataSnapshot dataSnapshot) {
                MessageAdapter.this.notifyDataSetChanged();
            }
        });
        messageViewHolder.mMessage.setText(this.messageList.get(position).getMessage());
        messageViewHolder.mSender.setText(this.messageList.get(position).getSenderphone());
        messageViewHolder.mTime.setText(new SimpleDateFormat("hh:mm a").format(new Date(this.messageList.get(position).getTimestamp())));
        if (this.messageList.get(messageViewHolder.getAdapterPosition()).getMediaUrlList().isEmpty()) {
            messageViewHolder.mViewMedia.setVisibility(View.GONE);
        }
        messageViewHolder.mViewMedia.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new ImageViewer.Builder(view.getContext(), MessageAdapter.this.messageList.get(messageViewHolder.getAdapterPosition()).getMediaUrlList()).setStartPosition(0).show();
            }
        });
        messageViewHolder.mMessage.setLongClickable(true);
        messageViewHolder.mMessage.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MessageAdapter.this.context);
                builder.setTitle("Delete Message");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int which) {
                        MessageAdapter.this.deleteMessage(position);
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

    /* access modifiers changed from: private */
    public void deleteMessage(final int i) {
        FirebaseDatabase.getInstance().getReference().child("chat").child(this.chatid).child("messages").child(this.messageList.get(i).getMessageId()).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onCancelled(DatabaseError databaseError) {
            }

            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    return;
                }
                if (dataSnapshot.child("creator").getValue().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    dataSnapshot.getRef().removeValue();
                    MessageAdapter.this.messageList.remove(i);
                    MessageAdapter.this.notifyDataSetChanged();
                    return;
                }
                Toast.makeText(MessageAdapter.this.context, "This message cannot be deleted", 0).show();
            }
        });
    }

    public int getItemCount() {
        return this.messageList.size();
    }

    public Filter getFilter() {
        return this.myFilter;
    }
}
