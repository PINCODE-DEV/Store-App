package com.softanime.storeapp.presentation.viewModel

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.softanime.storeapp.data.model.home.ProductsCategories
import com.softanime.storeapp.data.model.home.ResponseBanners
import com.softanime.storeapp.data.model.home.ResponseDiscount
import com.softanime.storeapp.data.repository.HomeRepo
import com.softanime.storeapp.utils.ELECTRONIC_DEVICES
import com.softanime.storeapp.utils.GENERAL
import com.softanime.storeapp.utils.NEW
import com.softanime.storeapp.utils.SORT
import com.softanime.storeapp.utils.network.NetworkRequest
import com.softanime.storeapp.utils.network.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repo: HomeRepo) : ViewModel() {

    init {
        viewModelScope.launch {
            delay(300)
            callBannersApi()
            callDiscountApi()
        }
    }

    // Save Latest Scroll State
    var latestScrollState : Parcelable ?= null
    var latestRecyclerState : Parcelable ?= null

    // Banners Data
    private val _bannersData = MutableLiveData<NetworkRequest<ResponseBanners>>()
    val bannersData: LiveData<NetworkRequest<ResponseBanners>> = _bannersData

    // Discount Data
    private val _discountData = MutableLiveData<NetworkRequest<ResponseDiscount>>()
    val discountData: LiveData<NetworkRequest<ResponseDiscount>> = _discountData

    // Get Banners
    private fun callBannersApi() = viewModelScope.launch {
        // Loading
        _bannersData.value = NetworkRequest.Loading()
        val response = repo.getBannersList(GENERAL)
        // Response
        _bannersData.value = NetworkResponse(response).generalResponse()
    }

    // Get Discount
    private fun callDiscountApi() = viewModelScope.launch {
        // Loading
        _discountData.value = NetworkRequest.Loading()
        val response = repo.getDiscountList(ELECTRONIC_DEVICES)
        // Response
        _discountData.value = NetworkResponse(response).generalResponse()
    }

    //<--Products-->

    private fun productQueries() : HashMap<String,String>{
        val queries = HashMap<String,String>()
        queries[SORT] = NEW
        return queries
    }

    private fun callProductsApi(category: ProductsCategories) = liveData {
        val cat = category.item
        emit(NetworkRequest.Loading())
        val response = repo.getProductsList(cat,productQueries())
        emit(NetworkResponse(response).generalResponse())
    }

    private val categoriesName = ProductsCategories.values()
        .associateWith {
            callProductsApi(it)
        }

    fun getProductsData(category : ProductsCategories) = categoriesName.getValue(category)
}