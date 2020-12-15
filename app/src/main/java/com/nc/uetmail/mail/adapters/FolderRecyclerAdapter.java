package com.nc.uetmail.mail.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nc.uetmail.R;
import com.nc.uetmail.mail.database.models.FolderModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FolderRecyclerAdapter extends RecyclerView.Adapter<FolderRecyclerAdapter.FolderHolder> {
    private List<FolderModel> models = new ArrayList<>();
    private OnItemClickListener listener;

    class FolderHolder extends RecyclerView.ViewHolder {
        private TextView tvFolderName;
        private TextView tvFolderCount;

        public FolderHolder(@NonNull final View itemView) {
            super(itemView);
            tvFolderName = itemView.findViewById(R.id.mail_row_folder_name);
            tvFolderCount = itemView.findViewById(R.id.mail_row_folder_count);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION)
                        listener.onItemClick(models.get(position));
                }
            });
        }
    }

    @NonNull
    @Override
    public FolderHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.mail_home_row_folder, viewGroup, false);
        return new FolderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderHolder noteHolder, int position) {
        FolderModel folder = models.get(position);
        folder.nullToEmpty();
        String fullName = folder.fullName.replaceAll("\\.", "/");
        noteHolder.tvFolderName.setText(fullName);
        noteHolder.tvFolderCount.setText(
            folder.unread_count + "/" + folder.message_count +
                " (" +
                noteHolder.itemView.getContext().getString(R.string.mail_unread_count) +
                ")"
        );
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public void setModels(final List<FolderModel> models) {
        this.models = models;
        notifyDataSetChanged();
    }

    public FolderModel getFolderModel(int position) {
        return models.get(position);
    }

    public interface OnItemClickListener {
        void onItemClick(FolderModel model);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
