package com.hari.rideit.repository

import com.android.volley.Response
import com.hari.rideit.api.RetrofitClient
import com.hari.rideit.model.DefaultResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class Repository {
    suspend fun putPost(userid:RequestBody,licenseno:RequestBody,licensetype:RequestBody,image: MultipartBody.Part,lat:RequestBody,long:RequestBody,auth:String):retrofit2.Response<DefaultResponse>{
        return RetrofitClient.api.postImage(userid,licenseno,licensetype,image,lat,long,auth)
    }
}