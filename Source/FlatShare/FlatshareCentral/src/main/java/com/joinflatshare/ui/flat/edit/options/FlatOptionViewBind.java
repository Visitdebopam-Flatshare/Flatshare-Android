package com.joinflatshare.ui.flat.edit.options;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.ui.flat.edit.FlatEditActivity;

public class FlatOptionViewBind {
    protected FlatEditActivity activity;
    // General
    protected ImageView img_amenitites_cross;
    public TextView txt_option_header;
    private View view_bg;

    public FlatOptionViewBind(FlatEditActivity activity) {
        this.activity = activity;
        bind();
    }

    private void bind() {
        view_bg=activity.findViewById(R.id.view_bg);
        img_amenitites_cross = activity.findViewById(R.id.img_amenitites_cross);
        txt_option_header = activity.findViewById(R.id.txt_option_header);
        view_bg.setEnabled(false);
        view_bg.setOnClickListener(view -> {

        });
    }
}
