package com.softanime.storeapp.presentation.adapter.home

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.softanime.storeapp.R
import com.softanime.storeapp.data.model.home.ResponseProducts.SubCategory.Products.Data
import com.softanime.storeapp.databinding.ItemProductsBinding
import com.softanime.storeapp.utils.BASE_URL_IMAGE
import com.softanime.storeapp.utils.base.BaseDiffUtils
import com.softanime.storeapp.utils.extensions.loadImage
import com.softanime.storeapp.utils.extensions.moneySeparating
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ProductsAdapter @Inject constructor(@ApplicationContext context: Context) :
    RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {
    // data
    private var products = emptyList<Data>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsAdapter.ViewHolder {
        val binding =
            ItemProductsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductsAdapter.ViewHolder, position: Int) =
        holder.bind(products[position])

    override fun getItemCount(): Int = products.size

    override fun getItemViewType(position: Int): Int = position

    override fun getItemId(position: Int) = position.toLong()

    inner class ViewHolder(private val binding: ItemProductsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Data) {
            binding.apply {
                // Title
                itemTitle.text = item.title
                // Image
                val imageUrl = "${BASE_URL_IMAGE}${item.image}"
                itemImg.loadImage(imageUrl)
                // Quantity
                itemProgress.progress = item.quantity.toString().toInt()
                // Discard
                if (item.discountedPrice!! > 0) {
                    itemDiscount.apply {
                        isVisible = true
                        text = item.discountedPrice.toString().toInt().moneySeparating()
                    }
                    itemPrice.apply {
                        text = item.discountedPrice.toString().toInt().moneySeparating()
                        paintFlags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        setTextColor(ContextCompat.getColor(context, R.color.salmon))
                    }
                    itemPriceDiscount.apply {
                        isVisible = true
                        text = item.finalPrice.toString().toInt().moneySeparating()
                    }


                } else {
                    itemDiscount.isVisible = false
                    itemPriceDiscount.isVisible = false
                    itemPrice.apply {
                        text = item.productPrice.toString().toInt().moneySeparating()
                        setTextColor(ContextCompat.getColor(context, R.color.darkTurquoise))
                    }
                }

                // Item click
                root.setOnClickListener {
                    onItemClickListener?.let {

                    }
                }
            }
        }
    }

    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }

    fun setData(data: List<Data>) {
        val adapterDiffUtils = BaseDiffUtils(products, data)
        val diffUtils = DiffUtil.calculateDiff(adapterDiffUtils)
        products = data
        diffUtils.dispatchUpdatesTo(this)
    }
}