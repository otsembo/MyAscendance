package com.myscendance.app.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.LinearLayout
import com.myscendance.app.R

class AnimatorDialog(mCtx:Context) : Dialog(mCtx) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_animator)

            window?.let {
                it.setLayout(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )

                it.setBackgroundDrawableResource(R.color.white_transparent)
            }
    }

}