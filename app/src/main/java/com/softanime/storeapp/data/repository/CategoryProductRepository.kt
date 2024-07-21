package com.softanime.storeapp.data.repository

import com.softanime.storeapp.data.network.ApiServices
import javax.inject.Inject

class CategoryProductRepository @Inject constructor(private val api: ApiServices) {
    suspend fun getProductsList(slug: String, queries: Map<String, String>) = api.getProductsList(slug, queries)
}