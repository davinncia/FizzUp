package com.example.fizzup_mahe.service

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("fizzup/files/public/sample.json")
    fun getAll(): Call<ApiData>

}