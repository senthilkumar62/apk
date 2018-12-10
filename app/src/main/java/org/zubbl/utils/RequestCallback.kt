package org.zubbl.utils

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import okhttp3.ResponseBody
import org.zubbl.R
import org.zubbl.application.AppController
import org.zubbl.interfaces.ServiceListener
import org.zubbl.model.common.JsonResp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class RequestCallback : Callback<ResponseBody> {

    private var listener: ServiceListener? = null
    private var requestCode = 0
    private var requestData = ""

    @Inject
    lateinit var jsonResp: JsonResp
    @Inject
    lateinit var context: Context

    constructor() {
        AppController.appComponent.inject(this)
    }

    constructor(listener: ServiceListener) {
        AppController.appComponent.inject(this)
        this.listener = listener
    }

    constructor(requestCode: Int, listener: ServiceListener) {
        AppController.appComponent.inject(this)
        this.listener = listener
        this.requestCode = requestCode
    }

    constructor(requestCode: Int, listener: ServiceListener, requestData: String) {
        AppController.appComponent.inject(this)
        this.listener = listener
        this.requestCode = requestCode
        this.requestData = requestData
    }

    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        this.listener!!.onSuccess(getSuccessResponse(call, response), requestData)
    }

    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        this.listener!!.onFailure(getFailureResponse(call, t), requestData)
    }

    private fun getFailureResponse(call: Call<ResponseBody>?, t: Throwable): JsonResp {
        try {
            jsonResp.clearAll()
            if (call?.request() != null) {
                jsonResp.method = call.request().method()
                jsonResp.requestCode = requestCode
                jsonResp.strRequest = call.request().toString()
                jsonResp.url = call.request().url().toString()
                Log.e("API Request ", call.request().toString())
            }
            jsonResp.isOnline = isOnline()
            jsonResp.errorMsg = t.message.toString()
            requestData = if (!isOnline()) context.resources.getString(R.string.network_failure) else t.message.toString()
            Log.e("API Fail Msg ", t.message!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return jsonResp
    }

    private fun getSuccessResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?): JsonResp {
        try {
            jsonResp.clearAll()
            if (call?.request() != null) {
                jsonResp.method = call.request().method()
                jsonResp.requestCode = requestCode
                jsonResp.strRequest = call.request().toString()
                jsonResp.url = call.request().url().toString()
                Log.e("API Request", call.request().toString())
            }
            if (response != null) {
                jsonResp.headers=response.raw().headers().toMultimap()
                jsonResp.responseCode = response.code()
                Log.e("API Resp Code ", response.code().toString())
                if (response.isSuccessful && response.body() != null) {
                    val strJson = response.body()?.string()
                    jsonResp.strResponse = strJson.toString()
                    Log.e("API Response ", strJson)
                } else {
                    jsonResp.isOnline = isOnline()
                    jsonResp.errorMsg = response.errorBody()?.string().toString()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return jsonResp
    }

    private fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectivityManager.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }
}
