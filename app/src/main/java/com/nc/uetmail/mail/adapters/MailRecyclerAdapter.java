package com.nc.uetmail.mail.adapters;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nc.uetmail.R;
import com.nc.uetmail.mail.database.models.MailModel;
import com.nc.uetmail.mail.session.components.MailMessage;
import com.nc.uetmail.mail.session.components.MailUtils;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Flags;

public class MailRecyclerAdapter extends RecyclerView.Adapter<MailRecyclerAdapter.MailHolder> {
    private List<MailModel> messages = new ArrayList<>();
    private OnItemClickListener listener;

    class MailHolder extends RecyclerView.ViewHolder {
        private TextView tvRowIconLetter;
        private TextView tvRowTitle;
        private TextView tvRowUser;
        private TextView tvRowBodyTxt;
        private TextView tvRowDate;
        private ImageView tvRowHasAttach;
        private ImageView tvRowSeen;

        public MailHolder(@NonNull final View itemView) {
            super(itemView);
            tvRowIconLetter = itemView.findViewById(R.id.mail_row_icon_letter);
            tvRowTitle = itemView.findViewById(R.id.mail_row_title);
            tvRowUser = itemView.findViewById(R.id.mail_row_user);
            tvRowBodyTxt = itemView.findViewById(R.id.mail_row_body_txt);
            tvRowDate = itemView.findViewById(R.id.mail_row_date);
            tvRowHasAttach = itemView.findViewById(R.id.mail_row_has_attach);
            tvRowSeen = itemView.findViewById(R.id.mail_row_seen);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION)
                        listener.onItemClick(messages.get(position), position);
                }
            });
        }
    }

    @NonNull
    @Override
    public MailHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.mail_home_row_mail, viewGroup, false);
        return new MailHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MailHolder noteHolder, int position) {
        MailModel message = messages.get(position);
        message.nullToEmpty();
        boolean seen = new Flags(
            MailUtils.callPrivateConstructor(Flags.Flag.class, 0, message.mail_flags_code)
        ).contains(Flags.Flag.SEEN);
        noteHolder.tvRowIconLetter.setText(message.getFirstUserLetter());
        noteHolder.tvRowUser.setText(MailMessage.toPersonalString(message.mail_from));
        noteHolder.tvRowTitle.setText(message.getShortSubject());

        noteHolder.tvRowUser.setTypeface(null, seen ? Typeface.NORMAL : Typeface.BOLD_ITALIC);
        noteHolder.tvRowTitle.setTypeface(null, seen ? Typeface.NORMAL : Typeface.BOLD_ITALIC);
        noteHolder.itemView.setBackgroundResource(
            seen ? R.color.colorWhite : R.color.colorDisabled
        );
        noteHolder.tvRowBodyTxt.setText(message.getShortBodyTxt());
        noteHolder.tvRowDate.setText(message.getFormatSentDate());
        noteHolder.tvRowHasAttach.setVisibility(
            message.mail_has_attachment ? View.VISIBLE : View.GONE
        );
        noteHolder.tvRowSeen.setVisibility(seen ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public List<MailModel> getMessages() {
        return messages;
    }

    public void setMessages(final List<MailModel> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    public MailModel getMessageAt(int position) {
        return messages.get(position);
    }

    public interface OnItemClickListener {
        void onItemClick(MailModel model, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
