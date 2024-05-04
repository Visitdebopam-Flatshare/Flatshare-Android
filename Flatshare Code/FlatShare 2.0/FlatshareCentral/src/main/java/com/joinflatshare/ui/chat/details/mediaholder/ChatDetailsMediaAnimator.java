package com.joinflatshare.ui.chat.details.mediaholder;

import android.animation.Animator;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.joinflatshare.FlatshareCentral.R;
import com.joinflatshare.utils.helper.CommonMethods;
import com.joinflatshare.utils.helper.CommonMethod;

public class ChatDetailsMediaAnimator {
    private ChatDetailsMediaBottomSheet mediaBottomSheet;

    public ChatDetailsMediaAnimator(ChatDetailsMediaBottomSheet mediaBottomSheet) {
        this.mediaBottomSheet = mediaBottomSheet;
    }

    protected void show() {
        View view = mediaBottomSheet.getActivity().findViewById(R.id.drawer_layout);
        float dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, view.getContext().getResources().getDisplayMetrics());
        int x = (int) (CommonMethods.getScreenWidth() - (dp * (18 + 40 + 4 + 56)));
        int y = (int) (dp * 220);

        int startRadius = 0;
        int endRadius = (int) Math.hypot(CommonMethods.getScreenWidth() - (dp * (8 + 8)), (dp * 220));

        Animator anim = ViewAnimationUtils.createCircularReveal(mediaBottomSheet.getActivity().viewBind.includeChatMedia.bottomSheetMedia, x, y, startRadius, endRadius);
        anim.setDuration(500);
        mediaBottomSheet.getActivity().viewBind.includeChatMedia.bottomSheetMedia.setVisibility(View.VISIBLE);
        anim.start();

    }

    protected void hide() {
        View view = mediaBottomSheet.getActivity().findViewById(R.id.drawer_layout);
        float dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, view.getContext().getResources().getDisplayMetrics());
        int x = (int) (CommonMethods.getScreenWidth() - (dp * (18 + 40 + 4 + 56)));
        int y = (int) (dp * 220);

        int startRadius = 0;
        int endRadius = (int) Math.hypot(CommonMethods.getScreenWidth() - (dp * (8 + 8)), (dp * 220));

        Animator anim = ViewAnimationUtils.createCircularReveal(mediaBottomSheet.getActivity().viewBind.includeChatMedia.bottomSheetMedia, x, y, endRadius, startRadius);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mediaBottomSheet.getActivity().viewBind.includeChatMedia.bottomSheetMedia.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        anim.start();
    }
}
