package com.softanime.storeapp.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softanime.storeapp.data.model.home.ResponseProducts
import com.softanime.storeapp.data.repository.CategoryProductRepository
import com.softanime.storeapp.data.repository.SearchFilterRepository
import com.softanime.storeapp.utils.MAX_PRICE
import com.softanime.storeapp.utils.MIN_PRICE
import com.softanime.storeapp.utils.ONLY_AVAILABLE
import com.softanime.storeapp.utils.SEARCH
import com.softanime.storeapp.utils.SELECTED_BRANDS
import com.softanime.storeapp.utils.SORT
import com.softanime.storeapp.utils.network.NetworkRequest
import com.softanime.storeapp.utils.network.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryProductViewModel @Inject constructor(
    private val repository: CategoryProductRepository,
    private val filterRepository: SearchFilterRepository
) : ViewModel() {
    //Products
    fun productsQueries(
        sort: String? = null,
        search: String? = null,
        minPrice: String? = null,
        maxPrice: String? = null,
        brands: String? = null,
        available: Boolean? = null
    ): Map<String, String> {

        val queries: HashMap<String, String> = HashMap()
        if (sort != null)
            queries[SORT] = sort
        if (search != null)
            queries[SEARCH] = search
        if (minPrice != null)
            queries[MIN_PRICE] = minPrice
        if (maxPrice != null)
            queries[MAX_PRICE] = maxPrice
        if (brands != null)
            queries[SELECTED_BRANDS] = brands
        if (available != null)
            queries[ONLY_AVAILABLE] = available.toString()
        return queries
    }

    private val _productData = MutableLiveData<NetworkRequest<ResponseProducts>>()
    val productData: LiveData<NetworkRequest<ResponseProducts>> = _productData

    fun callProductsApi(slug: String, queries: Map<String, String>) = viewModelScope.launch {
        _productData.value = NetworkRequest.Loading()
        val response = repository.getProductsList(slug, queries)
        _productData.value = NetworkResponse(response).generalResponse()
    }
}