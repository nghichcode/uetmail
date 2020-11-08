package com.nc.uetmail.mail.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nc.uetmail.R;
import com.nc.uetmail.mail.database.models.AttachmentModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AttachRecyclerAdapter extends RecyclerView.Adapter<AttachRecyclerAdapter.MessageHolder> {
    private List<AttachmentModel> attachments = new ArrayList<>();
    private OnItemClickListener listener;

    class MessageHolder extends RecyclerView.ViewHolder {
        private TextView tvAttachName;
        private TextView tvAttachSize;
        private RelativeLayout tvAttachDownload;

        public MessageHolder(@NonNull final View itemView) {
            super(itemView);
            tvAttachName = itemView.findViewById(R.id.mail_message_attach_name);
            tvAttachSize = itemView.findViewById(R.id.mail_message_attach_size);
            tvAttachDownload = itemView.findViewById(R.id.mail_message_attach_download);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION)
                        listener.onItemClick(attachments.get(position));
                }
            });

            tvAttachDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION)
                        listener.onItemClick(attachments.get(position));
                }
            });
        }
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.mail_home_row_message, viewGroup, false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder noteHolder, int position) {
        AttachmentModel attachment = attachments.get(position);
        attachment.nullToEmpty();
        noteHolder.tvAttachName.setText(attachment.name);
        noteHolder.tvAttachSize.setText(attachment.getSizeKb());
    }

    @Override
    public int getItemCount() {
        return attachments.size();
    }

    public void setAttachments(final List<AttachmentModel> attachments) {
        this.attachments = attachments;
        notifyDataSetChanged();
    }

    public AttachmentModel getAttachmentModel(int position) {
        return attachments.get(position);
    }

    public interface OnItemClickListener {
        void onItemClick(AttachmentModel model);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
