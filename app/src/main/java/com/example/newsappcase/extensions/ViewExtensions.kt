package com.example.newsappcase.extensions

import android.view.View
import com.example.newsappcase.util.SingleClickListener

fun View.setSingleOnClickListener(listener: View.OnClickListener) {
    val singleClickListener = SingleClickListener { listener.onClick(it) }
    setOnClickListener(singleClickListener)
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}