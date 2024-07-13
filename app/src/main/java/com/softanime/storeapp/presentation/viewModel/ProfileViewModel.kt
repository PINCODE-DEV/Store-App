package com.softanime.storeapp.presentation.viewModel

import academy.nouri.storeapp.data.models.login.BodyLogin
import academy.nouri.storeapp.data.models.login.ResponseLogin
import academy.nouri.storeapp.data.models.profile.ResponseProfile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softanime.storeapp.data.repository.ProfileRepo
import com.softanime.storeapp.utils.network.NetworkRequest
import com.softanime.storeapp.utils.network.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repo: ProfileRepo) : ViewModel(){

    init {
        viewModelScope.launch {
            delay(300)
            callProfileApi()
        }
    }

    // Profile Data
    private val _profileData = MutableLiveData<NetworkRequest<ResponseProfile>>()
    val profileData: LiveData<NetworkRequest<ResponseProfile>> = _profileData

    // Profile
    fun callProfileApi() = viewModelScope.launch {
        // Loading
        _profileData.value = NetworkRequest.Loading()
        val response = repo.getProfile()
        // Response
        _profileData.value = NetworkResponse(response).generalResponse()
    }
}