package org.zubbl.service

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface ApiService {



    ///// LOGIN /////
    @FormUrlEncoded
    @POST("login")
    fun login(@FieldMap parameters: Map<String, String>): Call<ResponseBody>

    ///// REGISTER ////
    @FormUrlEncoded
    @POST("signup")
    fun register(@FieldMap parameters: HashMap<String,Any>): Call<ResponseBody>

    ///// PASSWORD //////
    @FormUrlEncoded
    @POST("forgotpassword")
    fun sendOTP(@FieldMap parameters: Map<String, String>): Call<ResponseBody>

    ///// PASSWORD //////
    @FormUrlEncoded
    @POST("changepassword")
    fun changePassword(@Query("activation_password") paramname:String, @FieldMap parameters: Map<String, String>): Call<ResponseBody>

    ///// NEAR BY ////
    @FormUrlEncoded
    @POST("nearBy")
    fun nearBy(@FieldMap parameters: Map<String, Double>): Call<ResponseBody>

    ///// MERCHANT DETAILS ////
    @FormUrlEncoded
    @POST("merchantDetails")
    fun merchantDetails(@FieldMap parameters: Map<String, String>): Call<ResponseBody>

    ///// JOIN ZUBBLE ////
    @FormUrlEncoded
    @POST("joinZubbl")
    fun joinZubble(@FieldMap parameters: Map<String, String>): Call<ResponseBody>

    ///// ADD ZUBBLE ////
    @FormUrlEncoded
    @POST("addZubbl")
    fun addZubbl(@FieldMap parameters: Map<String, String>): Call<ResponseBody>

    ////LEADERBOARD DATA /////
    @GET("getLeaderBoardList")
    fun getLeaderBoardDetails(@QueryMap parameters: Map<String, String>): Call<ResponseBody>

    /////MY FEED DATA/////
    @GET("myFeed")
    fun getMyFeedData(@QueryMap parameters: Map<String, String>): Call<ResponseBody>



}