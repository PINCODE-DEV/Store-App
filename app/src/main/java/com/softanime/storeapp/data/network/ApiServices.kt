package com.softanime.storeapp.data.network

import academy.nouri.storeapp.data.models.login.BodyLogin
import academy.nouri.storeapp.data.models.login.ResponseLogin
import com.softanime.storeapp.data.model.login.ResponseVerify
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiServices {
    @POST("auth/login")
    suspend fun postLogin(@Body body: BodyLogin): Response<ResponseLogin>

    @POST("auth/login/verify")
    suspend fun postLoginVerify(@Body body: BodyLogin): Response<ResponseVerify>
}