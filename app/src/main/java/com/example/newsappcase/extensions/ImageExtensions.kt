package com.example.newsappcase.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.displayImage(imagePath: String?) {
    Glide.with(this).load(imagePath).into(this)
}