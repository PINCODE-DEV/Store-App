package com.softanime.storeapp.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softanime.storeapp.data.model.categories.ResponseCategories
import com.softanime.storeapp.data.repository.CategoriesRepository
import com.softanime.storeapp.utils.network.NetworkRequest
import com.softanime.storeapp.utils.network.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(private val repository: CategoriesRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            delay(300)
            callCategoriesApi()
        }
    }

    //Categories
    private val _categoriesData = MutableLiveData<NetworkRequest<ResponseCategories>>()
    val categoriesData: LiveData<NetworkRequest<ResponseCategories>> = _categoriesData

    private fun callCategoriesApi() = viewModelScope.launch {
        _categoriesData.value = NetworkRequest.Loading()
        val response = repository.getCategoriesList()
        _categoriesData.value = NetworkResponse(response).generalResponse()
    }
}