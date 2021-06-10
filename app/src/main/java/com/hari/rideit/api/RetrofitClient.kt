package com.hari.rideit.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient{

        private const val BASE_URL="http://ec2-3-19-240-6.us-east-2.compute.amazonaws.com:3005/v1/rentrider/"
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        }

    val api:SimpleApi by lazy {
        retrofit.create(SimpleApi::class.java)

    }



}