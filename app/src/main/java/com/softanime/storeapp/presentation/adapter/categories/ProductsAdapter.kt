package com.softanime.storeapp.presentation.adapter.categories

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.softanime.storeapp.R
import com.softanime.storeapp.data.model.home.ResponseProducts.SubCategory.Products.Data
import com.softanime.storeapp.data.model.search.ResponseSearch
import com.softanime.storeapp.databinding.ItemSearchListBinding
import com.softanime.storeapp.presentation.adapter.search.ColorsAdapter
import com.softanime.storeapp.utils.BASE_URL_IMAGE
import com.softanime.storeapp.utils.base.BaseDiffUtils
import com.softanime.storeapp.utils.extensions.loadImage
import com.softanime.storeapp.utils.extensions.moneySeparating
import com.softanime.storeapp.utils.extensions.setup
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ProductsAdapter @Inject constructor(@ApplicationContext private val context: Context) :
    RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    private var items = emptyList<Data>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsAdapter.ViewHolder {
        val binding = ItemSearchListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductsAdapter.ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = position

    override fun getItemId(position: Int) = position.toLong()

    inner class ViewHolder(private val binding: ItemSearchListBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Data) {
            binding.apply {
                itemTitle.text = item.title
                //Image
                val imageUrl = "$BASE_URL_IMAGE${item.image}"
                itemImg.loadImage(imageUrl)
                //Quantity
                itemQuantity.text = "${context.getString(R.string.quantity)} ${item.quantity} " +
                        context.getString(R.string.item)
                //Discount
                if (item.discountedPrice!! > 0) {
                    itemDiscount.apply {
                        isVisible = true
                        text = item.discountedPrice.toString().toInt().moneySeparating()
                    }
                    itemPrice.apply {
                        text = item.productPrice.toString().toInt().moneySeparating()
                        paintFlags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        setTextColor(ContextCompat.getColor(context, R.color.salmon))
                    }
                    itemPriceDiscount.apply {
                        isVisible = true
                        text = item.finalPrice.toString().toInt().moneySeparating()
                    }
                } else {
                    itemDiscount.isVisible = false
                    //itemPriceDiscount.isVisible = false
                    itemPriceDiscount.apply {
                        text = item.productPrice.toString().toInt().moneySeparating()
                        setTextColor(ContextCompat.getColor(context, R.color.darkTurquoise))
                    }
                }
                //Colors
                item.colors?.let { colors ->
                    colorsList(colors, binding)
                }
                //Click
                root.setOnClickListener { }
            }
        }
    }

    private fun colorsList(list: List<ResponseSearch.Products.Data.Color>, binding: ItemSearchListBinding) {
        val colorsAdapter = ColorsAdapter()
        colorsAdapter.setData(list)
        binding.itemsColors.setup(
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true), colorsAdapter
        )
    }

    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }

    fun setData(data: List<Data>) {
        val adapterDiffUtils = BaseDiffUtils(items, data)
        val diffUtils = DiffUtil.calculateDiff(adapterDiffUtils)
        items = data
        diffUtils.dispatchUpdatesTo(this)
    }
}