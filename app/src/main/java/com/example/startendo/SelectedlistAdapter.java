package com.example.startendo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class SelectedlistAdapter extends RecyclerView.Adapter<SelectedlistAdapter.SelectedlistViewHolder> {
    ArrayList<UserObject> Selectedlist;
    ChatPageActivity chatPageActivity;

    class SelectedlistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public LinearLayout mLayout;
        public TextView mPhone;

        public SelectedlistViewHolder(View view, ChatPageActivity chatPageActivity) {
            super(view);
            this.mPhone = (TextView) view.findViewById(R.id.SelectedPhoneNumber);
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.Layoutgg);
            this.mLayout = linearLayout;
            linearLayout.setOnClickListener(this);
        }

        public void onClick(View view) {
            SelectedlistAdapter.this.chatPageActivity.deleteselecteditem(view, getAdapterPosition());
        }
    }

    public SelectedlistAdapter(ArrayList<UserObject> arrayList, ChatPageActivity chatPageActivity2) {
        this.Selectedlist = arrayList;
        this.chatPageActivity = chatPageActivity2;
    }

    public SelectedlistViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_userselected, (ViewGroup) null, false);
        inflate.setLayoutParams(new RecyclerView.LayoutParams(-2, -2));
        return new SelectedlistViewHolder(inflate, this.chatPageActivity);
    }

    public void onBindViewHolder(SelectedlistViewHolder selectedlistViewHolder, int i) {
        selectedlistViewHolder.mPhone.setText(this.Selectedlist.get(i).getPhone());
    }

    public int getItemCount() {
        return this.Selectedlist.size();
    }
}
