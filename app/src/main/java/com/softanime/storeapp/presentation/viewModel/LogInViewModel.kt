package com.softanime.storeapp.presentation.viewModel

import academy.nouri.storeapp.data.models.login.BodyLogin
import academy.nouri.storeapp.data.models.login.ResponseLogin
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softanime.storeapp.data.repository.LogInRepo
import com.softanime.storeapp.utils.network.NetworkRequest
import com.softanime.storeapp.utils.network.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(private val repo: LogInRepo) : ViewModel() {

    // Login
    private val _loginData = MutableLiveData<NetworkRequest<ResponseLogin>>()
    val loginData: LiveData<NetworkRequest<ResponseLogin>> = _loginData
    fun callLogInApi(body: BodyLogin) = viewModelScope.launch {
        // Loading
        _loginData.value = NetworkRequest.Loading()
        val response = repo.postLogin(body)
        // Response
        _loginData.value = NetworkResponse(response).generalResponse()
    }
}