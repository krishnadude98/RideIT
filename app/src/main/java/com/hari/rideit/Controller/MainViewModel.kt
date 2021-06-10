package com.hari.rideit.Controller

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hari.rideit.model.DefaultResponse
import com.hari.rideit.repository.Repository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class MainViewModel(private val repository: Repository): ViewModel() {
    val myResponse:MutableLiveData<Response<DefaultResponse>> = MutableLiveData()
    fun putPost(userid: RequestBody, licenseno: RequestBody, licensetype: RequestBody, image: MultipartBody.Part, lat: RequestBody, long: RequestBody,auth:String){
        viewModelScope.launch {
            val response:Response<DefaultResponse> =repository.putPost(userid,licenseno,licensetype,image,lat,long,auth)
            myResponse.value= response
        }
    }
}