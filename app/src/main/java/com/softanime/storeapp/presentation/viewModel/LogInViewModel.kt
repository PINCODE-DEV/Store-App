package com.softanime.storeapp.presentation.viewModel

import academy.nouri.storeapp.data.models.login.BodyLogin
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softanime.storeapp.data.repository.LogInRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(private val repo: LogInRepo) : ViewModel() {
    fun postLogin(body: BodyLogin) = viewModelScope.launch {
        repo.postLogin(body)
    }
}