package com.nc.uetmail.mail.adapters;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nc.uetmail.R;
import com.nc.uetmail.mail.database.models.UserModel;

import java.util.ArrayList;
import java.util.List;

public class UserMailRecyclerAdapter extends RecyclerView.Adapter<UserMailRecyclerAdapter.MessageHolder> {
    private List<UserModel> users = new ArrayList<>();
    private OnItemClickListener listener;

    class MessageHolder extends RecyclerView.ViewHolder {
        private TextView tvRowUserIconLetter;
        private TextView tvRowUserName;
        private TextView tvRowUserMail;

        public MessageHolder(@NonNull final View itemView) {
            super(itemView);
            tvRowUserIconLetter = itemView.findViewById(R.id.mail_row_user_icon_letter);
            tvRowUserName = itemView.findViewById(R.id.mail_row_user_name);
            tvRowUserMail = itemView.findViewById(R.id.mail_row_user_email);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION)
                        listener.onItemClick(users.get(position));
                }
            });
        }
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.mail_home_row_user, viewGroup, false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder noteHolder, int position) {
        UserModel userModel = users.get(position);
        userModel.nullToEmpty();
        noteHolder.tvRowUserIconLetter.setText(userModel.getFirstUserLetter());
        noteHolder.tvRowUserName.setText(userModel.user);
        noteHolder.tvRowUserMail.setText(userModel.email);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setUsers(final List<UserModel> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    public UserModel getUserAt(int position) {
        return users.get(position);
    }

    public interface OnItemClickListener {
        void onItemClick(UserModel model);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
