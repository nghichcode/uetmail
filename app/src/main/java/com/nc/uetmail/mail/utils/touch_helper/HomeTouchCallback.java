package com.nc.uetmail.mail.utils.touch_helper;

import android.graphics.Canvas;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.nc.uetmail.R;
import com.nc.uetmail.mail.view.HomeActivity;
import com.nc.uetmail.mail.utils.RecyclerViewSwipeDecorator.Builder;

public class HomeTouchCallback extends ItemTouchHelper.SimpleCallback {
    public static interface SwipeInterface {
        void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int swipeDir);
    }

    private HomeActivity activity;
//    private Adapter adapter;
    private SwipeInterface swipeInterface;
    private String leftLabel;
    private String rightLabel;

    public HomeTouchCallback(
        HomeActivity activity, SwipeInterface swipeInterface,
        String leftLabel, String rightLabel, int dragDirs, int swipeDirs
    ) {
        super(dragDirs, swipeDirs);
        this.activity = activity;
//        this.adapter = adapter;
        this.swipeInterface = swipeInterface;
        this.leftLabel = leftLabel;
        this.rightLabel = rightLabel;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull ViewHolder viewHolder,
                          @NonNull ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int swipeDir) {
        swipeInterface.onSwiped(viewHolder, swipeDir);
//        adapter.notifyItemChanged(viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(@NonNull final Canvas c, @NonNull final RecyclerView recyclerView
        , @NonNull final RecyclerView.ViewHolder viewHolder, final float dX, final float dY,
                            final int actionState, final boolean isCurrentlyActive) {
//        float tmp_dX = dX;
//        boolean swipeRight = RecyclerViewSwipeDecorator.isSwipe(ItemTouchHelper.RIGHT, dX);
//        int right = viewHolder.itemView.getRight();
//        if (
//            ( swipeRight && Math.ceil(dX) >= right) || (!swipeRight && Math.ceil(dX) <= -right)
//        ) tmp_dX = (float) 0.0;

        new Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            .addSwipeLeftBackgroundColor(ContextCompat.getColor(activity, R.color.colorDanger))
            .addSwipeLeftLabel(leftLabel)
            .setSwipeLeftLabelColor(ContextCompat.getColor(activity, R.color.colorWhite))
            .addSwipeRightBackgroundColor(ContextCompat.getColor(activity, R.color.colorSuccess))
            .addSwipeRightLabel(rightLabel)
            .setSwipeRightLabelColor(ContextCompat.getColor(activity, R.color.colorWhite))
            .create()
            .decorate();
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

}
