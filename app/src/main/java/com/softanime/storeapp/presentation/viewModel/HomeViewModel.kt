package com.softanime.storeapp.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softanime.storeapp.data.model.home.ResponseBanners
import com.softanime.storeapp.data.model.home.ResponseDiscount
import com.softanime.storeapp.data.repository.HomeRepo
import com.softanime.storeapp.utils.ELECTRONIC_DEVICES
import com.softanime.storeapp.utils.GENERAL
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
}