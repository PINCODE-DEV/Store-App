package com.softanime.storeapp.data.repository

import com.softanime.storeapp.data.network.ApiServices
import javax.inject.Inject

class HomeRepo @Inject constructor(private val api: ApiServices) {
    suspend fun getBannersList(slug: String) = api.getBannersList(slug)
    suspend fun getDiscountList(slug: String) = api.getDiscountList(slug)
    suspend fun getProductsList(slug: String, queries: HashMap<String,String>) = api.getProductsList(slug, queries)
}