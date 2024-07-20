package com.softanime.storeapp.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softanime.storeapp.data.model.search.ResponseSearch
import com.softanime.storeapp.data.repository.SearchRepo
import com.softanime.storeapp.utils.Q
import com.softanime.storeapp.utils.SORT
import com.softanime.storeapp.utils.network.NetworkRequest
import com.softanime.storeapp.utils.network.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val repo: SearchRepo) : ViewModel(){

    // Search Data
    private val _searchData = MutableLiveData<NetworkRequest<ResponseSearch>>()
    val searchData: LiveData<NetworkRequest<ResponseSearch>> = _searchData

    // <---Search--->
    // Queries
    fun searchQueries(search: String, sort: String) : HashMap<String,String>{
        val queries = HashMap<String,String>()
        queries[Q] = search
        queries[SORT] = sort
        return queries
    }

    fun callSearchApi(queries : Map<String,String>) = viewModelScope.launch {
        // Loading
        _searchData.value = NetworkRequest.Loading()
        val response = repo.getSearchList(queries)
        // Response
        _searchData.value = NetworkResponse(response).generalResponse()
    }
}