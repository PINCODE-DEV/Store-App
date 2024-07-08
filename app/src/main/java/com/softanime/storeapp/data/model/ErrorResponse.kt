package com.softanime.storeapp.data.model
import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("message")
    val message: String?, // 09120174757
    @SerializedName("errors")
    val errors: Map<String,List<String>>?
)