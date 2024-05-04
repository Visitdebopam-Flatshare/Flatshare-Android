package com.joinflatshare.ui.dialogs

import android.view.View
import android.widget.LinearLayout
import com.google.android.material.card.MaterialCardView
import com.joinflatshare.FlatshareCentral.databinding.DialogSmokingBinding
import com.joinflatshare.interfaces.OnStringFetched
import com.joinflatshare.ui.base.BaseActivity

class DialogSmoking(
    activity: BaseActivity,
    card: MaterialCardView,
    linearView: LinearLayout,
    private val callback: OnStringFetched
) {
    var viewBind: DialogSmokingBinding

    init {
        linearView.visibility=View.GONE
        viewBind = DialogSmokingBinding.inflate(activity.layoutInflater)
        card.addView(viewBind.root)

        // content
        viewBind.txtSmoking.text = activity.getEmojiByUnicode(0x1F6AB)
        viewBind.txtVeg.text = activity.getEmojiByUnicode(0x1F44D)
        viewBind.txtParty.text = activity.getEmojiByUnicode(0x1F60D)

        viewBind.txtSmoking.setOnClickListener {
            callback.onFetched("1")
            card.removeView(viewBind.root)
            linearView.visibility=View.VISIBLE
        }
        viewBind.txtVeg.setOnClickListener {
            callback.onFetched("2")
            card.removeView(viewBind.root)
            linearView.visibility=View.VISIBLE
        }
        viewBind.txtParty.setOnClickListener {
            callback.onFetched("3")
            card.removeView(viewBind.root)
            linearView.visibility=View.VISIBLE
        }
    }
}