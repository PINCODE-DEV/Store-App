package com.softanime.storeapp.presentation.ui.search.search_filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.softanime.storeapp.data.model.search.FilterModel
import com.softanime.storeapp.databinding.ItemFilterBinding
import com.softanime.storeapp.utils.base.BaseDiffUtils
import javax.inject.Inject

class FilterAdapter @Inject constructor() : RecyclerView.Adapter<FilterAdapter.ViewHolder>() {

    private var items = emptyList<FilterModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = position

    override fun getItemId(position: Int) = position.toLong()

    inner class ViewHolder(private val binding: ItemFilterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FilterModel) {
            binding.apply {
                itemTitle.text = item.faName
                //Click
                root.setOnClickListener { onItemClickListener?.let { it(item.enName) } }
            }
        }
    }

    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }

    fun setData(data: List<FilterModel>) {
        val adapterDiffUtils = BaseDiffUtils(items, data)
        val diffUtils = DiffUtil.calculateDiff(adapterDiffUtils)
        items = data
        diffUtils.dispatchUpdatesTo(this)
    }
}