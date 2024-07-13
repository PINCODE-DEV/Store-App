package com.softanime.storeapp.utils.extensions

import android.content.Context
import android.hardware.input.InputManager
import android.media.Image
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import coil.load
import coil.request.CachePolicy
import com.google.android.material.snackbar.Snackbar

fun View.hideKeyboard(){
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken,0)
}

fun View.showSnackBar(message: String){
    Snackbar.make(this,message,Snackbar.LENGTH_SHORT).show()
}

fun ImageView.loadImage(url : String){
    this.load(url){
        crossfade(true)
        crossfade(500)
        diskCachePolicy(CachePolicy.ENABLED)
    }
}