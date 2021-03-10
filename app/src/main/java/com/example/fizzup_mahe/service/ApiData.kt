package com.example.fizzup_mahe.service

import com.example.fizzup_mahe.model.Exercise
import com.google.gson.annotations.SerializedName

data class ApiData(
    @SerializedName("data")
    val data: List<Exercise>
)

