package com.joinflatshare.customviews.dropdown;

import static android.widget.PopupWindow.INPUT_METHOD_NEEDED;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.interfaces.OnitemClick;

import java.util.ArrayList;

public class ShowDropDown {
    private Activity activity;
    private PopupWindow popup;
    private View contentLayout;

    public ShowDropDown(Activity activity) {
        this.activity = activity;
        init();
    }

    private void init() {
        LayoutInflater layoutInflater = (LayoutInflater) (activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        contentLayout = layoutInflater.inflate(R.layout.dialog_popup, null);
        popup = new PopupWindow(contentLayout);
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setInputMethodMode(INPUT_METHOD_NEEDED);
        popup.setFocusable(false);
        popup.setOutsideTouchable(true);
    }

    public void show(View anchor, ArrayList<String> items, OnitemClick onUiEventListener) {
        popup.setWidth(anchor.getWidth());
        popup.showAsDropDown(anchor, Gravity.NO_GRAVITY, 0, 0);
        if (anchor instanceof EditText) {
            EditText et = (EditText) anchor;
            if (et.getText().toString().equals("")) {
                popup.dismiss();
                return;
            }
        }
        setView(items, onUiEventListener);
    }

    public PopupWindow getPopup() {
        return popup;
    }

    private void setView(ArrayList<String> items, OnitemClick onUiEventListener) {
        RecyclerView rv = contentLayout.findViewById(R.id.rv_popup);
        rv.setLayoutManager(new LinearLayoutManager(activity));
        rv.setAdapter(new DropDownAdapter(activity, items, (view, position) -> {
            if (onUiEventListener != null)
                onUiEventListener.onitemclick(view, position);
            popup.dismiss();
        }));
    }
}
