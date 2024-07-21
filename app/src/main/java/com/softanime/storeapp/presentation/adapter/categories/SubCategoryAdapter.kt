package com.softanime.storeapp.presentation.adapter.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.softanime.storeapp.data.model.categories.ResponseCategories.ResponseCategoriesItem.SubCategory
import com.softanime.storeapp.databinding.ItemCategoriesSubBinding
import com.softanime.storeapp.utils.base.BaseDiffUtils
import javax.inject.Inject

class SubCategoryAdapter @Inject constructor() : RecyclerView.Adapter<SubCategoryAdapter.ViewHolder>() {

    private var items = emptyList<SubCategory>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoriesSubBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = position

    override fun getItemId(position: Int) = position.toLong()

    inner class ViewHolder(private val binding: ItemCategoriesSubBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SubCategory) {
            binding.apply {
                itemTitle.text = item.title
                //Click
                root.setOnClickListener {
                    sendSlug?.let { it(item.slug.toString()) }
                }
            }
        }
    }

    private var sendSlug: ((String) -> Unit)? = null

    fun getSlug(listener: (String) -> Unit) {
        sendSlug = listener
    }

    fun setData(data: List<SubCategory>) {
        val adapterDiffUtils = BaseDiffUtils(items, data)
        val diffUtils = DiffUtil.calculateDiff(adapterDiffUtils)
        items = data
        diffUtils.dispatchUpdatesTo(this)
    }
}