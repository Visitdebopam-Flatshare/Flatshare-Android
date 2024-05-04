package com.joinflatshare.customviews;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.interfaces.OnitemClick;

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {
    private Activity activity;
    private ColorDrawable background;
    private Drawable icon;
    private OnitemClick onitemClick;

    public RecyclerItemTouchHelper(Activity activity, OnitemClick onitemClick) {
        super(0, ItemTouchHelper.START);
        this.activity = activity;
        this.onitemClick = onitemClick;
        prepareView();
    }

    private void prepareView() {
        icon = ContextCompat.getDrawable(activity,
                R.drawable.ic_delete_round);
        background = new ColorDrawable(ContextCompat.getColor(activity, R.color.red));
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        onitemClick.onitemclick(viewHolder.itemView, position);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder, float dX,
                            float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;
        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + iconMargin;
        int iconBottom = iconTop + icon.getIntrinsicHeight();
        if (dX < 0) {
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
            background.draw(c);
            icon.draw(c);
        } else {
            icon.setBounds(0, 0, 0, 0);
            background.setBounds(0, 0, 0, 0);
        }
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        /*if (activity instanceof NotificationActivity) {
            NotificationActivity act = (NotificationActivity) activity;
            NotificationItem notification = act.notifications.get(position);
            if (!(notification.getType().equals(NOTIFICATION_TYPE_INVITE_FRIEND)
                    || notification.getType().equals(NOTIFICATION_TYPE_INVITE_FLAT_MEMBER))) {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            }
        }*/
        final int dragFlags = 0;
        final int swipeFlags = ItemTouchHelper.START;
        return makeMovementFlags(dragFlags, swipeFlags);
    }
}