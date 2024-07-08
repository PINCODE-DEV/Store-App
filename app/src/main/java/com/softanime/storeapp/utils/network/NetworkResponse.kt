package com.softanime.storeapp.utils.network

import com.google.gson.Gson
import com.softanime.storeapp.data.model.ErrorResponse
import retrofit2.Response

open class NetworkResponse<T>(private val response: Response<T>) {
    open fun generalResponse(): NetworkRequest<T> {
        return when {
            response.code() == 401 -> NetworkRequest.Error("You are not auth!")

            response.code() == 422 -> {
                var errorMessage = ""
                if (response.errorBody() != null) {
                    val errorResponse = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        ErrorResponse::class.java
                    )
                    val message = errorResponse.message
                    val errors = errorResponse.errors

                    errors?.forEach { (field, fieldError) ->
                        errorMessage = fieldError.joinToString()
                    }
                }
                NetworkRequest.Error(errorMessage)
            }

            response.code() == 500 -> NetworkRequest.Error("Try again!")

            response.isSuccessful -> NetworkRequest.Success(response.body())

            else -> NetworkRequest.Error(response.message())
        }
    }
}