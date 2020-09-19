package com.nc.uetmail.mail.adapters;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nc.uetmail.R;
import com.nc.uetmail.mail.database.models.MessageModel;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {
    private List<MessageModel> messages = new ArrayList<>();
    private OnItemClickListener listener;

    class MessageHolder extends RecyclerView.ViewHolder {
        private TextView tvSubject;
        private TextView tvContentTxt;
        private TextView tvSentDate;

        public MessageHolder(@NonNull final View itemView) {
            super(itemView);
            tvSubject = itemView.findViewById(R.id.tv_title);
            tvContentTxt = itemView.findViewById(R.id.tv_description);
            tvSentDate = itemView.findViewById(R.id.tv_priority);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int pos = getAdapterPosition();
//                    if (listener!=null && pos!=RecyclerView.NO_POSITION){
//                        listener.onItemClick(messages.get(pos));
//                    }
//                }
//            });
        }
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.note_item, viewGroup, false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder noteHolder, int i) {
        MessageModel currentMessage = messages.get(i);
        noteHolder.tvSubject.setText(currentMessage.getM_subject());
        noteHolder.tvContentTxt.setText(currentMessage.getM_content_txt());
        noteHolder.tvSentDate.setText(String.valueOf(currentMessage.getM_sent_date()));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setMessages(final List<MessageModel> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    public MessageModel getMessageAt(int position){
        return messages.get(position);
    }

    public interface OnItemClickListener {
        void onItemClick(MessageModel note);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
