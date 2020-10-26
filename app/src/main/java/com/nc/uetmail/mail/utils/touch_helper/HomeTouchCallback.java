package com.nc.uetmail.mail.utils.touch_helper;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.nc.uetmail.R;
import com.nc.uetmail.mail.view.HomeActivity;
import com.nc.uetmail.main.utils.RecyclerViewSwipeDecorator;

public class HomeTouchCallback extends ItemTouchHelper.SimpleCallback {
    public static interface SwipeInterface {
        void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int swipeDir);
    }

    private HomeActivity activity;
    private SwipeInterface swipeInterface;
    private String leftLabel;
    private String rightLabel;

    public HomeTouchCallback(
        HomeActivity activity, SwipeInterface swipeInterface,
        String leftLabel, String rightLabel, int dragDirs, int swipeDirs
    ) {
        super(dragDirs, swipeDirs);
        this.activity = activity;
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
    }

    @Override
    public void onChildDraw(@NonNull final Canvas c, @NonNull final RecyclerView recyclerView
        , @NonNull final RecyclerView.ViewHolder viewHolder, final float dX, final float dY,
                            final int actionState, final boolean isCurrentlyActive) {
        new RecyclerViewSwipeDecorator
            .Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
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
