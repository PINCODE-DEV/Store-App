package com.softanime.storeapp.data.network

import academy.nouri.storeapp.data.models.login.BodyLogin
import academy.nouri.storeapp.data.models.login.ResponseLogin
import academy.nouri.storeapp.data.models.profile.ResponseProfile
import com.softanime.storeapp.data.model.home.ResponseBanners
import com.softanime.storeapp.data.model.home.ResponseDiscount
import com.softanime.storeapp.data.model.login.ResponseVerify
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiServices {
    @POST("auth/login")
    suspend fun postLogin(@Body body: BodyLogin): Response<ResponseLogin>

    @POST("auth/login/verify")
    suspend fun postLoginVerify(@Body body: BodyLogin): Response<ResponseVerify>

    @GET("auth/detail")
    suspend fun getProfileData(): Response<ResponseProfile>

    @GET("ad/swiper/{slug}")
    suspend fun getBannersList(@Path("slug") slug: String): Response<ResponseBanners>

    @GET("offers/discount/{slug}")
    suspend fun getDiscountList(@Path("slug") slug: String): Response<ResponseDiscount>

}