package com.example.startendo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Iterator;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> implements Filterable {
    ChatPageActivity chatPageActivity;
/*    Filter myFilter = new Filter() {
 *//* access modifiers changed from: protected *//*
        public Filter.FilterResults performFiltering(CharSequence charSequence) {
            if (UserListAdapter.this.userListFull.size() == 0) {
                UserListAdapter.this.userListFull = new ArrayList<>(UserListAdapter.this.userList);
            }
            ArrayList arrayList = new ArrayList();
            if (charSequence.toString().isEmpty()) {
                Iterator<UserObject> it = UserListAdapter.this.userListFull.iterator();
                while (it.hasNext()) {
                    UserObject next = it.next();
                    if (!next.getSelected().booleanValue()) {
                        arrayList.add(next);
                    }
                }
            } else {
                String trim = charSequence.toString().toLowerCase().trim();
                Iterator<UserObject> it2 = UserListAdapter.this.userListFull.iterator();
                while (it2.hasNext()) {
                    UserObject next2 = it2.next();
                    if (!next2.getSelected().booleanValue() && (next2.getName().toString().toLowerCase().contains(trim) || next2.getPhone().toString().toLowerCase().contains(trim))) {
                        arrayList.add(next2);
                    }
                }
            }
            Filter.FilterResults filterResults = new Filter.FilterResults();
            filterResults.values = arrayList;
            return filterResults;
        }

        *//* access modifiers changed from: protected *//*
        public void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
            UserListAdapter.this.userList.clear();
            UserListAdapter.this.userList.addAll((ArrayList) filterResults.values);
            UserListAdapter.this.notifyDataSetChanged();
        }
    };*/

    @Override
    public Filter getFilter() {
        return myFilter;
    }
    Filter myFilter= new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            if(userListFull.size()==0)  userListFull=new ArrayList<>(userList) ;

            ArrayList<UserObject> filteredList= new ArrayList<>();
            if (constraint.toString().isEmpty()){
                for (UserObject item:userListFull){
                    if(!item.getSelected()){
                        filteredList.add(item);
                    }

                }

            }
            else{
                String filterPattern= constraint.toString().toLowerCase().trim();
                for (UserObject item:userListFull){
                    if (!item.getSelected()&&item.getName().toString().toLowerCase().contains(filterPattern)||item.getPhone().toString().toLowerCase().contains(filterPattern))

                        filteredList.add(item);
                }
            }
            FilterResults results = new FilterResults();
            results.values=filteredList;

            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            userList.clear();
            userList.addAll((ArrayList<UserObject>)results.values);




            notifyDataSetChanged();

        }
    };



    int selected_position = 0;
    ArrayList<UserObject> userList;
    ArrayList<UserObject> userListFull;

    public UserListAdapter(ArrayList<UserObject> arrayList, ChatPageActivity chatPageActivity2) {
        this.userList = arrayList;
        this.chatPageActivity = chatPageActivity2;
        this.userListFull = new ArrayList<>(arrayList);
    }

    class UserListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView check;
        public LinearLayout mLayout;
        public TextView mName;
        public TextView mPhone;
        public LinearLayout mcheckLayout;

        public UserListViewHolder(View view, ChatPageActivity chatPageActivity) {
            super(view);
            this.mName = (TextView) view.findViewById(R.id.Name);
            this.mPhone = (TextView) view.findViewById(R.id.Phone);
            this.mLayout = (LinearLayout) view.findViewById(R.id.Layout);
            this.mcheckLayout = (LinearLayout) view.findViewById(R.id.mcheckLayout);
            this.mcheckLayout.setOnClickListener(this);
        }

        public void onClick(View view) {
            UserListAdapter.this.chatPageActivity.makeselection(view, getAdapterPosition());
        }
    }

    public UserListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user, (ViewGroup) null, false);
        inflate.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new UserListViewHolder(inflate, this.chatPageActivity);
    }

    public void onBindViewHolder(UserListViewHolder userListViewHolder, int i) {
        userListViewHolder.mName.setText(this.userList.get(i).getName());
        userListViewHolder.mPhone.setText(this.userList.get(i).getPhone());
    }

    public int getItemCount() {
        return this.userList.size();
    }

  /*  public Filter getFilter() {
        return this.myFilter;
    }*/
}
