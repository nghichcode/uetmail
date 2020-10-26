package com.nc.uetmail.mail.adapters;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nc.uetmail.R;
import com.nc.uetmail.mail.database.models.MailModel;

import java.util.ArrayList;
import java.util.List;

public class MessageRecyclerAdapter extends RecyclerView.Adapter<MessageRecyclerAdapter.MessageHolder> {
    private List<MailModel> messages = new ArrayList<>();
    private OnItemClickListener listener;

    class MessageHolder extends RecyclerView.ViewHolder {
        private TextView tvRowIconLetter;
        private TextView tvRowTitle;
        private TextView tvRowUser;
        private TextView tvRowBodyTxt;
        private TextView tvRowDate;

        public MessageHolder(@NonNull final View itemView) {
            super(itemView);
            tvRowIconLetter = itemView.findViewById(R.id.mail_row_icon_letter);
            tvRowTitle = itemView.findViewById(R.id.mail_row_title);
            tvRowUser = itemView.findViewById(R.id.mail_row_user);
            tvRowBodyTxt = itemView.findViewById(R.id.mail_row_body_txt);
            tvRowDate = itemView.findViewById(R.id.mail_row_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION)
                        listener.onItemClick(messages.get(position));
                }
            });
        }
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.mail_home_row_mail, viewGroup, false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder noteHolder, int position) {
        MailModel message = messages.get(position);
        message.nullToEmpty();
        noteHolder.tvRowIconLetter.setText(message.getFirstUserLetter());
        noteHolder.tvRowTitle.setText(message.getShortSubject());
        noteHolder.tvRowUser.setText(message.mail_from);
        noteHolder.tvRowBodyTxt.setText(message.getShortBodyTxt());
        noteHolder.tvRowDate.setText(message.getFormatDate());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setMessages(final List<MailModel> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    public MailModel getMessageAt(int position) {
        return messages.get(position);
    }

    public interface OnItemClickListener {
        void onItemClick(MailModel model);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
