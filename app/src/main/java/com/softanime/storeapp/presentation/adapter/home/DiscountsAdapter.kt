package com.softanime.storeapp.presentation.adapter.home

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.softanime.storeapp.R
import com.softanime.storeapp.data.model.home.ResponseDiscount.ResponseDiscountItem
import com.softanime.storeapp.databinding.ItemDiscountBinding
import com.softanime.storeapp.utils.BASE_URL_IMAGE
import com.softanime.storeapp.utils.base.BaseDiffUtils
import com.softanime.storeapp.utils.extensions.loadImage
import com.softanime.storeapp.utils.extensions.moneySeparating
import javax.inject.Inject

class DiscountsAdapter @Inject constructor() : RecyclerView.Adapter<DiscountsAdapter.ViewHolder>() {
    // data
    private var discounts = emptyList<ResponseDiscountItem>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscountsAdapter.ViewHolder {
        val binding = ItemDiscountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiscountsAdapter.ViewHolder, position: Int) =
        holder.bind(discounts[position])

    override fun getItemCount(): Int = discounts.size

    override fun getItemViewType(position: Int): Int = position

    override fun getItemId(position: Int) = position.toLong()

    inner class ViewHolder(private val binding: ItemDiscountBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResponseDiscountItem) {
            binding.apply {
                itemTitle.text = item.title
                val imageUrl = "${BASE_URL_IMAGE}${item.image}"
                itemImg.loadImage(imageUrl)

                //Quantity
                itemProgress.progress = item.quantity.toString().toInt()

                //Final price
                itemPriceDiscount.text = item.finalPrice.toString().toInt().moneySeparating()

                //Discount
                itemPrice.apply {
                    text = item.discountedPrice.toString().toInt().moneySeparating()
                    paintFlags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    setTextColor(ContextCompat.getColor(context, R.color.salmon))
                }

                // Item click
                root.setOnClickListener {
                    onItemClickListener?.let {

                    }
                }
            }
        }
    }

    private var onItemClickListener: ((String, String) -> Unit)? = null

    fun setOnItemClickListener(listener: (String, String) -> Unit) {
        onItemClickListener = listener
    }

    fun setData(data: List<ResponseDiscountItem>) {
        val adapterDiffUtils = BaseDiffUtils(discounts, data)
        val diffUtils = DiffUtil.calculateDiff(adapterDiffUtils)
        discounts = data
        diffUtils.dispatchUpdatesTo(this)
    }
}