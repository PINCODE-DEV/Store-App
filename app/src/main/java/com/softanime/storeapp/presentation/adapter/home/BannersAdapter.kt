package com.softanime.storeapp.presentation.adapter.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.softanime.storeapp.data.model.home.ResponseBanners.ResponseBannersItem
import com.softanime.storeapp.databinding.ItemBannersBinding
import com.softanime.storeapp.utils.BASE_URL_IMAGE_WITH_STORAGE
import com.softanime.storeapp.utils.base.BaseDiffUtils
import com.softanime.storeapp.utils.extensions.loadImage
import javax.inject.Inject

class BannersAdapter @Inject constructor() : RecyclerView.Adapter<BannersAdapter.ViewHolder>() {
    // data
    private var banners = emptyList<ResponseBannersItem>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannersAdapter.ViewHolder {
        val binding = ItemBannersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannersAdapter.ViewHolder, position: Int) =
        holder.bind(banners[position])

    override fun getItemCount(): Int = banners.size

    override fun getItemViewType(position: Int): Int = position

    override fun getItemId(position: Int) = position.toLong()

    inner class ViewHolder(private val binding: ItemBannersBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResponseBannersItem) {
            binding.apply {
                itemTitle.text = item.title
                val imageUrl = "${BASE_URL_IMAGE_WITH_STORAGE}${item.image}"
                itemImg.loadImage(imageUrl)

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

    fun setData(data: List<ResponseBannersItem>) {
        val adapterDiffUtils = BaseDiffUtils(banners, data)
        val diffUtils = DiffUtil.calculateDiff(adapterDiffUtils)
        banners = data
        diffUtils.dispatchUpdatesTo(this)
    }
}