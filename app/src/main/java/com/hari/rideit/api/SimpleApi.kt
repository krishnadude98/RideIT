package com.hari.rideit.api


import com.hari.rideit.model.DefaultResponse
import okhttp3.*
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Headers

interface SimpleApi {

 @Multipart
 @POST("add")
 suspend fun postImage( @Part("userid")userid:RequestBody, @Part("licenseno")licenseno:RequestBody,
                       @Part("licensetype")licensetype:RequestBody, @Part image:MultipartBody.Part,
                       @Part("lat")lat:RequestBody, @Part("long") long:RequestBody,@Header("auth-token")auth:String):Response<DefaultResponse>




}