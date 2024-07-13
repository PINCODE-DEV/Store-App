package com.softanime.storeapp.di

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.softanime.storeapp.data.network.ApiServices
import com.softanime.storeapp.data.stored.SessionManager
import com.softanime.storeapp.utils.ACCEPT
import com.softanime.storeapp.utils.APPLICATION_JSON
import com.softanime.storeapp.utils.AUTHORIZATION
import com.softanime.storeapp.utils.BASE_URL
import com.softanime.storeapp.utils.BEARER
import com.softanime.storeapp.utils.CONNECTION_TIMEOUT
import com.softanime.storeapp.utils.NAMED_PING
import com.softanime.storeapp.utils.PING_INTERVAL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, baseUrl: String, gson: Gson): ApiServices =
        Retrofit.Builder()
            .client(client)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiServices::class.java)

    @Provides
    @Singleton
    fun provideBaseUrl() = BASE_URL

    @Provides
    @Singleton
    fun provideTimeout() = CONNECTION_TIMEOUT

    @Provides
    @Singleton
    @Named(NAMED_PING)
    fun providePingInterval() = PING_INTERVAL

    @Provides
    @Singleton
    fun provideClient(
        timeout: Long,
        @Named(NAMED_PING) pingInterval: Long,
        interceptor: Interceptor,
        session: SessionManager
    ): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(timeout, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val token = runBlocking {
                session.getToken.first().toString()
            }
            Log.i("LOG", "Header : $token")
            chain.proceed(chain.request().newBuilder().also {
                it.addHeader(AUTHORIZATION, "$BEARER $token")
                it.addHeader(ACCEPT, APPLICATION_JSON)
            }.build())
        }.also {
            it.addInterceptor(interceptor)
        }
        .readTimeout(timeout, TimeUnit.SECONDS)
        .writeTimeout(timeout, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .pingInterval(pingInterval, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().setLenient().create()

    @Provides
    @Singleton
    fun provideInterceptor(): Interceptor = HttpLoggingInterceptor().apply {
        //level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        level = HttpLoggingInterceptor.Level.BODY
    }
}
