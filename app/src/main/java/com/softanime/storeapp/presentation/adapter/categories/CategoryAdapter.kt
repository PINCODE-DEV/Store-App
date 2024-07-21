package com.softanime.storeapp.presentation.adapter.categories

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.softanime.storeapp.data.model.categories.ResponseCategories.ResponseCategoriesItem
import com.softanime.storeapp.data.model.categories.ResponseCategories.ResponseCategoriesItem.SubCategory
import com.softanime.storeapp.databinding.ItemCategoriesBinding
import com.softanime.storeapp.utils.base.BaseDiffUtils
import com.softanime.storeapp.utils.extensions.setup
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CategoryAdapter @Inject constructor(@ApplicationContext private val context: Context) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var items = emptyList<ResponseCategoriesItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = position

    override fun getItemId(position: Int) = position.toLong()

    inner class ViewHolder(private val binding: ItemCategoriesBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: ResponseCategoriesItem) {
            binding.apply {
                itemTitle.text = item.title
                //Sub category
                if (item.subCategories!!.isNotEmpty()) {
                    itemSubCatsList.isVisible = true
                    initSubCategoriesRecycler(item.subCategories, binding)
                } else {
                    itemSubCatsList.isVisible = false
                }
            }
        }
    }

    private fun initSubCategoriesRecycler(list: List<SubCategory>, binding: ItemCategoriesBinding) {
        val subCategoryAdapter = SubCategoryAdapter()
        subCategoryAdapter.setData(list)
        binding.itemSubCatsList.setup(
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true), subCategoryAdapter
        )
        //Send slug
        subCategoryAdapter.getSlug { slug->
            onItemClickListener?.let { it(slug) }
        }
    }

    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }

    fun setData(data: List<ResponseCategoriesItem>) {
        val adapterDiffUtils = BaseDiffUtils(items, data)
        val diffUtils = DiffUtil.calculateDiff(adapterDiffUtils)
        items = data
        diffUtils.dispatchUpdatesTo(this)
    }
}