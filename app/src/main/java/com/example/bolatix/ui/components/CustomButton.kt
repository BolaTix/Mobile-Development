package com.example.bolatix.ui.components

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class CustomButton : AppCompatButton {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        val normalDrawable = GradientDrawable()
        normalDrawable.shape = GradientDrawable.RECTANGLE
        normalDrawable.cornerRadius = 35f
        normalDrawable.setColor(Color.WHITE)
        val rippleColor = ColorStateList.valueOf(Color.parseColor("#E0E0E0"))
        val rippleDrawable = RippleDrawable(rippleColor, normalDrawable, null)
        background = rippleDrawable

        setPadding(0,0,0,0)
    }
}
