package com.softanime.storeapp.utils.extensions

import android.app.Dialog
import android.content.Context
import android.hardware.input.InputManager
import android.media.Image
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import coil.load
import coil.request.CachePolicy
import com.google.android.material.snackbar.Snackbar
import com.softanime.storeapp.R
import java.text.DecimalFormat

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
        placeholder(R.drawable.placeholder)
    }
}

fun View.shownLoading(isShow: Boolean, container: View){
    if (isShow) {
        this.isVisible = true
        container.isVisible = false
    }else{
        this.isVisible = false
        container.isVisible = true
    }
}

fun RecyclerView.setup(lm : LayoutManager, myAdapter: RecyclerView.Adapter<*>){
    this.apply {
        layoutManager = lm
        adapter = myAdapter
        setHasFixedSize(true)
    }
}

fun Int.moneySeparating(): String {
    return "${DecimalFormat("#,###.##").format(this)} تومان"
}

fun Dialog.transparentCorners(){
    this.window!!.setBackgroundDrawableResource(android.R.color.transparent)
}