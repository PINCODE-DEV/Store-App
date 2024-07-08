package com.softanime.storeapp.di

import academy.nouri.storeapp.data.models.login.BodyLogin
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object FragmentModule {
@Provides
fun bodyLogIn() = BodyLogin()
}