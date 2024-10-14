package com.example.newsappcase.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.newsappcase.R

fun ImageView.displayImage(imagePath: String?) {
    Glide.with(this).load(imagePath).placeholder(R.drawable.ic_error).into(this)
}